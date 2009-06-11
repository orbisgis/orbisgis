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
package org.gdms.sql.customQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.sql.customQuery.showAttributes.ShowCall;
import org.gdms.sql.customQuery.spatial.convert.Explode;
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
		registerQuery(Extrude.class);
		registerQuery(ShowCall.class);

		registerQuery(Explode.class);
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
					"There is already a function with that name");
		} else if (queries.get(queryName) != null) {
			throw new IllegalArgumentException("Query already registered: "
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
