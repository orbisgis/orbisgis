package org.gdms.data;

public class GDBMSEvent {
	private DataSource dataSource;

	public GDBMSEvent(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
