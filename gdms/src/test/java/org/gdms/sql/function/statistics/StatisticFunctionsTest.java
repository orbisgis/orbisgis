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
package org.gdms.sql.function.statistics;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class StatisticFunctionsTest extends FunctionTest {

	public void testAbs() throws Exception {
		// Test null input
		Abs function = new Abs();
		Value res = evaluate(function, new ColumnValue(Type.LONG, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(-123L));
		assertTrue(res.getType() == Type.LONG);
		assertTrue(res.getAsInt() == 123);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(-123),
					ValueFactory.createValue(-123));
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

	public void testPower() throws Exception {
		// Test null input
		Power function = new Power();
		Value res = evaluate(function, new ColumnValue(Type.LONG, ValueFactory
				.createValue(3)), new ColumnValue(Type.LONG, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(4L), ValueFactory
				.createValue(4L));
		assertTrue(res.getType() == Type.DOUBLE);
		assertTrue(res.getAsInt() == 4 * 4 * 4 * 4);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(-123),
					ValueFactory.createValue(3), ValueFactory.createValue(-123));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(5), ValueFactory
					.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testRound() throws Exception {
		// Test null input
		Round function = new Round();
		Value res = evaluate(function, new ColumnValue(Type.FLOAT, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(4.53));
		assertTrue(res.getType() == Type.LONG);
		assertTrue(res.getAsDouble() == 5);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(-123),
					ValueFactory.createValue(3));
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

	public void testSqrt() throws Exception {
		// Test null input
		Sqrt function = new Sqrt();
		Value res = evaluate(function, new ColumnValue(Type.INT, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(10));
		assertTrue(res.getType() == Type.DOUBLE);
		assertTrue(res.getAsDouble() == Math.sqrt(10));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(-123),
					ValueFactory.createValue(3));
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

	public void testStandardDeviation() throws Exception {
		// Test null input
		StandardDeviation function = new StandardDeviation();
		Value res = evaluate(function, new ColumnValue(Type.INT, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		function = new StandardDeviation();
		res = evaluate(function, new ColumnValue(Type.INT, ValueFactory
				.createNullValue()));
		assertTrue(res.isNull());
		res = evaluate(function, ValueFactory.createValue(5));
		res = evaluate(function, ValueFactory.createValue(6));
		res = evaluate(function, ValueFactory.createValue(8));
		res = evaluate(function, ValueFactory.createValue(9));
		res = evaluate(function, new ColumnValue(Type.INT, ValueFactory
				.createNullValue()));
		assertTrue(res.getType() == Type.DOUBLE);
		assertTrue(res.getAsDouble() == Math.sqrt(2.5));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(-123),
					ValueFactory.createValue(3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test zero rows
		assertTrue(evaluateAggregatedZeroRows(new StandardDeviation()).isNull());
	}

}
