package org.gdms.data;

/**
 *
 */
public interface DataSourceDefinition {
    /**
     * Creates a DataSource with the information of this object
     *
     * @param tableName name of the DataSource
     * @param tableAlias alias of the DataSource
     *
     * @return DataSource
     */
    public DataSource createDataSource(String tableName, String tableAlias, String driverName) throws DataSourceCreationException;

    /**
     * if any, frees the resources taken when the DataSource was created
     * @param name DataSource registration name
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
