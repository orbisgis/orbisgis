package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class RealAttribute extends PropertyName implements RealParameter{

	private RealParameterContext ctx;

	public RealAttribute(){
		ctx = RealParameterContext.realContext;
	}

    public RealAttribute(String fieldName) {
        super(fieldName);
		ctx = RealParameterContext.realContext;
    }

    public RealAttribute(JAXBElement<PropertyNameType> expr) throws InvalidStyle {
        super(expr);
		ctx = RealParameterContext.realContext;
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
	public String toString(){
		return "<" + this.fieldName + ">";
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public RealParameterContext getContext() {
		return ctx;
	}
}
