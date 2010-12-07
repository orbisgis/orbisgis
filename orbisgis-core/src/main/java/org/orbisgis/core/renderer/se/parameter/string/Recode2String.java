package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public class Recode2String extends Recode<StringParameter, StringLiteral> implements StringParameter {

    public Recode2String(StringLiteral fallback, StringParameter lookupValue) {
        super(fallback, lookupValue);
    }

    public Recode2String(JAXBElement<RecodeType> expr) throws InvalidStyle {
        RecodeType t = expr.getValue();

        this.fallbackValue = new StringLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

        for (MapItemType mi : t.getMapItem()) {
            this.addMapItem(mi.getKey(), SeParameterFactory.createStringParameter(mi.getValue()));
        }
    }

    @Override
    public String getValue(Feature feat) {
        try {
            return getParameter(feat).getValue(feat);
        } catch (ParameterException ex) {
            return this.fallbackValue.getValue( feat);
        }
    }
}
