package org.gdms.sql.customQuery;

import java.util.HashMap;

import org.gdms.sql.customQuery.utility.ShowCall;

/**
 * Manages the custom queries
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class QueryManager {
	private static HashMap<String, CustomQuery> queries = new HashMap<String, CustomQuery>();

	static {
		registerQuery(new RegisterCall());
		registerQuery(new ShowCall());
		registerQuery(new BuildSpatialIndexCall());
	}

	/**
	 * Registers a query
	 *
	 * @param query
	 *            Query to add to the manager.
	 *
	 * @throws RuntimeException
	 *             If a query with the name already exists
	 */
	public static void registerQuery(CustomQuery query) {
		String queryName = query.getName().toLowerCase();

		if (queries.get(queryName) != null) {
			throw new RuntimeException("Query already registered");
		}

		queries.put(queryName, query);
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

		return (CustomQuery) queries.get(queryName);
	}
}
