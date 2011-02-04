package org.orbisgis.core.renderer.se.parameter.string;

import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.ThreshholdsBelongToType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class Categorize2String extends Categorize<StringParameter, StringLiteral> implements StringParameter {

    public Categorize2String(StringParameter initialClass, StringLiteral fallback, RealParameter lookupValue) {
        super(initialClass, fallback, lookupValue);
    }

    public Categorize2String(JAXBElement<CategorizeType> expr) throws InvalidStyle {
        CategorizeType t = expr.getValue();

        this.fallbackValue = new StringLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));


        Iterator<JAXBElement<ParameterValueType>> it = t.getThresholdAndValue().iterator();

        this.setClassValue(0, SeParameterFactory.createStringParameter(it.next().getValue()));

        // Fetch class values and thresholds
        while (it.hasNext()) {
            this.addClass(SeParameterFactory.createRealParameter(it.next().getValue()),
                    SeParameterFactory.createStringParameter(it.next().getValue()));
        }

        if (t.getThreshholdsBelongTo() == ThreshholdsBelongToType.PRECEDING) {
            this.setThresholdsPreceding();
        } else {
            this.setThresholdsSucceeding();
        }
        super.setPropertyFromJaxB(t);
    }

    @Override
    public String getValue(SpatialDataSourceDecorator sds, long fid) {
        try {
            return getParameter(sds, fid).getValue(sds, fid);
        } catch (ParameterException ex) {
            return this.fallbackValue.getValue( sds, fid);
        }
    }
}
