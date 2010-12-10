package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

public class Recode2Color extends Recode<ColorParameter, ColorLiteral> implements ColorParameter {

    public Recode2Color(ColorLiteral fallback, StringParameter lookupValue){
        super(fallback, lookupValue);
    }

    public Recode2Color(JAXBElement<RecodeType> expr) throws InvalidStyle {
        RecodeType t = expr.getValue();

        this.fallbackValue = new ColorLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

        for (MapItemType mi : t.getMapItem()){
            this.addMapItem(mi.getKey(), SeParameterFactory.createColorParameter(mi.getValue()));
        }
    }


    @Override
    public Color getColor(Feature feat) throws ParameterException{
		if (feat == null){
			throw new ParameterException("No feature");
		}
        return getParameter(feat).getColor(feat);
    }
}
