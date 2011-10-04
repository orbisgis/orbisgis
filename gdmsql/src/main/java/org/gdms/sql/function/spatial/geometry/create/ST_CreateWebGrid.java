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

import org.gdms.data.AlreadyClosedException;
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
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_CreateWebGrid extends AbstractTableFunction {

        private static final double DPI = 2 * Math.PI;
        private final GeometryFactory gf = new GeometryFactory();
        private static final Logger LOG = Logger.getLogger(ST_CreateWebGrid.class);

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        final double deltaR = values[0].getAsDouble();
                        final double deltaT = values[1].getAsDouble();

                        final DataSet inSds = tables[0];

                        // built the driver for the resulting datasource and register it...
                        final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                                getMetadata(null));
                        final Envelope envelope = DriverUtilities.getFullExtent(inSds);
                        createGrid(driver, envelope, deltaR, deltaT, pm);

                        return driver.getTable("main");
                } catch (AlreadyClosedException e) {
                        throw new FunctionException(e);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                }
        }

        @Override
        public String getName() {
                return "ST_CreateWebGrid";
        }

        @Override
        public String getDescription() {
                return "Calculate a regular grid that may be optionnaly oriented";
        }

        @Override
        public String getSqlOrder() {
                return "select * " + getName() + "(table, 4000,1000);";
        }

        private void createGrid(final MemoryDataSetDriver driver,
                final Envelope env, double deltaR, double deltaT,
                final ProgressMonitor pm) throws DriverException {
                final double r = 0.5 * Math.sqrt(env.getWidth() * env.getWidth()
                        + env.getHeight() * env.getHeight());
                final Coordinate centroid = env.centre();
                final double perimeter = DPI * r;
                final int nr = (int) Math.ceil(r / deltaR);
                deltaR = r / nr; // TODO : to be comment
                final int nt = (int) Math.ceil(perimeter / (2 * deltaT));
                deltaT = DPI / nt;
                pm.startTask("Creating grid", nt);

                int gridCellIndex = 0;
                for (int t = 0; t < nt; t++) {

                        if (t >= 100 && t % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo((100 * t / nt));
                                }
                        }

                        for (int i = 0; i < nr; i++) {
                                createGridCell(driver, centroid, i, t, gridCellIndex, deltaR,
                                        deltaT);
                                gridCellIndex++;
                        }
                }
                pm.progressTo(nt);
                pm.endTask();
        }

        private void createGridCell(final MemoryDataSetDriver driver,
                final Coordinate centroid, final int r, final int t,
                final int gridCellIndex, final double deltaR, final double deltaT) {
                final Coordinate[] summits = new Coordinate[5];
                summits[0] = polar2cartesian(centroid, r, t, deltaR, deltaT);
                summits[1] = polar2cartesian(centroid, r + 1, t, deltaR, deltaT);
                summits[2] = polar2cartesian(centroid, r + 1, t + 1, deltaR, deltaT);
                summits[3] = polar2cartesian(centroid, r, t + 1, deltaR, deltaT);
                summits[4] = summits[0];
                createGridCell(driver, summits, gridCellIndex);
        }

        private Coordinate polar2cartesian(final Coordinate centroid, final int r,
                final int t, final double deltaR, final double deltaT) {
                final double rr = r * deltaR;
                final double tt = t * deltaT;
                return new Coordinate(centroid.x + rr * Math.cos(tt), centroid.y + rr
                        * Math.sin(tt));
        }

        private void createGridCell(final MemoryDataSetDriver driver,
                final Coordinate[] summits, final int gridCellIndex) {
                final LinearRing g = gf.createLinearRing(summits);
                final Geometry gg = gf.createPolygon(g, null);
                driver.addValues(new Value[]{ValueFactory.createValue(gg),
                                ValueFactory.createValue(gridCellIndex)});
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
