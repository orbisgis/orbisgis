package org.orbisgis.core.renderer.se.parameter.color;


import java.awt.Color;
import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.ThreshholdsBelongToType;

import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;


public class Categorize2Color extends Categorize<ColorParameter, ColorLiteral> implements ColorParameter {

    public Categorize2Color(ColorParameter initialClass, ColorLiteral fallback, RealParameter lookupValue){
        super(initialClass, fallback, lookupValue);
    }

    public Categorize2Color(JAXBElement<CategorizeType> expr) {
        CategorizeType t = expr.getValue();

        this.fallbackValue = new ColorLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

        this.setClassValue(0, SeParameterFactory.createColorParameter(t.getFirstValue()));


        Iterator<JAXBElement<ParameterValueType>> it = t.getThresholdAndValue().iterator();

        // Fetch class values and thresholds
        while (it.hasNext()){
            RealParameter th = SeParameterFactory.createRealParameter(it.next().getValue());
            ColorParameter c = SeParameterFactory.createColorParameter(it.next().getValue());
            this.addClass(th,c);
        }

        if (t.getThreshholdsBelongTo() == ThreshholdsBelongToType.PRECEDING)
            this.setThresholdsPreceding();
        else
            this.setThresholdsSucceeding();

    }

    @Override
    public Color getColor(DataSource ds, long fid){
        try {
            return getParameter(ds, fid).getColor(ds, fid);
        } catch (ParameterException ex) {
            // fetch the fallback  value
            // it's a literal so no need to access the feature
            return this.fallbackValue.getColor(null, 0);
        }
    }

}
