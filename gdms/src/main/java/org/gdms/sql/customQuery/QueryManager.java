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
package org.gdms.sql.customQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.sql.customQuery.cluster.kmeans.ST_KMeans;
import org.gdms.sql.customQuery.showAttributes.ShowCall;
import org.gdms.sql.customQuery.spatial.geometry.connectivity.ST_BlockIdentity;
import org.gdms.sql.customQuery.spatial.geometry.convert.ST_Explode;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_BigCreateGrid;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_CreateGrid;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_CreatePointsGrid;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_CreateWebGrid;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_Extrude;
import org.gdms.sql.customQuery.spatial.geometry.create.ST_RandomGeometry;
import org.gdms.sql.customQuery.spatial.geometry.other.ST_MainDirections;
import org.gdms.sql.customQuery.spatial.geometry.qa.ST_InternalGapFinder;
import org.gdms.sql.customQuery.spatial.geometry.topology.ST_Graph;
import org.gdms.sql.customQuery.spatial.geometry.topology.ST_PlanarGraph;
import org.gdms.sql.customQuery.spatial.geometry.topology.ST_ToLineNoder;
import org.gdms.sql.customQuery.spatial.raster.Interpolation.ST_Interpolate;
import org.gdms.sql.customQuery.spatial.raster.convert.ST_RasterToPoints;
import org.gdms.sql.customQuery.spatial.raster.convert.ST_RasterToPolygons;
import org.gdms.sql.customQuery.spatial.raster.convert.ST_RasterizeLine;
import org.gdms.sql.customQuery.spatial.raster.convert.ST_VectorizeLine;
import org.gdms.sql.customQuery.system.ExportCall;
import org.gdms.sql.customQuery.system.FunctionHelp;
import org.gdms.sql.customQuery.system.RegisterCall;
import org.gdms.sql.customQuery.system.RegisterFunction;
import org.gdms.sql.function.FunctionManager;

/**
 * Manages the custom queries
 * 
 * @author Fernando Gonzalez Cortes
 */
public class QueryManager {
	private static HashMap<String, Class<? extends CustomQuery>> queries = new HashMap<String, Class<? extends CustomQuery>>();
	private static ArrayList<QueryManagerListener> listeners = new ArrayList<QueryManagerListener>();

	static {
		registerQuery(RegisterCall.class);
                registerQuery(ExportCall.class);
		registerQuery(ST_Extrude.class);
		registerQuery(ShowCall.class);
		registerQuery(RegisterFunction.class);
		registerQuery(ST_Explode.class);
		registerQuery(FunctionHelp.class);
		registerQuery(ST_CreateGrid.class);
		registerQuery(ST_CreateWebGrid.class);
		registerQuery(ST_BigCreateGrid.class);
		registerQuery(ST_RandomGeometry.class);
		registerQuery(ST_InternalGapFinder.class);
		registerQuery(ST_ToLineNoder.class);
		registerQuery(ST_PlanarGraph.class);
		registerQuery(ST_RasterizeLine.class);
		registerQuery(ST_RasterToPoints.class);
		registerQuery(ST_RasterToPolygons.class);
		registerQuery(ST_VectorizeLine.class);
		registerQuery(ST_CreatePointsGrid.class);
		registerQuery(ST_Interpolate.class);
		registerQuery(ST_KMeans.class);
		registerQuery(ST_Graph.class);
		registerQuery(ST_MainDirections.class);
                registerQuery(ST_BlockIdentity.class);
	}

	/**
	 * Registers a query
	 * 
	 * @param query
	 *            Query to add to the manager.
	 * 
	 * @throws RuntimeException
	 *             If a query with the name already exists, or the class is not
	 *             a valid implementation of {@link CustomQuery} with a default
	 *             constructor
	 */
	public static void registerQuery(Class<? extends CustomQuery> queryClass) {
		CustomQuery query = null;
		try {
			query = queryClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instantiate query: "
					+ queryClass);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot instantiate query: "
					+ queryClass);
		}
		String queryName = query.getName().toLowerCase();

		if (FunctionManager.getFunction(queryName) != null) {
			throw new IllegalArgumentException(
					"There is already a function with the name  " + queryName);
		} else if (queries.get(queryName) != null) {
			throw new IllegalArgumentException("Query already registered : "
					+ queryName);
		} else {
			queries.put(queryName, queryClass);
			fireQueryAdded(queryName);
		}
	}

	private static void fireQueryAdded(String queryName) {
		for (QueryManagerListener listener : listeners) {
			listener.queryAdded(queryName);
		}
	}

	/**
	 * Gets the query by name
	 * 
	 * @param queryName
	 *            Name of the query
	 * 
	 * @return An instance of the query
	 */
	public static CustomQuery getQuery(String queryName) {
		queryName = queryName.toLowerCase();

		try {
			Class<? extends CustomQuery> queryClass = queries.get(queryName);
			if (queryClass != null) {
				return queryClass.newInstance();
			} else {
				return null;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException("bug!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("bug!", e);
		}
	}

	public static String[] getQueryNames() {
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> it = queries.keySet().iterator();
		while (it.hasNext()) {
			ret.add(it.next());
		}

		return (String[]) ret.toArray(new String[0]);
	}

	public static Class<? extends CustomQuery> remove(String queryName) {
		if (queryName != null) {
			Class<? extends CustomQuery> ret = queries.remove(queryName
					.toLowerCase());
			if (ret != null) {
				fireQueryRemoved(queryName);
			}
			return ret;
		} else {
			return null;
		}
	}

	private static void fireQueryRemoved(String queryName) {
		for (QueryManagerListener listener : listeners) {
			listener.queryRemoved(queryName);
		}
	}

	public static void addQueryManagerListener(QueryManagerListener listener) {
		listeners.add(listener);
	}

	public static boolean removeQueryManagerListener(
			QueryManagerListener listener) {
		return listeners.remove(listener);
	}
}
