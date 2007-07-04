package org.gdms.data;

public class DataSourceFactoryEvent {

	private String name;

	private DataSourceFactory factory;

	private String newName;

	private Boolean wellKnownName;

	public DataSourceFactoryEvent(String name, Boolean invalid, DataSourceFactory factory) {
		this.name = name;
		this.factory = factory;
		this.wellKnownName = invalid;
	}

	public DataSourceFactoryEvent(String dsName, DataSourceFactory factory,
			String newName) {
		this(dsName, false, factory);
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

	public Boolean isWellKnownName() {
		return wellKnownName;
	}

}
