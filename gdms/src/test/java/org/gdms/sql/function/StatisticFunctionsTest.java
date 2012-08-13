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
package org.gdms.sql.function;

import org.junit.Test;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.math.Abs;
import org.gdms.sql.function.math.Power;
import org.gdms.sql.function.math.Round;
import org.gdms.sql.function.math.Sqrt;
import org.gdms.sql.function.math.StandardDeviation;
import org.gdms.data.types.IncompatibleTypesException;

import static org.junit.Assert.*;

public class StatisticFunctionsTest extends FunctionTest {

        @Test
        public void testAbs() throws Exception {
                // Test null input
                Abs function = new Abs();
                Value res = evaluate(function, new ColumnValue(Type.LONG, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(-123L));
                assertEquals(res.getType(), Type.LONG);
                assertEquals(res.getAsInt(), 123);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(-123),
                                ValueFactory.createValue(-123));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testPower() throws Exception {
                // Test null input
                Power function = new Power();
                Value res = evaluate(function, new ColumnValue(Type.LONG, ValueFactory.createValue(3)), new ColumnValue(Type.LONG, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(4L), ValueFactory.createValue(4L));
                assertEquals(res.getType(), Type.DOUBLE);
                assertEquals(res.getAsInt(), 4 * 4 * 4 * 4);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(-123),
                                ValueFactory.createValue(3), ValueFactory.createValue(-123));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(5), ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testRound() throws Exception {
                // Test null input
                Round function = new Round();
                Value res = evaluate(function, new ColumnValue(Type.FLOAT, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(4.53));
                assertEquals(res.getType(), Type.LONG);
                assertEquals(res.getAsDouble(), 5, 0);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(-123),
                                ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testSqrt() throws Exception {
                // Test null input
                Sqrt function = new Sqrt();
                Value res = evaluate(function, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(10));
                assertEquals(res.getType(), Type.DOUBLE);
                assertEquals(res.getAsDouble(), Math.sqrt(10), 0);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(-123),
                                ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testStandardDeviation() throws Exception {
                // Test null input
                StandardDeviation function = new StandardDeviation();
                Value res = evaluate(function, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                function = new StandardDeviation();
                res = evaluate(function, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertTrue(res.isNull());
                res = evaluate(function, ValueFactory.createValue(5));
                res = evaluate(function, ValueFactory.createValue(6));
                res = evaluate(function, ValueFactory.createValue(8));
                res = evaluate(function, ValueFactory.createValue(9));
                res = evaluate(function, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.DOUBLE);
                assertEquals(res.getAsDouble(), Math.sqrt(2.5), 0.00001);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(-123),
                                ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(false));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new StandardDeviation()).isNull());
        }
}
