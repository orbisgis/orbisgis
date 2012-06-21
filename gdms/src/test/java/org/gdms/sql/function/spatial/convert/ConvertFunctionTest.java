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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql.function.spatial.convert;

import org.gdms.driver.DataSet;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.data.types.Dimension3DConstraint;
import org.junit.Test;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.convert.ST_Centroid;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_2D;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_3D;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiLine;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.create.ST_Boundary;
import org.gdms.data.types.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.sql.function.spatial.geometry.convert.ST_EndPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_Explode;
import org.gdms.sql.function.spatial.geometry.convert.ST_Holes;
import org.gdms.sql.function.spatial.geometry.convert.ST_StartPoint;

import org.orbisgis.progress.NullProgressMonitor;
import static org.junit.Assert.*;

public class ConvertFunctionTest extends FunctionTest {

        @Test
        public void testConstraint2D() throws Exception {
                // Test null input
                ST_Force_2D function = new ST_Force_2D();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                Value vg1 = ValueFactory.createValue(JTSPoint3D);
                res = evaluate(function, vg1);
                assertEquals(res.getType(), Type.POINT);
                assertTrue(res.equals(vg1).getAsBoolean());

                // Test too many parameters
                try {
                        res = evaluate(function, vg1, ValueFactory.createValue(JTSMultiLineString2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test return type
                Type type = TypeFactory.createType(Type.LINESTRING);
                type = evaluate(function, type);
                assertEquals(type.getTypeCode(), Type.GEOMETRY);
                assertEquals(type.getIntConstraint(Constraint.DIMENSION_3D_GEOMETRY), 2);

                // Test coordinates
                try {
                        res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                                ValueFactory.createValue(JTSPoint3D)));
                        Coordinate coordResult = res.getAsGeometry().getCoordinate();

                        Coordinate coordSource = JTSPoint3D.getCoordinate();

//                        System.out.println(JTSPoint3D.getCoordinate().z);
                        assertTrue(coordResult.equals2D(coordSource));

                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testConstraint3D() throws Exception {
                // Test null input
                ST_Force_3D function = new ST_Force_3D();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                Value vg1 = ValueFactory.createValue(JTSMultiPolygon2D);
                res = evaluate(function, vg1);
                assertTrue((res.getType() & Type.GEOMETRY) != 0);
                assertTrue(res.equals(vg1).getAsBoolean());

                // Test too many parameters
                try {
                        res = evaluate(function, vg1, ValueFactory.createValue(JTSMultiLineString2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test return type
                Type type = TypeFactory.createType(Type.LINESTRING);
                type = evaluate(function, type);
                assertEquals(type.getTypeCode(), Type.GEOMETRY);
                assertEquals(type.getIntConstraint(Constraint.DIMENSION_3D_GEOMETRY), 3);
        }

        @Test
        public void testST_ToMultiSegments() throws Exception {

                ST_ToMultiSegments function = new ST_ToMultiSegments();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                Value vg1 = ValueFactory.createValue(JTSMultiLineString2D);
                res = evaluate(function, vg1);

                assertEquals(res.getAsGeometry().getNumGeometries(), 3);
        }

        @Test
        public void testBoundary() throws Exception {
                // Test null input
                ST_Boundary function = new ST_Boundary();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D));
                assertEquals(res.getType(), Type.MULTILINESTRING);
                assertTrue(res.getAsGeometry().equalsExact(JTSMultiPolygon2D.getBoundary()));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiLineString2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test return type
                Type type = TypeFactory.createType(Type.LINESTRING, new Dimension3DConstraint(3));
                type = evaluate(function, type);
                assertEquals(type.getTypeCode(), Type.GEOMETRY);
        }

        public final void testToMultiline() throws Exception {
                Geometry g = testSpatialFunction(new ST_ToMultiLine(),
                        JTSMultiPolygon2D, 1).getAsGeometry();
                assertTrue(JTSMultiPolygon2D.getEnvelopeInternal().equals(g.getEnvelopeInternal()));
        }

        public final void testToMultipoint() throws Exception {
                Geometry g = testSpatialFunction(new ST_ToMultiPoint(),
                        JTSMultiPolygon2D, 1).getAsGeometry();
                assertEquals(JTSMultiPolygon2D.getCoordinates().length, g.getNumGeometries());

        }

        public final void testCentroid() throws Exception {
                Geometry g = testSpatialFunction(new ST_Centroid(), JTSMultiPolygon2D, 1).getAsGeometry();
                assertEquals(JTSMultiPolygon2D.getCentroid(), g);
        }

        /**  
         * Test open and end point functions.  
         * @throws Exception  
         */
        public final void testStartEndPoint() throws Exception {
                //Test with linestring  
                Geometry g = testSpatialFunction(new ST_StartPoint(),
                        JTSLineString3D, 1).getAsGeometry();
                assertTrue(JTSLineString3D.getCoordinates()[0].equals2D(g.getCoordinate()));

                g = testSpatialFunction(new ST_EndPoint(),
                        JTSLineString3D, 1).getAsGeometry();
                assertTrue(JTSLineString3D.getCoordinates()[0].equals2D(g.getCoordinate()));

                //Test multilinestring  
                g = testSpatialFunction(new ST_EndPoint(),
                        JTSMultiLineString2D, 1).getAsGeometry();
                assertTrue(JTSMultiLineString2D.getCoordinates()[0].equals2D(g.getCoordinate()));

                g = testSpatialFunction(new ST_StartPoint(),
                        JTSMultiLineString2D, 1).getAsGeometry();
                assertTrue(JTSMultiLineString2D.getCoordinates()[0].equals2D(g.getCoordinate()));

                //Test with bad geometry arguments  
                Value value = testSpatialFunction(new ST_EndPoint(), JTSPolygon2D, 1);
                assertTrue(value.isNull());

                value = testSpatialFunction(new ST_StartPoint(), JTSPolygon2D, 1);
                assertTrue(value.isNull());

                value = testSpatialFunction(new ST_EndPoint(), JTSPoint2D, 1);
                assertTrue(value.isNull());

                value = testSpatialFunction(new ST_StartPoint(), JTSPoint2D, 1);
                assertTrue(value.isNull());



        }

        /**  
         * Test function to extract holes from a geometry  
         * @throws Exception  
         */
        public final void testST_Holes() throws Exception {

                Geometry g = testSpatialFunction(new ST_Holes(),
                        JTSPolygonWith2Holes, 1).getAsGeometry();
                assertEquals(g.getNumGeometries(), 2);

                g = g.getFactory().createGeometryCollection(new Geometry[]{JTSGeometryCollection, JTSPolygonWith2Holes});

                g = testSpatialFunction(new ST_Holes(),
                        g, 1).getAsGeometry();
                assertEquals(g.getNumGeometries(), 2);
        }

        @Test
        public void testST_Explode() throws Exception {
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(JTSGeometryCollection)});
                ST_Explode sT_Explode = new ST_Explode();
                DataSet result = sT_Explode.evaluate(dsf, new DataSet[]{driver1}, new Value[]{}, new NullProgressMonitor());
                assertTrue(result.getRowCount() == JTSGeometryCollection.getNumGeometries());
        }
        
        @Test
        public void testST_Explode2() throws Exception {
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(JTSPoint2D)});
                ST_Explode sT_Explode = new ST_Explode();
                DataSet result = sT_Explode.evaluate(dsf, new DataSet[]{driver1}, new Value[]{}, new NullProgressMonitor());
                assertTrue(result.getRowCount() == JTSPoint2D.getNumGeometries());
        }
}