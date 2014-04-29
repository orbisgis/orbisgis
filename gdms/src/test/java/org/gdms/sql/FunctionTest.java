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
package org.gdms.sql;

import com.vividsolutions.jts.geom.GeometryCollection;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.ScalarFunction;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.types.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.TestBase;
import org.gdms.sql.function.AggregateFunction;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionValidator;
import org.junit.Before;

import static org.junit.Assert.*;

public abstract class FunctionTest extends TestBase {

        protected Geometry JTSMultiPolygon2D;
        protected Geometry JTSMultiLineString2D;
        protected Geometry JTSMultiPoint2D;
        protected Geometry JTSPolygon2D; // With two holes
        protected Geometry JTSGeometryCollection;
        protected Geometry JTSPoint3D;
        protected Geometry JTSPoint2D;
        protected Geometry JTSLineString2D;
        protected Geometry JTSLineString3D;
        protected Geometry JTSPolygonWith2Holes;
        protected GeometryCollection JTS3DCollection;
        protected WKTReader wktReader;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                wktReader = new WKTReader();
                JTSMultiPolygon2D = wktReader.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
                JTSMultiLineString2D = wktReader.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
                JTSMultiPoint2D = wktReader.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
                JTSPolygon2D = wktReader.read("POLYGON ((181 124, 87 162, 76 256, 166 315, 286 325, 373 255, 387 213, 377 159, 351 121, 298 101, 234 56, 181 124), (165 244, 227 219, 234 300, 168 288, 165 244), (244 130, 305 135, 324 186, 306 210, 272 206, 206 174, 244 130))");

                JTSLineString2D = wktReader.read("LINESTRING (1 1, 2 1, 2 2, 1 2, 1 1)");
                JTSLineString3D = wktReader.read("LINESTRING (1 1 1, 2 1 2, 2 2 3, 1 2 4, 1 1 5)");
                JTSPoint3D = wktReader.read("POINT(0 10 20)");
                JTSPoint2D = wktReader.read("POINT(0 10)");

                JTSPolygonWith2Holes = wktReader.read("POLYGON ((85 55, 85 306, 366 306, 366 55, 85 55), (153 205, 212 173, 241 190, 251 253, 235 278, 147 254, 153 205), (262 88, 321 97, 324 153, 303 177, 240 138, 262 88))");

                GeometryFactory gf = new GeometryFactory();
                JTSGeometryCollection = gf.createGeometryCollection(new Geometry[]{
                                JTSMultiPolygon2D, JTSMultiLineString2D, JTSPolygon2D});
                JTS3DCollection = gf.createGeometryCollection(new Geometry[]{JTSMultiPolygon2D, JTSLineString3D});

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"pk", "geom"},
                        new Type[]{
                                TypeFactory.createType(Type.INT,
                                new PrimaryKeyConstraint()),
                                TypeFactory.createType(Type.GEOMETRY)});

                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(1),
                                ValueFactory.createValue(JTSMultiPolygon2D)});
                driver1.addValues(new Value[]{ValueFactory.createValue(2),
                                ValueFactory.createValue(JTSMultiLineString2D)});
                driver1.addValues(new Value[]{ValueFactory.createValue(3),
                                ValueFactory.createValue(JTSLineString2D)});
                driver1.addValues(new Value[]{ValueFactory.createValue(4),
                                ValueFactory.createValue(JTSPolygon2D)});
                // and register this new driver...

                dsf.getSourceManager().register("ds1", driver1);

                // second datasource
                final MemoryDataSetDriver driver2 = new MemoryDataSetDriver(
                        new String[]{"pk", "geom"},
                        new Type[]{TypeFactory.createType(Type.INT,
                                new PrimaryKeyConstraint()),
                                TypeFactory.createType(Type.GEOMETRY)});

                driver1.addValues(new Value[]{ValueFactory.createValue(1),
                                ValueFactory.createValue(JTSMultiPolygon2D)});
                // and register this new driver...
                dsf.getSourceManager().register("ds2", driver2);

        }

        protected Value evaluate(Function function, ColumnValue... args)
                throws FunctionException {
                int[] types = new int[args.length];
                Value[] values = new Value[args.length];
                for (int i = 0; i < types.length; i++) {
                        types[i] = args[i].getTypeCode();
                        values[i] = args[i].getValue();
                }
                FunctionValidator.failIfTypesDoNotMatchSignature(types, function.getFunctionSignatures());
                return evaluateFunction(function, values);
        }

        protected Value evaluateAggregatedZeroRows(AggregateFunction function) {
                return function.getAggregateResult();
        }

        private Value evaluateFunction(Function function, Value[] values)
                throws FunctionException {
                if (function.isAggregate()) {
                        AggregateFunction f = (AggregateFunction) function;
                        f.evaluate(dsf, values);
                        Value lastCall = f.getAggregateResult();

                        return lastCall;
                } else {
                        return ((ScalarFunction) function).evaluate(dsf, values);
                }
        }

        protected Value evaluate(Function function, Value... args)
                throws FunctionException {
                int[] types = new int[args.length];
                for (int i = 0; i < types.length; i++) {
                        types[i] = args[i].getType();
                }
                FunctionValidator.failIfTypesDoNotMatchSignature(types, function.getFunctionSignatures());
                return evaluateFunction(function, args);
        }

        protected int evaluateDeclaredType(ScalarFunction function, int... args)
                throws FunctionException {
                return function.getType(args);
        }

        protected int evaluateDeclaredType(AggregateFunction function, int... args)
                throws FunctionException {
                return function.getType(args);
        }

        protected Value testSpatialFunction(ScalarFunction function,
                Geometry normalValue, int parameterCount) throws Exception {
                return testSpatialFunction(function, new int[]{Type.GEOMETRY}, parameterCount, ValueFactory.createValue(normalValue));
        }

        protected Value testSpatialFunction(ScalarFunction function,
                int[] valueType, int parameterCount, Value... normalValue) throws Exception {
                // Test null input
                ColumnValue[] vals = new ColumnValue[parameterCount];
                for (int i = 0; i < parameterCount; i++) {
                        vals[i] = new ColumnValue(valueType[i], ValueFactory.createNullValue());
                }
                Value res = evaluate(function, vals);
                assertTrue(res.isNull());

                // Test too many parameters
                Value[] args = new Value[parameterCount + 1];
                for (int i = 0; i < args.length; i++) {
                        args[i] = normalValue[0];
                }
                try {
                        res = evaluate(function, args);
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        args = new Value[parameterCount];
                        for (int i = 0; i < args.length; i++) {
                                args[i] = ValueFactory.createValue(new Value[0]);
                        }
                        res = evaluate(function, args);
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test normal input value and type
                res = evaluate(function, normalValue);
                return res;
        }
}
