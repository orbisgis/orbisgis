package org.gdms.data;

public class SQLEvent {

	private String sql;

	private DataSourceFactory factory;

	public SQLEvent(String sql, DataSourceFactory factory) {
		this.sql = sql;
		this.factory = factory;
	}

	public DataSourceFactory getFactory() {
		return factory;
	}

	public String getSQL() {
		return sql;
	}

}
