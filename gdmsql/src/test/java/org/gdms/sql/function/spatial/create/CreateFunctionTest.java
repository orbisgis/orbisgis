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
package org.gdms.sql.function.spatial.create;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.driver.DataSet;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.types.Type;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.junit.Test;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.create.ST_CreateGrid;
import org.gdms.sql.function.spatial.geometry.create.ST_CreatePointsGrid;
import org.gdms.sql.function.spatial.geometry.create.ST_MakeEnvelope;
import org.gdms.sql.function.spatial.geometry.create.ST_RemoveDuplicateCoordinate;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

public class CreateFunctionTest extends FunctionTest {

        /**
         * Test the make envelope function
         * @throws Exception
         */
        @Test
        public void testST_MakeEnvelope() throws Exception {

                ST_MakeEnvelope st_MakeEnvelope = new ST_MakeEnvelope();
                Envelope env = JTSPolygon2D.getEnvelopeInternal();
                Value[] values = new Value[]{ValueFactory.createValue(env.getMinX()), ValueFactory.createValue(env.getMinY()), ValueFactory.createValue(env.getMaxX()), ValueFactory.createValue(env.getMaxY())};
                Value result = evaluate(st_MakeEnvelope, values);
                assertEquals(env.getMinX(), result.getAsGeometry().getEnvelopeInternal().getMinX(), 0);
                assertEquals(env.getMinY(), result.getAsGeometry().getEnvelopeInternal().getMinY(), 0);
                assertEquals(env.getMaxX(), result.getAsGeometry().getEnvelopeInternal().getMaxX(), 0);
                assertEquals(env.getMaxY(), result.getAsGeometry().getEnvelopeInternal().getMaxY(), 0);
        }

        /**
         * Remove repeated coordinates.
         * @throws Exception
         */
        @Test
        public void testRemoveDuplicateCoordinate() throws Exception {
                ST_RemoveDuplicateCoordinate sT_RemoveRepeatedPoints = new ST_RemoveDuplicateCoordinate();
                Value[] values = new Value[]{ValueFactory.createValue(wktReader.read("LINESTRING(0 0, 1 0, 1 0, 2 10, 0 0 )"))};
                Value result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertFalse(CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

                values = new Value[]{ValueFactory.createValue(wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))"))};
                result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertFalse(CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

                values = new Value[]{ValueFactory.createValue(wktReader.read("POLYGON (( 155 186, 155 282, 276 282, 276 282, 276 186, 155 186 ), ( 198 253, 198 253, 198 218, 198 218, 244 222, 239 243, 198 253 ))"))};
                result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertFalse(CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

        }

        /**
         * Test the ST_CreatePointsGrid with a mask.
         * @throws Exception
         */
        @Test
        public void testST_CreatePointsGridMask() throws Exception {
                ST_CreatePointsGrid sT_CreatePointsGrid = new ST_CreatePointsGrid();
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});

                Value[] values = new Value[]{ValueFactory.createValue(1), ValueFactory.createValue(1), ValueFactory.createValue(true)};

                DataSet[] tables = new DataSet[]{driver1};
                DataSet result = sT_CreatePointsGrid.evaluate(dsf, tables, values, new NullProgressMonitor());

                for (int i = 0; i < result.getRowCount(); i++) {
                        Geometry geom = result.getGeometry(i, 0);
                        assertTrue(polygon.contains(geom));
                }
        }

        /**
         * Test to create a regular square grid
         * @throws Exception
         */
        @Test
        public void testST_CreateSquareGRID() throws Exception {
                ST_CreateGrid sT_CreateGrid = new ST_CreateGrid();
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});

                Value[] values = new Value[]{ValueFactory.createValue(1), ValueFactory.createValue(1)};

                DataSet[] tables = new DataSet[]{driver1};
                DataSet result = sT_CreateGrid.evaluate(dsf, tables, values, new NullProgressMonitor());
                checkGrid(result, true);
        }

        /**
         * Test to create an oriented regular square grid
         * @throws Exception
         */
        @Test
        public void testST_CreateOrientedSquareGRID() throws Exception {
                ST_CreateGrid sT_CreateGrid = new ST_CreateGrid();
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});

                Value[] values = new Value[]{ValueFactory.createValue(1), ValueFactory.createValue(1), ValueFactory.createValue(0)};

                DataSet[] tables = new DataSet[]{driver1};
                DataSet result = sT_CreateGrid.evaluate(dsf, tables, values, new NullProgressMonitor());
                checkGrid(result, false);
        }

        /**
         * Test to create an oriented regular square grid
         * @throws Exception
         */
        @Test
        public void testST_CreateOrientedSquareGRID2() throws Exception {
                ST_CreateGrid sT_CreateGrid = new ST_CreateGrid();
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});

                Value[] values = new Value[]{ValueFactory.createValue(1), ValueFactory.createValue(1), ValueFactory.createValue(90)};

                DataSet[] tables = new DataSet[]{driver1};
                DataSet result = sT_CreateGrid.evaluate(dsf, tables, values, new NullProgressMonitor());
                checkGrid(result, false);
        }

        private void checkGrid(final DataSet dataSource, final boolean checkCentroid)
                throws Exception {
                final long rowCount = dataSource.getRowCount();
                double minX = dataSource.getFullExtent().getMinX();
                double maxY = dataSource.getFullExtent().getMaxY();
                
                for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                        final Value[] fields = dataSource.getRow(rowIndex);
                        final Geometry geom = fields[0].getAsGeometry();
                        final int id_col = fields[2].getAsInt();
                        final int id_row = fields[3].getAsInt();
                        assertTrue(geom instanceof Polygon);
                        assertTrue(Math.abs(1 - geom.getArea()) < 0.000001);
                        if (checkCentroid) {                                
                                assertEquals((minX  + 0.5) +(id_col-1), geom.getCentroid().getCoordinate().x, 0);
                                assertEquals((maxY - 0.5) - (id_row-1), geom.getCentroid().getCoordinate().y, 0);
                        }

                        System.out.println();
                }
        }
}
