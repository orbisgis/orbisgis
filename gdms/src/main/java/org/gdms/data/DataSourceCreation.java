package org.gdms.data;

import org.gdms.driver.DriverException;

/**
 *
 */
public interface DataSourceCreation {
    /**
     * Creates the source
     * 
     * @throws DriverException if the source creation fails
     */
    public void create() throws DriverException;

    /**
     * Gives to the DataSourceDefinition a reference of the DataSourceFactory
     * where the DataSourceDefinition is registered
     * 
     * @param dsf Reference to the DataSourceFactory
     */
    public void setDataSourceFactory(DataSourceFactory dsf);
}
