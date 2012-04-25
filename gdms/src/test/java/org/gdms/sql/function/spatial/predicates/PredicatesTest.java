/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.sql.function.spatial.predicates;

import org.junit.Test;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.ScalarFunction;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Contains;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Crosses;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Disjoint;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Equals;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Intersects;
import org.gdms.sql.function.spatial.geometry.predicates.ST_IsWithin;
import org.gdms.sql.function.spatial.geometry.predicates.ST_IsWithinDistance;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Overlaps;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Relate;
import org.gdms.sql.function.spatial.geometry.predicates.ST_Touches;
import org.gdms.data.types.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

import static org.junit.Assert.*;

public class PredicatesTest extends FunctionTest {

        @Test
        public void testPredicates() throws Exception {
                testPredicate(new ST_Contains(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Crosses(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Disjoint(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Equals(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Intersects(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_IsWithin(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Overlaps(), JTSMultiPolygon2D, JTSMultiLineString2D);
                testPredicate(new ST_Touches(), JTSMultiPolygon2D, JTSMultiLineString2D);
        }

        private void testPredicate(ScalarFunction function, Geometry g1, Geometry g2)
                throws Exception {
                // Test null input
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(g1)), new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(g2), ValueFactory.createValue(g1));
                assertEquals(res.getType(), Type.BOOLEAN);

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(g2), ValueFactory.createValue(g2), ValueFactory.createValue(JTSMultiPoint2D));
                        fail();
                } catch (IncompatibleTypesException e) {
                }

                // Test wrong parameter type
                try {
                        res = evaluate(function, ValueFactory.createValue(g1), ValueFactory.createValue(true));
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testIsWithinDistance() throws Exception {
                // Test null input
                ST_IsWithinDistance function = new ST_IsWithinDistance();
                Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(JTSMultiLineString2D)), new ColumnValue(Type.GEOMETRY,
                        ValueFactory.createValue(JTSMultiPoint2D)), new ColumnValue(Type.DOUBLE,
                        ValueFactory.createNullValue()));
                assertTrue(res.isNull());

                // Test normal input value and type
                res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory.createValue(14));
                assertEquals(res.getType(), Type.BOOLEAN);
                assertEquals(res.getAsBoolean(), JTSMultiLineString2D.isWithinDistance(JTSMultiPolygon2D, 14));

                // Test too many parameters
                try {
                        res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(JTSMultiLineString2D), ValueFactory.createValue(14),
                                ValueFactory.createValue(1));
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
        public void testRelate() throws Exception {
                // Test null input
                ST_Relate function = new ST_Relate();
                Value geomA = ValueFactory.createValue(JTSMultiPolygon2D);
                Value geomB = ValueFactory.createValue(JTSMultiLineString2D);
                Value pattern = ValueFactory.createValue("FF21FFFF2");

                Value[] values = new Value[]{geomA, geomB, pattern};
                Value val = function.evaluate(dsf, values);
                assertTrue(val.getAsBoolean());

                pattern = ValueFactory.createValue("F1FFFF2F2");

                values = new Value[]{geomA, geomB, pattern};
                val = function.evaluate(dsf, values);
                assertFalse(val.getAsBoolean());
        }
}
