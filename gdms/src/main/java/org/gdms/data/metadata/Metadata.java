package org.gdms.data.metadata;

import org.gdms.driver.DriverException;

/**
 * Gets information about the DataSource
 */
public interface Metadata {
    /**
     * @return Number of fields
     */
    public int getFieldCount() throws DriverException;

    /**
     * Gets the type of the specified field
     * 
     * @param fieldId index of the field
     * 
     * @return
     */
    public int getFieldType(int fieldId) throws DriverException;

    /**
     * Gets the name of the specified field
     * 
     * @param fieldId index of the field
     * 
     * @return String
     */
    public String getFieldName(int fieldId) throws DriverException;
    
    /**
     * Gets the names of the primary key fields
     * 
     * @return
     */
    public String[] getPrimaryKey() throws DriverException;

    /**
     * @param fieldId index of the field
     * 
     * @return True if the field is read only, false if not and null
     * if the underlying format does not have information about 
     */
    public Boolean isReadOnly(int fieldId) throws DriverException;
}
