package org.gdms.data;

/**
 *
 */
public interface DataSourceDefinition {
    /**
     * Creates a InternalDataSource with the information of this object
     *
     * @param tableName name of the InternalDataSource
     * @param tableAlias alias of the InternalDataSource
     *
     * @return InternalDataSource
     */
    public InternalDataSource createDataSource(String tableName, String tableAlias, String driverName) throws DataSourceCreationException;

    /**
     * if any, frees the resources taken when the InternalDataSource was created
     * @param name InternalDataSource registration name
     *
     * @throws DataSourceFinalizationException If the operation fails
     */
    public void freeResources(String name) throws DataSourceFinalizationException;

    /**
     * Gives to the DataSourceDefinition a reference of the DataSourceFactory
     * where the DataSourceDefinition is registered
     *
     * @param dsf
     */
    public void setDataSourceFactory(DataSourceFactory dsf);
    
}
