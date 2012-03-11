/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.create;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.apache.log4j.Logger;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_CreateGrid extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private boolean isAnOrientedGrid;
        private double deltaX;
        private double deltaY;
        private double cosAngle;
        private double sinAngle;
        private double cosInvAngle;
        private double sinInvAngle;
        private double llcX;
        private double llcY;
        private static final Logger LOG = Logger.getLogger(ST_CreateGrid.class);
        private DiskBufferDriver driver;

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        final DataSet inSds = tables[0];

                        deltaX = values[0].getAsDouble();
                        deltaY = values[1].getAsDouble();

                        // built the driver for the resulting datasource and register it...
                        driver = new DiskBufferDriver(dsf,
                                getMetadata(null));

                        if (3 == values.length) {
                                isAnOrientedGrid = true;
                                final double angle = (values[2].getAsDouble() * Math.PI) / 180;
                                createGrid(driver, prepareOrientedGrid(inSds, angle), pm);
                        } else {
                                isAnOrientedGrid = false;
                                createGrid(driver, DriverUtilities.getFullExtent(inSds), pm);
                        }

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
                        driver.stop();
                }
        }

        @Override
        public String getName() {
                return "ST_CreateGrid";
        }

        @Override
        public String getDescription() {
                return "Calculate a regular grid that may be optionnaly oriented";
        }

        @Override
        public String getSqlOrder() {
                return "select * from " + getName() + "(table, 4000,1000[,15]);";
        }

        private void createGrid(final DiskBufferDriver driver,
                final Envelope env, final ProgressMonitor pm)
                throws DriverException {

                final int nbX = (int) Math.ceil((env.getMaxX() - env.getMinX())
                        / deltaX);
                pm.startTask("Creating grid", nbX);
                int gridCellIndex = 0;
                int row = 0;
                for (double y = env.getMaxY(); y > env.getMinY(); y -= deltaY) {
                        if (row >= 100 && row % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(row);
                                }
                        }
                        row++;
                        int col = 1;
                        for (double x = env.getMinX(); x < env.getMaxX(); x += deltaX) {
                                gridCellIndex++;
                                final Coordinate[] summits = new Coordinate[5];
                                summits[0] = invTranslateAndRotate(x, y);
                                summits[1] = invTranslateAndRotate(x + deltaX, y);
                                summits[2] = invTranslateAndRotate(x + deltaX, y - deltaY);
                                summits[3] = invTranslateAndRotate(x, y - deltaY);
                                summits[4] = invTranslateAndRotate(x, y);
                                createGridCell(driver, summits, gridCellIndex, col, row);
                                col++;
                        }
                }
                driver.writingFinished();
                driver.start();
                pm.progressTo(nbX);
                pm.endTask();
        }

        private Envelope prepareOrientedGrid(
                final DataSet inSds, final double angle)
                throws DriverException {
                double xMin = Double.MAX_VALUE;
                double xMax = Double.MIN_VALUE;
                double yMin = Double.MAX_VALUE;
                double yMax = Double.MIN_VALUE;

                cosAngle = Math.cos(angle);
                sinAngle = Math.sin(angle);
                cosInvAngle = Math.cos(-angle);
                sinInvAngle = Math.sin(-angle);
                final Envelope env = DriverUtilities.getFullExtent(inSds);
                llcX = env.getMinX();
                llcY = env.getMinY();

                final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(inSds.getMetadata());

                final int rowCount = (int) inSds.getRowCount();
                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                        final Geometry g = inSds.getFieldValue(rowIndex, spatialFieldIndex).getAsGeometry();
                        final Coordinate[] allCoordinates = g.getCoordinates();
                        for (Coordinate inCoordinate : allCoordinates) {
                                final Coordinate outCoordinate = translateAndRotate(inCoordinate);
                                if (outCoordinate.x < xMin) {
                                        xMin = outCoordinate.x;
                                }
                                if (outCoordinate.x > xMax) {
                                        xMax = outCoordinate.x;
                                }
                                if (outCoordinate.y < yMin) {
                                        yMin = outCoordinate.y;
                                }
                                if (outCoordinate.y > yMax) {
                                        yMax = outCoordinate.y;
                                }
                        }
                }
                return new Envelope(xMin, xMax, yMin, yMax);
        }

        private Coordinate translateAndRotate(final Coordinate inCoordinate) {
                // do the rotation after the translation in the local coordinates system
                final double x = inCoordinate.x - llcX;
                final double y = inCoordinate.y - llcY;
                return new Coordinate(cosAngle * x - sinAngle * y, sinAngle * x
                        + cosAngle * y, inCoordinate.z);
        }

        private Coordinate invTranslateAndRotate(final double x,
                final double y) {
                if (isAnOrientedGrid) {
                        // do the (reverse) translation after the (reverse) rotation
                        final double localX = cosInvAngle * x - sinInvAngle * y;
                        final double localY = sinInvAngle * x + cosInvAngle * y;
                        return new Coordinate(localX + llcX, localY + llcY);
                } else {
                        return new Coordinate(x, y);
                }
        }

        private void createGridCell(final DiskBufferDriver driver,
                final Coordinate[] summits, final int gridCellIndex, int col, int row) throws DriverException {
                final LinearRing g = GF.createLinearRing(summits);
                final Geometry gg = GF.createPolygon(g, null);
                driver.addValues(new Value[]{ValueFactory.createValue(gg),
                                ValueFactory.createValue(gridCellIndex),
                                ValueFactory.createValue(col), ValueFactory.createValue(row)});
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.POLYGON),
                                TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                "id", "id_col", "id_row"});
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
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE)
                        };
        }
}
