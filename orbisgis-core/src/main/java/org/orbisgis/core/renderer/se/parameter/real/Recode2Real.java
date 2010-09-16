package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.se.parameter.MapItem;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

public class Recode2Real extends Recode<RealParameter, RealLiteral> implements RealParameter {

	Double min;
	Double max;

    public Recode2Real(RealLiteral fallback, StringParameter lookupValue){
        super(fallback, lookupValue);
    }

    public Recode2Real(JAXBElement<RecodeType> expr) {
        RecodeType t = expr.getValue();

        this.fallbackValue = new RealLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

        for (MapItemType mi : t.getMapItem()){
            this.addMapItem(mi.getKey(), SeParameterFactory.createRealParameter(mi.getValue()));
        }
    }

    @Override
    public double getValue(Feature feat){
        // Should always depend on features !
        try{
            return getParameter(feat).getValue(feat);
        }
        catch(ParameterException ex){
            // Since fallback value is a literal, the following is secure
            return this.fallbackValue.getValue(feat);
        }
    }

	@Override
	public void addMapItem(String key, RealParameter p){
		p.setMaxValue(max);
		p.setMinValue(min);
		super.addMapItem(key, p);
	}

	@Override
	public void setMinValue(Double min) {
		this.min = min;
		this.getFallbackValue().setMinValue(min);
		for (MapItem<RealParameter> item : this.mapItems){
			item.getValue().setMinValue(min);
		}
	}

	@Override
	public void setMaxValue(Double max) {
		this.max = max;
		this.getFallbackValue().setMaxValue(max);
		for (MapItem<RealParameter> item : this.mapItems){
			item.getValue().setMaxValue(max);
		}
	}

}

