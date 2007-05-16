package org.gdms.data.metadata;

import java.util.HashMap;

import org.gdms.driver.DriverException;


/**
 * Defines the schema of a source in driver specific terms
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface DriverMetadata {
    
    /**
     * Gets the number of fields
     * 
     * @return
     * @throws DriverException 
     */
    public int getFieldCount() throws DriverException;
    
    /**
     * Gets the name of the field.
     * 
     * @param fieldId
     * @return
     */
    public String getFieldName(int fieldId) throws DriverException;
    
    /**
     * Gets the name of the field type. It should be one
     * of the values returned by the getAvailableTypes method
     * in the driver
     * 
     * @return
     */
    public String getFieldType(int fieldId) throws DriverException;
    
    /**
     * 
     * @return
     */
    public String getFieldParam(int fieldId, String paramName) throws DriverException;

    /**
     * Gets the param names for the specified field
     * 
     * @param fieldId
     * @return
     * @throws DriverException 
     */
    public String[] getParamNames(int fieldId) throws DriverException;

    /**
     * Gets the param values for the specified field
     * 
     * @param fieldId
     * @return
     * @throws DriverException 
     */
    public String[] getParamValues(int fieldId) throws DriverException;

    /**
     * Gets the parameters for the specified field
     * 
     * @return
     * @throws DriverException 
     */
    public HashMap<String, String> getFieldParams(int fieldId) throws DriverException;
    
    /**
     * Gets the names of the fields which are primary key. An array of
     * zero elements if there isn't any primary key
     * 
     * @return
     * @throws DriverException 
     */
    public String[] getPrimaryKeys() throws DriverException;
}
