package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class RealAttribute extends PropertyName implements RealParameter{

    public RealAttribute(String fieldName, DataSource ds) throws DriverException{
        super(fieldName, ds);
    }

    public RealAttribute(JAXBElement<PropertyNameType> expr) {
        super(expr);
    }

    @Override
    public double getValue(DataSource ds, long fid) throws ParameterException{
        try{
            return this.getFieldValue(ds, (int)fid).getAsDouble();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\"");
        }
    }
}
