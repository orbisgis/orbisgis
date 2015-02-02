/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.create;

import com.vividsolutions.jts.algorithm.locate.IndexedPointInAreaLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.log4j.Logger;
import org.orbisgis.commons.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_CreatePointsGrid extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private double deltaX;
        private double deltaY;
        private static final Logger LOG = Logger.getLogger(ST_CreatePointsGrid.class);
        private DiskBufferDriver driver;

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        deltaX = values[0].getAsDouble();
                        deltaY = values[1].getAsDouble();
                        final DataSet inSds = tables[0];

                        // built the driver for the resulting datasource and register it...
                        driver = new DiskBufferDriver(dsf, getMetadata(null));

                        if (values.length == 2) {
                                createGrid(driver, DriverUtilities.getFullExtent(inSds), pm);
                        } else {

                                createPointsInsidePolygon(driver, inSds, values[2].getAsBoolean(), pm);
                        }
                        driver.writingFinished();
                        driver.open();
                        return driver;
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }
        }

        @Override
        public void workFinished() throws DriverException {
                if (driver != null) {
                        driver.close();
                }
        }

        @Override
        public String getName() {
                return "ST_CreatePointsGrid";
        }

        @Override
        public String getDescription() {
                return "Calculate a regular points grid. The grid can be limited to a polygon area.";
        }

        @Override
        public String getSqlOrder() {
                return "select * from " + getName() + "(table, 4000,1000 [, true]);";
        }

        private void createGrid(final DiskBufferDriver driver, final Envelope env,
                final ProgressMonitor pm) throws DriverException {
                final int nbX = (int) Math.ceil((env.getMaxX() - env.getMinX())
                        / deltaX);
                pm.startTask("Creating grid", nbX);
                int gridCellIndex = 0;
                int i = 0;
                for (double x = env.getMinX(); x < env.getMaxX(); x += deltaX) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }

                        for (double y = env.getMinY(); y < env.getMaxY(); y += deltaY) {
                                gridCellIndex++;
                                Geometry g = GF.createPoint(new Coordinate(x, y));
                                driver.addValues(new Value[]{ValueFactory.createValue(g),
                                                ValueFactory.createValue(gridCellIndex)});
                        }
                        i++;
                }
                pm.progressTo(nbX);
                pm.endTask();
        }

        public void createPointsInsidePolygon(final DiskBufferDriver driver, DataSet inSds, boolean mask,
                final ProgressMonitor pm) throws FunctionException, DriverException {
                int gridCellIndex = 0;
                long rowCount = inSds.getRowCount();
                Metadata met = inSds.getMetadata();
                int geomIndex = MetadataUtilities.getGeometryFieldIndex(met);
                int geomDim = MetadataUtilities.getGeometryDimension(met, geomIndex);
                if (geomDim == 2) {
                        if (mask) {
                                for (int i = 0; i < rowCount; i++) {
                                        Geometry geom = inSds.getGeometry(i, geomIndex);
                                        int numGeom = geom.getNumGeometries();
                                        if (numGeom > 1) {
                                                for (int j = 0; j < numGeom; j++) {
                                                        createGeometryGrid(driver, (Polygon) geom.getGeometryN(j), Location.INTERIOR, gridCellIndex, pm);
                                                        gridCellIndex++;
                                                }
                                        } else {
                                                createGeometryGrid(driver, (Polygon) geom.getGeometryN(0), Location.INTERIOR, gridCellIndex, pm);
                                                gridCellIndex++;
                                        }
                                }
                        } else {
                                createGrid(driver, DriverUtilities.getFullExtent(inSds), pm);
                        }
                } else {
                        throw new FunctionException("Only multi or simple polygon are allowed");
                }
        }

        /**
         * Create regular points using a polygon as mask.
         * @param driver
         * @param polygon
         * @param pm
         * @throws DriverException
         */
        private void createGeometryGrid(final DiskBufferDriver driver, final Polygon polygon, int location, int gridCellIndex, final ProgressMonitor pm) throws DriverException {
                IndexedPointInAreaLocator extentLocator = new IndexedPointInAreaLocator(polygon);
                Envelope env = polygon.getEnvelopeInternal();
                pm.startTask("Creating points", 100);
                int i = 0;
                double moduloX = env.getMinX() - (env.getMinX() % deltaX);
                double moduloY = env.getMinY() - (env.getMinY() % deltaY);
                for (double x = moduloX + deltaX; x < env.getMaxX(); x += deltaX) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        for (double y = moduloY + deltaY; y < env.getMaxY(); y += deltaY) {
                                if (extentLocator.locate(new Coordinate(x, y)) == location) {
                                        gridCellIndex++;
                                        Geometry g = GF.createPoint(new Coordinate(x, y));
                                        driver.addValues(new Value[]{ValueFactory.createValue(g),
                                                        ValueFactory.createValue(gridCellIndex)});
                                }
                        }
                        i++;
                }
                pm.endTask();
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.POINT),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                "gid"});
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE),
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE, ScalarArgument.BOOLEAN)
                        };
        }
}
