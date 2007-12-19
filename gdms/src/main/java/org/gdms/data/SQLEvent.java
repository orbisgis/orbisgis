package org.gdms.data;

/**
 * Event containing the information about the execution of an SQL instruction
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class SQLEvent {

	private String sql;

	private DataSourceFactory factory;

	public SQLEvent(String sql, DataSourceFactory factory) {
		this.sql = sql;
		this.factory = factory;
	}

	/**
	 * Gets the DataSourceFactory that executed the SQL
	 *
	 * @return
	 */
	public DataSourceFactory getFactory() {
		return factory;
	}

	/**
	 * Gets the executed SQL
	 *
	 * @return
	 */
	public String getSQL() {
		return sql;
	}

}
