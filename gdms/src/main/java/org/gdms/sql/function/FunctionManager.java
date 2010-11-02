/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
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
import org.gdms.sql.function.alphanumeric.LongToDoubleFunction;
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
import org.gdms.sql.function.math.ACos;
import org.gdms.sql.function.math.ASin;
import org.gdms.sql.function.math.ATan;
import org.gdms.sql.function.math.Ceil;
import org.gdms.sql.function.math.Cos;
import org.gdms.sql.function.math.Exp;
import org.gdms.sql.function.math.Floor;
import org.gdms.sql.function.math.Log;
import org.gdms.sql.function.math.Pi;
import org.gdms.sql.function.math.Random;
import org.gdms.sql.function.math.Sin;
import org.gdms.sql.function.math.Sqrt;
import org.gdms.sql.function.math.StandardDeviation;
import org.gdms.sql.function.math.Tan;
import org.gdms.sql.function.math.ToDegrees;
import org.gdms.sql.function.math.ToRadians;
import org.gdms.sql.function.spatial.geometry.affineTransformation.ST_Rotate;
import org.gdms.sql.function.spatial.geometry.affineTransformation.ST_Scale;
import org.gdms.sql.function.spatial.geometry.convert.ST_Centroid;
import org.gdms.sql.function.spatial.geometry.convert.ST_EndPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_3D;
import org.gdms.sql.function.spatial.geometry.convert.ST_InteriorPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_PointN;
import org.gdms.sql.function.spatial.geometry.convert.ST_PointsToLine;
import org.gdms.sql.function.spatial.geometry.convert.ST_StartPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiLine;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.create.ST_AddPoint;
import org.gdms.sql.function.spatial.geometry.create.ST_AddPoints;
import org.gdms.sql.function.spatial.geometry.create.ST_Boundary;
import org.gdms.sql.function.spatial.geometry.create.ST_BoundingCircle;
import org.gdms.sql.function.spatial.geometry.create.ST_Densify;
import org.gdms.sql.function.spatial.geometry.create.ST_MakeLine;
import org.gdms.sql.function.spatial.geometry.create.ST_MakePoint;
import org.gdms.sql.function.spatial.geometry.create.ST_MinimumRectangle;
import org.gdms.sql.function.spatial.geometry.create.ST_OctogonalEnvelope;
import org.gdms.sql.function.spatial.geometry.create.ST_RemoveRepeatedPoints;
import org.gdms.sql.function.spatial.geometry.crs.ST_Transform;
import org.gdms.sql.function.spatial.geometry.distance.ST_NearestPoints;
import org.gdms.sql.function.spatial.geometry.edit.ST_AddZToGeometry;
import org.gdms.sql.function.spatial.geometry.edit.ST_Normalize;
import org.gdms.sql.function.spatial.geometry.edit.ST_Reverse;
import org.gdms.sql.function.spatial.geometry.edit.ST_Snap;
import org.gdms.sql.function.spatial.geometry.io.ST_AsWKT;
import org.gdms.sql.function.spatial.geometry.io.ST_GeomFromText;
import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_Difference;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnion;
import org.gdms.sql.function.spatial.geometry.operators.ST_GeomUnionArg;
import org.gdms.sql.function.spatial.geometry.operators.ST_Intersection;
import org.gdms.sql.function.spatial.geometry.operators.ST_RingBuffer;
import org.gdms.sql.function.spatial.geometry.operators.ST_SymDifference;
import org.gdms.sql.function.spatial.geometry.other.ST_MeanSpacing;
import org.gdms.sql.function.spatial.geometry.polygonize.ST_Polygonize;
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
import org.gdms.sql.function.spatial.geometry.properties.ST_CircleCompacity;
import org.gdms.sql.function.spatial.geometry.properties.ST_ConvexHull;
import org.gdms.sql.function.spatial.geometry.properties.ST_Dimension;
import org.gdms.sql.function.spatial.geometry.properties.ST_Distance;
import org.gdms.sql.function.spatial.geometry.properties.ST_Extent;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryN;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryType;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsClosed;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsEmpty;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsRectangle;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsSimple;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsValid;
import org.gdms.sql.function.spatial.geometry.properties.ST_Length;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumGeometries;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumInteriorRing;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumPoints;
import org.gdms.sql.function.spatial.geometry.properties.ST_X;
import org.gdms.sql.function.spatial.geometry.properties.ST_Y;
import org.gdms.sql.function.spatial.geometry.properties.ST_Z;
import org.gdms.sql.function.spatial.geometry.simplify.ST_Simplify;
import org.gdms.sql.function.spatial.geometry.simplify.ST_SimplifyPreserveTopology;
import org.gdms.sql.function.spatial.geometry.trigo.ST_Azimut;
import org.gdms.sql.function.spatial.mixed.ST_Envelope;
import org.gdms.sql.function.spatial.mixed.ST_SetZ;
import org.gdms.sql.function.spatial.raster.algebra.ST_RasterAlgebra;
import org.gdms.sql.function.spatial.raster.create.ST_CropRaster;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8Accumulation;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8AllOutlets;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8ConstrainedAccumulation;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8Direction;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8RiverDistance;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8Slope;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8StrahlerStreamOrder;
import org.gdms.sql.function.spatial.raster.hydrology.ST_D8Watershed;
import org.gdms.sql.function.spatial.raster.hydrology.ST_FillSinks;
import org.gdms.sql.function.spatial.raster.hydrology.ST_LSFactor;
import org.gdms.sql.function.spatial.raster.hydrology.ST_StreamPowerIndex;
import org.gdms.sql.function.spatial.raster.hydrology.ST_WetnessIndex;
import org.gdms.sql.function.spatial.raster.morphology.ST_Shadow;
import org.gdms.sql.function.spatial.raster.properties.ST_Count;

