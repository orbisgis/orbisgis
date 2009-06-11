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
package org.gdms.sql.function.spatial.operators;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.spatial.geometry.operators.Buffer;
import org.gdms.sql.function.spatial.geometry.operators.Difference;
import org.gdms.sql.function.spatial.geometry.operators.GeomUnion;
import org.gdms.sql.function.spatial.geometry.operators.Intersection;
import org.gdms.sql.function.spatial.geometry.operators.SymDifference;
import org.gdms.sql.function.spatial.geometry.properties.ConvexHull;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class OperatorsTest extends FunctionTest {

	public void testBuffer() throws Exception {
		// Test null input
		Buffer function = new Buffer();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.DOUBLE,
				ValueFactory.createValue(3)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
				.createValue(4));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equals(g1.buffer(4)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(4), ValueFactory.createValue(4), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(3), ValueFactory.createValue(3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(""), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(""), ValueFactory
					.createValue(3), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testDifference() throws Exception {
		// Test null input
		Difference function = new Difference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g1)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g2.difference(g1)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testGeomUnion() throws Exception {
		// Test null input
		GeomUnion function = new GeomUnion();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		function = new GeomUnion();
		res = evaluate(function, ValueFactory.createValue(g2));
		Value res2 = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.equals(res2).getAsBoolean());

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test it works with geometry collections
		evaluate(function, ValueFactory.createValue(geomCollection));

		// Test zero rows
		assertTrue(evaluateAggregatedZeroRows(new GeomUnion()).isNull());
	}

	public void testIntersection() throws Exception {
		// Test null input
		Intersection function = new Intersection();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
				.createValue(g2));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g3.intersection(g2)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
					.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testSymDifference() throws Exception {
		// Test null input
		SymDifference function = new SymDifference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g3)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g2));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g2.symDifference(g2)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}


	public void testConvexHull() throws Exception {
		Function function = new ConvexHull();

		// Test null input
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g1));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().contains(g1));
		assertTrue(g1.contains(res.getAsGeometry()));
		System.out.println(res.getAsGeometry());
		assertTrue(res.getAsGeometry().getNumGeometries() == 1);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(123));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		try {
			res = evaluate(function, ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}


	}
}
