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
package org.gdms.sql.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.alphanumeric.AutoNumeric;
import org.gdms.sql.function.alphanumeric.Average;
import org.gdms.sql.function.alphanumeric.ConcatenateFunction;
import org.gdms.sql.function.alphanumeric.Count;
import org.gdms.sql.function.alphanumeric.IsUID;
import org.gdms.sql.function.alphanumeric.Max;
import org.gdms.sql.function.alphanumeric.Min;
import org.gdms.sql.function.alphanumeric.Pk;
import org.gdms.sql.function.alphanumeric.ReplaceString;
import org.gdms.sql.function.alphanumeric.StrLength;
import org.gdms.sql.function.alphanumeric.String2BooleanFunction;
import org.gdms.sql.function.alphanumeric.String2DateFunction;
import org.gdms.sql.function.alphanumeric.String2DoubleFunction;
import org.gdms.sql.function.alphanumeric.String2IntFunction;
import org.gdms.sql.function.alphanumeric.SubString;
import org.gdms.sql.function.alphanumeric.Sum;
import org.gdms.sql.function.alphanumeric.ToStringFunction;
import org.gdms.sql.function.math.Pi;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_3D;
import org.gdms.sql.function.spatial.geometry.convert.STO_ToMultiLine;
import org.gdms.sql.function.spatial.geometry.convert.STO_ToMultiPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_Boundary;
import org.gdms.sql.function.spatial.geometry.convert.ST_Centroid;
import org.gdms.sql.function.spatial.geometry.create.ST_MakeLine;
import org.gdms.sql.function.spatial.geometry.create.ST_MakePoint;
import org.gdms.sql.function.spatial.geometry.edition.STO_AddZToGeometry;
import org.gdms.sql.function.spatial.geometry.io.ST_AsWKT;
import org.gdms.sql.function.spatial.geometry.io.ST_GeomFromText;
import org.gdms.sql.function.spatial.geometry.operators.ST0_RingBuffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_Difference;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnion;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnionArg;
import org.gdms.sql.function.spatial.geometry.operators.ST_Intersection;
import org.gdms.sql.function.spatial.geometry.operators.ST_SymDifference;
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
import org.gdms.sql.function.spatial.geometry.properties.ST_Area;
import org.gdms.sql.function.spatial.geometry.properties.ST_ConvexHull;
import org.gdms.sql.function.spatial.geometry.properties.ST_Dimension;
import org.gdms.sql.function.spatial.geometry.properties.ST_Distance;
import org.gdms.sql.function.spatial.geometry.properties.ST_Extent;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryN;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryType;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsEmpty;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsSimple;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsValid;
import org.gdms.sql.function.spatial.geometry.properties.ST_Length;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumGeometries;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumInteriorRing;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumPoints;
import org.gdms.sql.function.spatial.geometry.properties.ST_X;
import org.gdms.sql.function.spatial.geometry.properties.ST_Y;
import org.gdms.sql.function.spatial.geometry.properties.ST_Z;
import org.gdms.sql.function.spatial.geometry.trigo.ST_Azimut;
import org.gdms.sql.function.spatial.mixed.ST_Envelope;
import org.gdms.sql.function.spatial.raster.STO_CropRaster;
import org.gdms.sql.function.statistics.Sqrt;
import org.gdms.sql.function.statistics.StandardDeviation;