public class FunctionManager {
	private static HashMap<String, Class<? extends Function>> nameFunction = new HashMap<String, Class<? extends Function>>();
	private static ArrayList<FunctionManagerListener> listeners = new ArrayList<FunctionManagerListener>();
	static {
		addFunction(ConcatenateFunction.class);
		addFunction(String2DateFunction.class);
		addFunction(String2IntFunction.class);
		addFunction(String2DoubleFunction.class);
		addFunction(LongToDoubleFunction.class);
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
		addFunction(ST_ToMultiPoint.class);
		addFunction(ST_ToMultiLine.class);
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
		addFunction(ST_CropRaster.class);
		addFunction(ST_MakePoint.class);
		addFunction(ST_MakeLine.class);
		addFunction(ReplaceString.class);
		addFunction(IsUID.class);
		addFunction(ST_NumGeometries.class);
		addFunction(ST_X.class);
		addFunction(ST_Y.class);
		addFunction(ST_Z.class);
		addFunction(ST_Distance.class);
		addFunction(ST_RingBuffer.class);
		addFunction(ST_AddZToGeometry.class);
		addFunction(ST_Azimut.class);
		addFunction(Pi.class);
		addFunction(ST_Densify.class);
		addFunction(ST_Scale.class);
		addFunction(ST_Rotate.class);
		addFunction(ST_BoundingCircle.class);
		addFunction(ST_MinimumRectangle.class);
		addFunction(ST_OctogonalEnvelope.class);
		addFunction(ST_NearestPoints.class);
		addFunction(ST_CircleCompacity.class);
		addFunction(ST_IsClosed.class);
		addFunction(ST_Simplify.class);
		addFunction(ST_SimplifyPreserveTopology.class);
		addFunction(ST_Polygonize.class);
		addFunction(ST_Reverse.class);
		addFunction(ST_Normalize.class);
		addFunction(ST_IsRectangle.class);
		addFunction(ST_Snap.class);
		addFunction(ST_PointsToLine.class);
		addFunction(ST_D8Accumulation.class);
		addFunction(ST_D8AllOutlets.class);
		addFunction(ST_D8ConstrainedAccumulation.class);
		addFunction(ST_D8Direction.class);
		addFunction(ST_D8RiverDistance.class);
		addFunction(ST_D8Slope.class);
		addFunction(ST_D8StrahlerStreamOrder.class);
		addFunction(ST_D8Watershed.class);
		addFunction(ST_FillSinks.class);
		addFunction(ST_LSFactor.class);
		addFunction(ST_StreamPowerIndex.class);
		addFunction(ST_WetnessIndex.class);
		addFunction(ST_InteriorPoint.class);
		addFunction(ST_SetZ.class);
		addFunction(ST_RasterAlgebra.class);
		addFunction(ST_Count.class);
		addFunction(ST_Shadow.class);
		addFunction(ST_ToMultiSegments.class);
		addFunction(ST_RemoveRepeatedPoints.class);
		addFunction(ST_AddPoint.class);
		addFunction(ST_AddPoints.class);
		addFunction(Log.class);
		addFunction(Tan.class);
		addFunction(ATan.class);
		addFunction(ACos.class);
		addFunction(Cos.class);
		addFunction(Sin.class);
		addFunction(ASin.class);
		addFunction(Exp.class);
		addFunction(Floor.class);
		addFunction(Ceil.class);
		addFunction(Random.class);
		addFunction(ToDegrees.class);
		addFunction(ToRadians.class);
		addFunction(ST_StartPoint.class);
		addFunction(ST_EndPoint.class);
		addFunction(ST_PointN.class);
		addFunction(ST_MeanSpacing.class);
		addFunction(ST_Transform.class);
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