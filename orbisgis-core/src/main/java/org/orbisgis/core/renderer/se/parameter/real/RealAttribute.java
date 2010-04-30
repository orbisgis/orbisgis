package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class RealAttribute extends PropertyName implements RealParameter{

    public RealAttribute(String fieldName, DataSource ds) throws DriverException{
        super(fieldName, ds);
    }


    @Override
    public boolean dependsOnFeature(){
        return true;
    }

    @Override
    public double getValue(DataSource ds, int fid) throws ParameterException{
        try{
            return this.getFieldValue(ds, fid).getAsDouble();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\"");
        }
    }
}
