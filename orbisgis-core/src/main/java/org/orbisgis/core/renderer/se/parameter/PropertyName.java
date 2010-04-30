package org.orbisgis.core.renderer.se.parameter;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public abstract class PropertyName{

    public PropertyName(){
    }

    public PropertyName(String fieldName, DataSource ds) throws DriverException{
        setColumnName(fieldName, ds);
    }



    public void setColumnName(String fieldName, DataSource ds) throws DriverException{
        // look for field before assigning the name !
        this.fieldId = ds.getFieldIndexByName(fieldName);
        this.fieldName = fieldName;
    }

    public String getColumnName(){
        return fieldName;
    }

    public Value getFieldValue(DataSource ds, int fid) throws DriverException{
        return ds.getFieldValue(fid, fieldId);
    }

    protected String fieldName;
    private int fieldId;

}
