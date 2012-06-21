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
package org.gdms.sql.function.spatial.operators;

import org.junit.Test;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.ScalarFunction;
import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_Difference;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnion;
import org.gdms.sql.function.spatial.geometry.operators.ST_Intersection;
import org.gdms.sql.function.spatial.geometry.operators.ST_SymDifference;
import org.gdms.sql.function.spatial.geometry.properties.ST_ConvexHull;
import org.gdms.data.types.IncompatibleTypesException;

import static org.junit.Assert.*;

public class OperatorsTest extends FunctionTest {

        @Test
        public void testBuffer() throws Exception {
                // Test null input
                ST_Buffer function = new ST_Buffer();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()), new ColumnValue(Type.DOUBLE,
                        ValueFactory.createValue(3)));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(4));
                assertTrue(res.getType() == Type.MULTIPOLYGON || res.getType() == Type.POLYGON);
                assertEquals(res.getAsGeometry(), JTSMultiPolygon2D.buffer(4));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(4), ValueFactory.createValue(4), ValueFactory.createValue(4));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(""), ValueFactory.createValue(""));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
                try {
                        res = evaluate(function, ValueFactory.createValue(""), ValueFactory.createValue(3), ValueFactory.createValue(""));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testDifference() throws Exception {
                // Test null input
                ST_Difference function = new ST_Difference();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()), new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(JTSMultiPolygon2D)));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiPolygon2D));
                assertTrue((res.getType() & Type.GEOMETRY) != 0);
                assertTrue(res.getAsGeometry().equalsExact(JTSMultiLineString2D.difference(JTSMultiPolygon2D)));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testGeomUnion() throws Exception {
                // Test null input
                ST_GeomUnion function = new ST_GeomUnion();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                function = new ST_GeomUnion();
                res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D));
                Value res2 = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.equals(res2).getAsBoolean());

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiLineString2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test it works with geometry collections
                evaluate(function, ValueFactory.createValue(JTSGeometryCollection));

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new ST_GeomUnion()).isNull());
        }

        @Test
        public void testIntersection() throws Exception {
                // Test null input
                ST_Intersection function = new ST_Intersection();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(JTSMultiLineString2D)), new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory.createValue(JTSMultiLineString2D));
                assertTrue((res.getType() &Type.GEOMETRY) != 0);
                assertTrue(res.getAsGeometry().equalsExact(JTSMultiPoint2D.intersection(JTSMultiLineString2D)));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testSymDifference() throws Exception {
                // Test null input
                ST_SymDifference function = new ST_SymDifference();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(JTSMultiPoint2D)), new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiLineString2D));
                assertTrue((res.getType() & Type.GEOMETRYCOLLECTION) != 0);
                assertTrue(res.getAsGeometry().equalsExact(JTSMultiLineString2D.symDifference(JTSMultiLineString2D)));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testConvexHull() throws Exception {
                ScalarFunction function = new ST_ConvexHull();

                // Test null input
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D));
                assertEquals(res.getType(), Type.POLYGON);
                assertTrue(res.getAsGeometry().contains(JTSMultiPolygon2D));
                assertTrue(JTSMultiPolygon2D.contains(res.getAsGeometry()));
//		System.out.println(res.getAsGeometry());
                assertEquals(res.getAsGeometry().getNumGeometries(), 1);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(4));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(123));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                try {
                        res = evaluate(function, ValueFactory.createValue(""));
                        fail();
                } catch (IncompatibleTypesException e) {
                }


        }
}
