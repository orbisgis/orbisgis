package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class RealAttribute extends PropertyName implements RealParameter{

	private Double min;
	private Double max;

	public RealAttribute(){
	}

    public RealAttribute(String fieldName, DataSource ds) throws DriverException{
        super(fieldName, ds);
    }

    public RealAttribute(JAXBElement<PropertyNameType> expr) {
        super(expr);
    }

    @Override
    public double getValue(Feature feat) throws ParameterException{
        try{
            return this.getFieldValue(feat).getAsDouble();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ fieldName +"\"");
        }
    }

	@Override
	public void setMinValue(Double min) {
		this.min = min;
	}

	@Override
	public void setMaxValue(Double max) {
		this.max = max;
	}
}
