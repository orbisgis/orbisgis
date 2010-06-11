package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
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

    public StringAttribute(JAXBElement<PropertyNameType> expr) {
        super(expr);
    }

    @Override
    public String getValue(Feature feat) throws ParameterException{ // TODO implement
        try {
            // TODO implement
            return getFieldValue(feat).getAsString();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\"");
        }
    }
}
