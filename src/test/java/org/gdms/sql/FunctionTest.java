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
	protected Geometry g4; // With two holes
	protected Geometry geomCollection;
	protected Geometry lineString;
	protected Geometry multiLineString;

	@Override
	protected void setUp() throws Exception {
		WKTReader wktr = new WKTReader();
		g1 = wktr.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
		g2 = wktr.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
		g3 = wktr.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
		g4 = wktr
				.read("POLYGON ((181 124, 87 162, 76 256, 166 315, 286 325, 373 255, 387 213, 377 159, 351 121, 298 101, 234 56, 181 124), (165 244, 227 219, 234 300, 168 288, 165 244), (244 130, 305 135, 324 186, 306 210, 272 206, 206 174, 244 130))");

		lineString =  wktr.read("LINESTRING (182926.9929378531 2426741.3309792844, 182923 2426740, 182911 2426741, 182892.98591865166 2426741.8352893963)");
		multiLineString = wktr.read("MULTILINESTRING ((183567.4305275123 2427174.0239940695, 183554.57165989507 2427169.953758708, 183514.0431114669 2427155.6831430644, 183464.95219365245 2427123.7169640227, 183415.2904512123 2427073.484396957, 183399.87818631704 2427058.642956687, 183355.35386550863 2427011.264512751, 183305.12129844268 2426956.4653486786, 183284.57161191572 2426924.4991696365, 183257.17202987973 2426879.9748488283, 183197 2426813, 183176 2426791, 183155.56524649635 2426776.0847669416, 183138 2426768, 183113 2426761, 183074 2426761, 183018.5673363165 2426762.3849759237, 182967.19311999905 2426760.101677421, 182935 2426744, 182923 2426740, 182911 2426741, 182892.98591865166 2426741.8352893963, 182875 2426729, 182865 2426715, 182847.3199485917 2426696.1693193363, 182791.37913526825 2426634.5202597557, 182778.82099350175 2426607.1206777194, 182778.82099350175 2426577.4377971804, 182761.6962547293 2426524.9219316114, 182761.6962547293 2426496.380700324, 182767.40450098677 2426476.9726630487))");

		GeometryFactory gf = new GeometryFactory();
		geomCollection = gf.createGeometryCollection(new Geometry[] { g1, g2, g4 });
		geomCollection = gf.createGeometryCollection(new Geometry[] { g3, g4,
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
			if (lastCall != null) {
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
