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
package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.data.values.WKBUtil;

public abstract class FunctionTest extends TestCase {

        protected Geometry JTSMultiPolygon2D;
        protected Geometry JTSMultiLineString2D;
        protected Geometry JTSMultiPoint2D;
        protected Geometry JTSPolygon2D; // With two holes
        protected Geometry JTSGeometryCollection;
        protected Geometry JTSPoint3D;
        protected Geometry JTSLineString2D;
        protected Geometry JTSPoint2D;  
        protected Geometry JTSMultiLineString3D;
        protected Geometry JTSLineString3D;
        protected  Geometry JTSPolygonWith2Holes;
      
        public static DataSourceFactory dsf = new DataSourceFactory();


        @Override
        protected void setUp() throws Exception {
                WKTReader wktr = new WKTReader();
                JTSMultiPolygon2D = wktr.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
                JTSMultiLineString2D = wktr.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
                JTSMultiLineString3D = wktr.read("MULTILINESTRING ((0 0 1, 1 1 12, 0 1 2, 0 0 12))");
                JTSMultiPoint2D = wktr.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
                JTSPolygon2D = wktr.read("POLYGON ((181 124, 87 162, 76 256, 166 315, 286 325, 373 255, 387 213, 377 159, 351 121, 298 101, 234 56, 181 124), (165 244, 227 219, 234 300, 168 288, 165 244), (244 130, 305 135, 324 186, 306 210, 272 206, 206 174, 244 130))");

                JTSLineString2D = wktr.read("LINESTRING (1 1, 2 1, 2 2, 1 2, 1 1)");
                JTSLineString3D = wktr.read("LINESTRING (1 1 1, 2 1 2, 2 2 3, 1 2 4, 1 1 5)");
                JTSPoint3D = wktr.read("POINT(0 10 20)");
                JTSPoint2D = wktr.read("POINT(0 10)");

                JTSPolygonWith2Holes = wktr.read("POLYGON ((85 55, 85 306, 366 306, 366 55, 85 55), (153 205, 212 173, 241 190, 251 253, 235 278, 147 254, 153 205), (262 88, 321 97, 324 153, 303 177, 240 138, 262 88))");

                GeometryFactory gf = new GeometryFactory();
                JTSGeometryCollection = gf.createGeometryCollection(new Geometry[]{
                                JTSMultiPolygon2D, JTSMultiLineString2D, JTSPolygon2D});
                // first datasource
                final GenericObjectDriver driver1 = new GenericObjectDriver(
                        new String[]{"pk", "geom"},
                        new Type[]{
                                TypeFactory.createType(
                                Type.INT,
                                new Constraint[]{new PrimaryKeyConstraint()}),
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

                if (!dsf.exists("ds1")) {
                        dsf.getSourceManager().register("ds1", driver1);
                }

                // second datasource
                final GenericObjectDriver driver2 = new GenericObjectDriver(
                        new String[]{"pk", "geom"},
                        new Type[]{
                                TypeFactory.createType(
                                Type.INT,
                                new Constraint[]{new PrimaryKeyConstraint()}),
                                TypeFactory.createType(Type.GEOMETRY)});

                driver1.addValues(new Value[]{ValueFactory.createValue(1),
                                ValueFactory.createValue(JTSMultiPolygon2D)});
                // and register this new driver...
                if (!dsf.exists("ds2")) {
                        dsf.getSourceManager().register("ds2", driver2);
                }
        }

        protected Value evaluate(Function function, ColumnValue... args)
                throws FunctionException {
                Type[] types = new Type[args.length];
                Value[] values = new Value[args.length];
                for (int i = 0; i < types.length; i++) {
                        types[i] = TypeFactory.createType(args[i].getTypeCode());
                        values[i] = args[i].getValue();
                }
                FunctionOperator.validateFunction(types, function);
                return evaluateFunction(function, values);
        }

        protected Value evaluateAggregatedZeroRows(Function function) {
                return function.getAggregateResult();
        }

        private Value evaluateFunction(Function function, Value[] values)
                throws FunctionException {
                if (function.isAggregate()) {
                        Value lastEvaluation = function.evaluate(dsf, values);
                        Value lastCall = function.getAggregateResult();
                        if (lastCall != null) {
                                return lastCall;
                        } else {
                                return lastEvaluation;
                        }
                } else {
                        return function.evaluate(dsf, values);
                }
        }

        protected Value evaluate(Function function, Value... args)
                throws FunctionException {
                Type[] types = new Type[args.length];
                for (int i = 0; i < types.length; i++) {
                        types[i] = TypeFactory.createType(args[i].getType());
                }
                FunctionOperator.validateFunction(types, function);
                return evaluateFunction(function, args);
        }

        protected Type evaluate(Function function, Type... args)
                throws FunctionException {
                return function.getType(args);
        }

        protected Value testSpatialFunction(Function function,
                Geometry normalValue, int parameterCount) throws Exception {
                return testSpatialFunction(function, ValueFactory.createValue(normalValue), Type.GEOMETRY, parameterCount);
        }

        protected Value testSpatialFunction(Function function, Value normalValue,
                int valueType, int parameterCount) throws Exception {
                // Test null input
                Value res = evaluate(function, new ColumnValue(valueType, ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test too many parameters
                Value[] args = new Value[parameterCount + 1];
                for (int i = 0; i < args.length; i++) {
                        args[i] = normalValue;
                }
                try {
                        res = evaluate(function, args);
                        assertTrue(false);
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        Value wrong = ValueFactory.createValue(new Value[0]);
                        res = evaluate(function, wrong);
                        assertTrue(false);
                } catch (IncompatibleTypesException e) {
                }

                // Test normal input value and type
                res = evaluate(function, normalValue);
                return res;
        }

        /**
         * Return a wkt representation of the geometry
         * @param geom
         * @return
         */
        public String toString(Geometry geom) {
                return WKBUtil.getTextWKTWriter3DInstance().write(geom);
        }
}
