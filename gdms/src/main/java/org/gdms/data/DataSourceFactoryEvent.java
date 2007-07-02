package org.gdms.data;

public class DataSourceFactoryEvent {

	private String name;

	private DataSourceFactory factory;

	private String newName;

	public DataSourceFactoryEvent(String name, DataSourceFactory factory) {
		this.name = name;
		this.factory = factory;
	}

	public DataSourceFactoryEvent(String dsName, DataSourceFactory factory,
			String newName) {
		this(dsName, factory);
		this.newName = newName;
	}

	public DataSourceFactory getFactory() {
		return factory;
	}

	public String getName() {
		return name;
	}

	public String getNewName() {
		return newName;
	}

}
