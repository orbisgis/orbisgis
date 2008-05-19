/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function;

import java.util.HashMap;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.alphanumeric.AutoNumeric;
import org.gdms.sql.function.alphanumeric.Average;
import org.gdms.sql.function.alphanumeric.ConcatenateFunction;
import org.gdms.sql.function.alphanumeric.Count;
import org.gdms.sql.function.alphanumeric.Max;
import org.gdms.sql.function.alphanumeric.Min;
import org.gdms.sql.function.alphanumeric.Pk;
import org.gdms.sql.function.alphanumeric.StrLength;
import org.gdms.sql.function.alphanumeric.String2BooleanFunction;
import org.gdms.sql.function.alphanumeric.String2DateFunction;
import org.gdms.sql.function.alphanumeric.String2DoubleFunction;
import org.gdms.sql.function.alphanumeric.String2IntFunction;
import org.gdms.sql.function.alphanumeric.Sum;
import org.gdms.sql.function.spatial.convert.Boundary;
import org.gdms.sql.function.spatial.convert.Centroid;
import org.gdms.sql.function.spatial.convert.Constraint3D;
import org.gdms.sql.function.spatial.convert.ToMultiLine;
import org.gdms.sql.function.spatial.convert.ToMultiPoint;
import org.gdms.sql.function.spatial.geometryProperties.Area;
import org.gdms.sql.function.spatial.geometryProperties.Dimension;
import org.gdms.sql.function.spatial.geometryProperties.GeometryN;
import org.gdms.sql.function.spatial.geometryProperties.GeometryType;
import org.gdms.sql.function.spatial.geometryProperties.GetZValue;
import org.gdms.sql.function.spatial.geometryProperties.IsEmpty;
import org.gdms.sql.function.spatial.geometryProperties.IsSimple;
import org.gdms.sql.function.spatial.geometryProperties.IsValid;
import org.gdms.sql.function.spatial.geometryProperties.Length;
import org.gdms.sql.function.spatial.geometryProperties.NumPoints;
import org.gdms.sql.function.spatial.io.AsWKT;
import org.gdms.sql.function.spatial.io.GeomFromText;
import org.gdms.sql.function.spatial.operators.Buffer;
import org.gdms.sql.function.spatial.operators.Difference;
import org.gdms.sql.function.spatial.operators.GeomUnion;
import org.gdms.sql.function.spatial.operators.Intersection;
import org.gdms.sql.function.spatial.operators.SymDifference;
import org.gdms.sql.function.spatial.predicates.Contains;
import org.gdms.sql.function.spatial.predicates.Equals;
import org.gdms.sql.function.spatial.predicates.Intersects;
import org.gdms.sql.function.statistics.Sqrt;
import org.gdms.sql.function.statistics.StandardDeviation;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class FunctionManager {
	private static HashMap<String, Class<? extends Function>> nameFunction = new HashMap<String, Class<? extends Function>>();
	static {
		addFunction(ConcatenateFunction.class);
		addFunction(String2DateFunction.class);
		addFunction(String2IntFunction.class);
		addFunction(String2DoubleFunction.class);
		addFunction(String2BooleanFunction.class);
		addFunction(Count.class);
		addFunction(Sum.class);
		addFunction(StrLength.class);
		addFunction(Max.class);
		addFunction(Min.class);
		addFunction(Buffer.class);
		addFunction(Intersects.class);
		addFunction(Contains.class);
		addFunction(Intersection.class);
		addFunction(GeomUnion.class);		
		addFunction(GeomFromText.class);
		addFunction(AsWKT.class);
		addFunction(Area.class);
		addFunction(Length.class);
		addFunction(NumPoints.class);
		addFunction(Dimension.class);
		addFunction(Constraint3D.class);
		addFunction(GeometryType.class);
		addFunction(IsEmpty.class);
		addFunction(IsSimple.class);
		addFunction(Boundary.class);
		addFunction(GeometryN.class);
		addFunction(Equals.class);
		addFunction(GetZValue.class);
		addFunction(Centroid.class);
		addFunction(Difference.class);
		addFunction(SymDifference.class);
		addFunction(Average.class);
		addFunction(StandardDeviation.class);

		addFunction(Sqrt.class);
		addFunction(ToMultiPoint.class);
		addFunction(ToMultiLine.class);
		addFunction(IsValid.class);

		addFunction(AutoNumeric.class);
		addFunction(Pk.class);
	}

	/**
	 * Add a new function to the SQL engine
	 *
	 * @param function
	 *            function
	 *
	 * @throws RuntimeException
	 *
	 */
	public static void addFunction(Class<? extends Function> functionClass) {
		Function function;
		try {
			function = functionClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("bug!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("bug!", e);
		}
		String functionName = function.getName().toLowerCase();
		if (QueryManager.getQuery(functionName) != null) {
			throw new RuntimeException(
					"A custom query already exists with that name:"
							+ functionName);
		}
		if (nameFunction.get(functionName) != null) {
			throw new RuntimeException("Function " + functionName
					+ " already exists");
		}

		nameFunction.put(functionName, functionClass);
	}

	/**
	 * Gets the function which name is equal to the parameter
	 *
	 * @param name
	 *
	 * @return a new function instance or null if there is no function with that
	 *         name
	 */
	public static Function getFunction(String name) {
		Class<? extends Function> func = nameFunction.get(name.toLowerCase());

		if (func == null) {
			return null;
		} else {
			Function ret;
			try {
				ret = func.newInstance();
				if (ret == null) {
					throw new RuntimeException("Bad clone method for " + name);
				} else {
					return ret;
				}
			} catch (InstantiationException e) {
				throw new RuntimeException("bug!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("bug!", e);
			}
		}
	}
}