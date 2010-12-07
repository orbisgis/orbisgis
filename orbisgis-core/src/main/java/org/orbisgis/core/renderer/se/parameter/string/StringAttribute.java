package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;

import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;

import org.gdms.driver.DriverException;

import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class StringAttribute extends PropertyName implements StringParameter{

    /**
     * 
     * @param fieldName
     * @param ds
     * @throws DriverException
     */
    public StringAttribute(String fieldName) {
        super(fieldName);
    }

    public StringAttribute(JAXBElement<PropertyNameType> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public String getValue(Feature feat) throws ParameterException{ // TODO implement
        try {
			Value fieldValue = getFieldValue(feat);
			return fieldValue.toString();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\" (" + e + ")");
        }
    }
}
