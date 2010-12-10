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
import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_Difference;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnion;
import org.gdms.sql.function.spatial.geometry.operators.ST_Intersection;
import org.gdms.sql.function.spatial.geometry.operators.ST_SymDifference;
import org.gdms.sql.function.spatial.geometry.properties.ST_ConvexHull;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class OperatorsTest extends FunctionTest {

	public void testBuffer() throws Exception {
		// Test null input
		ST_Buffer function = new ST_Buffer();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.DOUBLE,
				ValueFactory.createValue(3)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory
				.createValue(4));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equals(JTSMultiPolygon2D.buffer(4)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
					.createValue(4), ValueFactory.createValue(4), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
					.createValue(3), ValueFactory.createValue(3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
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
		ST_Difference function = new ST_Difference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(JTSMultiPolygon2D)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
				.createValue(JTSMultiPolygon2D));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(JTSMultiLineString2D.difference(JTSMultiPolygon2D)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D), ValueFactory
					.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
					.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

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
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
					.createValue(JTSMultiLineString2D));
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
		evaluate(function, ValueFactory.createValue(JTSGeometryCollection));

		// Test zero rows
		assertTrue(evaluateAggregatedZeroRows(new ST_GeomUnion()).isNull());
	}

	public void testIntersection() throws Exception {
		// Test null input
		ST_Intersection function = new ST_Intersection();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(JTSMultiLineString2D)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory
				.createValue(JTSMultiLineString2D));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(JTSMultiPoint2D.intersection(JTSMultiLineString2D)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory
					.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiPoint2D), ValueFactory
					.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testSymDifference() throws Exception {
		// Test null input
		ST_SymDifference function = new ST_SymDifference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(JTSMultiPoint2D)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
				.createValue(JTSMultiLineString2D));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(JTSMultiLineString2D.symDifference(JTSMultiLineString2D)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
					.createValue(JTSMultiPolygon2D), ValueFactory.createValue(JTSMultiPoint2D));
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
		Function function = new ST_ConvexHull();

		// Test null input
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().contains(JTSMultiPolygon2D));
		assertTrue(JTSMultiPolygon2D.contains(res.getAsGeometry()));
		System.out.println(res.getAsGeometry());
		assertTrue(res.getAsGeometry().getNumGeometries() == 1);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(JTSMultiLineString2D), ValueFactory
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
