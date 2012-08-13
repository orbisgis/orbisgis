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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.ScalarFunction;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.sql.function.alphanumeric.AutoNumeric;
import org.gdms.sql.function.alphanumeric.Average;
import org.gdms.sql.function.alphanumeric.Count;
import org.gdms.sql.function.alphanumeric.Max;
import org.gdms.sql.function.alphanumeric.Min;
import org.gdms.sql.function.alphanumeric.StrLength;
import org.gdms.sql.function.alphanumeric.Sum;

import static org.junit.Assert.*;

public class AlphanumericFunctionTest extends FunctionTest {

        @Test
        public void testAutoNumeric() throws Exception {
                final ScalarFunction function = new AutoNumeric();

                // Test too many parameters
                try {
                        evaluate(function, ValueFactory.createValue(54));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test return type and value
                int type = evaluate(function, new Value[0]).getType();
                assertTrue((Type.LONG == type) || (Type.INT == type));
                assertEquals(1l, evaluate(function, new Value[0]).getAsLong());
        }

        @Test
        public void testAverage() throws Exception {
                // Test null input
                Average avg = new Average();
                Value res = evaluate(avg, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertTrue(res.isNull());
                avg = new Average();
                res = evaluate(avg, new ColumnValue(Type.DOUBLE, ValueFactory.createNullValue()));
                res = evaluate(avg, ValueFactory.createValue(5));
                assertEquals(res.getAsDouble(), 5, 0);

                // Test normal input value and type
                avg = new Average();
                res = evaluate(avg, ValueFactory.createValue(2));
                res = evaluate(avg, ValueFactory.createValue(4));
                res = evaluate(avg, new ColumnValue(Type.INT, ValueFactory.createNullValue()));
                assertEquals(res.getAsDouble(), 3, 0);

                // Test too many parameters
                try {
                        res = evaluate(avg, ValueFactory.createValue(54), ValueFactory.createValue(6));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(avg, ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new Average()).isNull());
        }

        @Test
        public void testCount() throws Exception {
                // Test null input
                Count function = new Count();
                Value res = evaluate(function, new ColumnValue(Type.STRING,
                        ValueFactory.createNullValue()));
                assertEquals(res.getAsInt(), 0);

                // Test normal input value and type
                function = new Count();
                res = evaluate(function, ValueFactory.createValue(3));
                res = evaluate(function, ValueFactory.createNullValue());
                assertEquals(res.getType(), Type.LONG);
                assertEquals(res.getAsInt(), 1);

                // Test zero rows
                assertEquals(evaluateAggregatedZeroRows(new Count()).getAsInt(), 0);
        }

        @Test
        public void testMax() throws Exception {
                // Test null input
                Max function = new Max();
                Value res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                function = new Max();
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createValue((byte) 3)));
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.BYTE);
                assertEquals(res.getAsInt(), 3);

                function = new Max();
                res = evaluate(function, ValueFactory.createValue(3f));
                res = evaluate(function, new ColumnValue(Type.FLOAT, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.FLOAT);
                assertEquals(res.getAsInt(), 3);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(3), ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue("f"));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new Max()).isNull());
        }

        @Test
        public void testMin() throws Exception {
                // Test null input
                Min function = new Min();
                Value res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                function = new Min();
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createValue((byte) 3)));
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.BYTE);
                assertEquals(res.getAsInt(), 3);

                function = new Min();
                res = evaluate(function, ValueFactory.createValue(3f));
                res = evaluate(function, new ColumnValue(Type.FLOAT, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.FLOAT);
                assertEquals(res.getAsInt(), 3);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(3), ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue("f"));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new Min()).isNull());
        }

        @Test
        public void testSum() throws Exception {
                // Test null input
                Sum function = new Sum();
                Value res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                function = new Sum();
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createValue((byte) 3)));
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createValue((byte) 3)));
                res = evaluate(function, new ColumnValue(Type.BYTE, ValueFactory.createNullValue()));
                assertTrue(TypeFactory.isNumerical(res.getType()));
                assertEquals(res.getAsInt(), 6);

                function = new Sum();
                res = evaluate(function, ValueFactory.createValue(3f));
                res = evaluate(function, new ColumnValue(Type.FLOAT, ValueFactory.createNullValue()));
                assertEquals(res.getType(), Type.FLOAT);
                assertEquals(res.getAsInt(), 3);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(3), ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue("f"));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test zero rows
                assertTrue(evaluateAggregatedZeroRows(new Sum()).isNull());
        }

        @Test
        public void testStrLength() throws Exception {
                // Test null input
                StrLength function = new StrLength();
                Value res = evaluate(function, new ColumnValue(Type.STRING,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue("fer"));
                assertEquals(res.getType(), Type.INT);
                assertEquals(res.getAsInt(), 3);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue("as"),
                                ValueFactory.createValue("as"));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(3));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }
}
