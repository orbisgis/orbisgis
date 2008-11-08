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

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

public abstract class FunctionTest extends TestCase {

	protected Geometry g1;
	protected Geometry g2;
	protected Geometry g3;
	protected Geometry g4; //With two holes
	protected Geometry geomCollection;

	@Override
	protected void setUp() throws Exception {
		WKTReader wktr = new WKTReader();
		g1 = wktr.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
		g2 = wktr.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
		g3 = wktr.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
		g4 = wktr.read("POLYGON ((181 124, 87 162, 76 256, 166 315, 286 325, 373 255, 387 213, 377 159, 351 121, 298 101, 234 56, 181 124), (165 244, 227 219, 234 300, 168 288, 165 244), (244 130, 305 135, 324 186, 306 210, 272 206, 206 174, 244 130))");
		

		GeometryFactory gf = new GeometryFactory();
		geomCollection = gf.createGeometryCollection(new Geometry[] { g1, g2 });
		geomCollection = gf.createGeometryCollection(new Geometry[] { g3,
				geomCollection });
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
			Value lastEvaluation = function.evaluate(values);
			Value lastCall = function.getAggregateResult();
			if (lastCall != null ) {
				return lastCall;
			} else {
				return lastEvaluation;
			}
		} else {
			return function.evaluate(values);
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
		return testSpatialFunction(function, ValueFactory
				.createValue(normalValue), Type.GEOMETRY, parameterCount);
	}

	protected Value testSpatialFunction(Function function, Value normalValue,
			int valueType, int parameterCount) throws Exception {
		// Test null input
		Value res = evaluate(function, new ColumnValue(valueType, ValueFactory
				.createNullValue()));
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

}