public class FunctionManager {
	private static HashMap<String, Class<? extends Function>> nameFunction = new HashMap<String, Class<? extends Function>>();
	private static ArrayList<FunctionManagerListener> listeners = new ArrayList<FunctionManagerListener>();
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
		addFunction(ST_Buffer.class);
		addFunction(ST_Intersects.class);
		addFunction(ST_Contains.class);
		addFunction(ST_Intersection.class);
		addFunction(ST_GeomUnion.class);
		addFunction(ST_GeomFromText.class);
		addFunction(ST_AsWKT.class);
		addFunction(ST_Area.class);
		addFunction(ST_Length.class);
		addFunction(ST_NumPoints.class);
		addFunction(ST_Dimension.class);
		addFunction(ST_Force_3D.class);
		addFunction(ST_GeometryType.class);
		addFunction(ST_IsEmpty.class);
		addFunction(ST_IsSimple.class);
		addFunction(ST_Boundary.class);
		addFunction(ST_GeometryN.class);
		addFunction(ST_Equals.class);
		addFunction(ST_Centroid.class);
		addFunction(ST_Difference.class);
		addFunction(ST_SymDifference.class);
		addFunction(Average.class);
		addFunction(StandardDeviation.class);
		addFunction(ST_NumInteriorRing.class);
		addFunction(Sqrt.class);
		addFunction(STO_ToMultiPoint.class);
		addFunction(STO_ToMultiLine.class);
		addFunction(ST_IsValid.class);
		addFunction(ToStringFunction.class);
		addFunction(AutoNumeric.class);
		addFunction(Pk.class);
		addFunction(ST_IsWithin.class);
		addFunction(ST_IsWithinDistance.class);
		addFunction(ST_Relate.class);
		addFunction(ST_Touches.class);
		addFunction(ST_Disjoint.class);
		addFunction(ST_Crosses.class);
		addFunction(ST_Overlaps.class);
		addFunction(ST_GeomUnionArg.class);
		addFunction(ST_Extent.class);
		addFunction(ST_ConvexHull.class);
		addFunction(SubString.class);
		addFunction(ST_Envelope.class);
		addFunction(STO_CropRaster.class);
		addFunction(ST_MakePoint.class);
		addFunction(ST_MakeLine.class);
		addFunction(ReplaceString.class);
		addFunction(IsUID.class);
		addFunction(ST_NumGeometries.class);
		addFunction(ST_X.class);
		addFunction(ST_Y.class);
		addFunction(ST_Z.class);
		addFunction(ST_Distance.class);
		addFunction(ST0_RingBuffer.class);
		addFunction(STO_AddZToGeometry.class);
		addFunction(ST_Azimut.class);
		addFunction(Pi.class);
	}

	public static void addFunctionManagerListener(
			FunctionManagerListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the listener if it is present in the listener list
	 * 
	 * @param listener
	 * @return true if the listener was successfully removed. False if the
	 *         specified parameter was not a listener
	 */
	public static boolean removeFunctionManagerListener(
			FunctionManagerListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Add a new function to the SQL engine
	 * 
	 * @param function
	 *            function
	 * 
	 * @throws IllegalArgumentException
	 *             If the class is not a valid function implementation with an
	 *             empty constructor or there is already a function or custom
	 *             query with that name
	 */
	public static void addFunction(Class<? extends Function> functionClass)
			throws IllegalArgumentException {
		Function function;
		try {
			function = functionClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instantiate function: "
					+ functionClass, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot instantiate function: "
					+ functionClass, e);
		}
		String functionName = function.getName().toLowerCase();
		if (QueryManager.getQuery(functionName) != null) {
			throw new IllegalArgumentException(
					"A custom query already exists with that name:"
							+ functionName);
		}
		if (nameFunction.get(functionName) != null) {
			throw new IllegalArgumentException("Function " + functionName
					+ " already exists");
		}

		nameFunction.put(functionName, functionClass);

		fireFunctionAdded(functionName);
	}

	private static void fireFunctionAdded(String functionName) {
		for (FunctionManagerListener listener : listeners) {
			listener.functionAdded(functionName);
		}
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

	public static String[] getFunctionNames() {
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> it = nameFunction.keySet().iterator();
		while (it.hasNext()) {
			ret.add(it.next());
		}

		return (String[]) ret.toArray(new String[0]);
	}

	public static Class<? extends Function> remove(String functionName) {
		if (functionName != null) {
			Class<? extends Function> ret = nameFunction.remove(functionName
					.toLowerCase());
			if (ret != null) {
				fireFunctionRemoved(functionName);
			}
			return ret;
		} else {
			return null;
		}
	}

	private static void fireFunctionRemoved(String functionName) {
		for (FunctionManagerListener listener : listeners) {
			listener.functionRemoved(functionName);
		}
	}
}