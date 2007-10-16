package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.values.Value;

/**
 * Interface to implement by the custom queries
 *
 * @author Fernando Gonzalez Cortes
 */
public interface CustomQuery {
	/**
	 * Executes the custom query
	 * @param dsf data source fáctory
	 * @param tables
	 *            tables involved in the query
	 * @param values
	 *            values passed to the query
	 *
	 * @return DataSource result of the query
	 *
	 * @throws ExecutionException
	 *             if the custom query execution fails
	 */
	public DataSource evaluate(DataSourceFactory dsf,
			DataSource[] tables, Value[] values) throws ExecutionException;

	/**
	 * Gets the query name. Must ve a valid SQL identifier (i.e.: '.' is not
	 * allowed)
	 *
	 * @return query name
	 */
	public String getName();
}
