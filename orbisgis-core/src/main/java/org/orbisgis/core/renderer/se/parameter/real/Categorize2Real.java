package org.orbisgis.core.renderer.se.parameter.real;

import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.ThreshholdsBelongToType;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public final class Categorize2Real extends Categorize<RealParameter, RealLiteral> implements RealParameter {

	private Double min;
	private Double max;

    public Categorize2Real(RealParameter initialClass, RealLiteral fallback, RealParameter lookupValue){
        super(initialClass, fallback, lookupValue);
		this.setMinValue(null);
		this.setMaxValue(null);
    }

    public Categorize2Real(JAXBElement<CategorizeType> expr) {
        CategorizeType t = expr.getValue();

        this.fallbackValue = new RealLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));


        Iterator<JAXBElement<ParameterValueType>> it = t.getThresholdAndValue().iterator();

        this.setClassValue(0, SeParameterFactory.createRealParameter(it.next().getValue()));

        // Fetch class values and thresholds
        while (it.hasNext()){
            this.addClass(SeParameterFactory.createRealParameter(it.next().getValue()),
                    SeParameterFactory.createRealParameter(it.next().getValue()));
        }

        if (t.getThreshholdsBelongTo() == ThreshholdsBelongToType.PRECEDING)
            this.setThresholdsPreceding();
        else
            this.setThresholdsSucceeding();
             
    }

    @Override
    public double getValue(Feature feat){
        try{
            return getParameter(feat).getValue(feat);
        }
        catch(ParameterException ex){
            return this.fallbackValue.getValue(feat);
        }
    }


	@Override
	public void setClassValue(int i, RealParameter value){
		super.setClassValue(i, value);
		value.setMinValue(min);
		value.setMaxValue(max);
	}

	@Override
	public void setFallbackValue(RealLiteral l){
		super.setFallbackValue(l);
		l.setMinValue(min);
		l.setMaxValue(max);
	}



	@Override
	public void setMinValue(Double min) {
		this.min = min;
		this.fallbackValue.setMinValue(min);

		for (int i=0; i<this.getNumClasses();i++){
			RealParameter classValue = this.getClassValue(i);
			classValue.setMinValue(min);
		}
	}

	@Override
	public void setMaxValue(Double max) {
		this.max = max;
		this.fallbackValue.setMaxValue(max);
		for (int i=0; i<this.getNumClasses();i++){
			RealParameter classValue = this.getClassValue(i);
			classValue.setMaxValue(max);
		}
	}
}
