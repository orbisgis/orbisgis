package org.gdms.data;

public abstract class AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private DataSourceFactory dsf;

	public void freeResources(String name)
			throws DataSourceFinalizationException {
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

}
