package org.gdms.data;


public abstract class AbstractDataSourceCreation implements DataSourceCreation {

    private DataSourceFactory dataSourceFactory;

    public void setDataSourceFactory(DataSourceFactory dsf) {
        this.dataSourceFactory = dsf;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
    
}
