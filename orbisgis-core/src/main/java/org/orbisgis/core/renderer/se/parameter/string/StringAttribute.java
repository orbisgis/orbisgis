package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ValueReferenceType;
import org.gdms.data.SpatialDataSourceDecorator;

import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;

import org.gdms.driver.DriverException;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

public class StringAttribute extends ValueReference implements StringParameter{

    /**
     * 
     * @param fieldName
     * @param ds
     * @throws DriverException
     */
    public StringAttribute(String fieldName) {
        super(fieldName);
    }

    public StringAttribute(JAXBElement<ValueReferenceType> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public String getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException{ // TODO implement
        try {
			Value fieldValue = getFieldValue(sds, fid);
			return fieldValue.toString();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\" (" + e + ")");
        }
    }
}
