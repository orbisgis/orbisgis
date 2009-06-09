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
package org.gdms.sql.function.spatial.predicates;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.spatial.geometry.predicates.Contains;
import org.gdms.sql.function.spatial.geometry.predicates.Crosses;
import org.gdms.sql.function.spatial.geometry.predicates.Disjoint;
import org.gdms.sql.function.spatial.geometry.predicates.Equals;
import org.gdms.sql.function.spatial.geometry.predicates.Intersects;
import org.gdms.sql.function.spatial.geometry.predicates.IsWithin;
import org.gdms.sql.function.spatial.geometry.predicates.IsWithinDistance;
import org.gdms.sql.function.spatial.geometry.predicates.Overlaps;
import org.gdms.sql.function.spatial.geometry.predicates.Relate;
import org.gdms.sql.function.spatial.geometry.predicates.Touches;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

public class PredicatesTest extends FunctionTest {

	public void testPredicates() throws Exception {
		testPredicate(new Contains(), g1, g2);
		testPredicate(new Crosses(), g1, g2);
		testPredicate(new Disjoint(), g1, g2);
		testPredicate(new Equals(), g1, g2);
		testPredicate(new Intersects(), g1, g2);
		testPredicate(new IsWithin(), g1, g2);
		testPredicate(new Overlaps(), g1, g2);
		testPredicate(new Touches(), g1, g2);
	}

	private void testPredicate(Function function, Geometry g1, Geometry g2)
			throws Exception {
		// Test null input
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g1)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.BOOLEAN);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
					.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}


	public void testIsWithinDistance() throws Exception {
		// Test null input
		IsWithinDistance function = new IsWithinDistance();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g3)), new ColumnValue(Type.DOUBLE,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1), ValueFactory.createValue(14));
		assertTrue(res.getType() == Type.BOOLEAN);
		assertTrue(res.getAsBoolean() == g2.isWithinDistance(g1, 14));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(14),
					ValueFactory.createValue(1));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testRelate() throws Exception {
		// Test null input
		Relate function = new Relate();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.STRING);
		assertTrue(res.getAsString().equals(g2.relate(g1).toString()));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(14));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}
}
