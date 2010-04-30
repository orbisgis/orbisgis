package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;


public class StringAttribute extends PropertyName implements StringParameter{

    /**
     * 
     * @param fieldName
     * @param ds
     * @throws DriverException
     */
    public StringAttribute(String fieldName, DataSource ds) throws DriverException{
        super(fieldName, ds);
    }

    @Override
    public boolean dependsOnFeature(){
        return true;
    }


    @Override
    public String getValue(DataSource ds, int fid) throws ParameterException{ // TODO implement
        try {
            // TODO implement
            return getFieldValue(ds, fid).getAsString();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\"");
        }
    }
}
