/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
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
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_CreatePointsGrid extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private double deltaX;
        private double deltaY;
        private static final Logger LOG = Logger.getLogger(ST_CreatePointsGrid.class);

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        deltaX = values[0].getAsDouble();
                        deltaY = values[1].getAsDouble();
                        final DataSet inSds = tables[0];

                        // built the driver for the resulting datasource and register it...
                        final DiskBufferDriver driver = new DiskBufferDriver(dsf,
                                getMetadata(null));

                        createGrid(driver, DriverUtilities.getFullExtent(inSds), pm);

                        driver.writingFinished();
                        driver.start();
                        return driver.getTable("main");
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }
        }

        @Override
        public String getName() {
                return "ST_CreatePointsGrid";
        }

        @Override
        public String getDescription() {
                return "Calculate a regular points grid. Use a geometry to exclude some area.";
        }

        @Override
        public String getSqlOrder() {
                return "select " + getName() + "(4000,1000) from myTable;";
        }

        private void createGrid(final DiskBufferDriver driver, final Envelope env,
                final ProgressMonitor pm) throws DriverException {
                final int nbX = (int) Math.ceil((env.getMaxX() - env.getMinX()
				/ deltaX));
                pm.startTask("Creating grid", nbX);
		final int nbY = (int) Math.ceil((env.getMaxY() - env.getMinY()
				/ deltaY));
                int gridCellIndex = 0;
                double x = env.centre().x - (deltaX * nbX) / 2;
                for (int i = 0; i < nbX; i++, x += deltaX) {

                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }

                        double y = env.centre().y - (deltaY * nbY) / 2;
                        for (int j = 0; j < nbY; j++, y += deltaY) {
                                gridCellIndex++;
                                Geometry g = GF.createPoint(new Coordinate(x, y));
                                driver.addValues(new Value[]{ValueFactory.createValue(g),
                                                ValueFactory.createValue(gridCellIndex)});
                        }
                }
                pm.progressTo(nbX);
                pm.endTask();
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                "gid"});
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE)
                        };
        }
}
