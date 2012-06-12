package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * A categorization from {@code RealParameter} to {@code ColorParamter}
 * @author alexis, maxence
 */
public class Categorize2Color extends Categorize<ColorParameter, ColorLiteral> implements ColorParameter {

        /**
         * Build a new {@code Categorize2Color} with the given parameters. Built using 
         * {@link Categorize#Categorize(org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.real.RealParameter) Categorize}
         * @param initialClass
         * The value of the first class.
         * @param fallback
         * The default value if an input can't be processed.
         * @param lookupValue 
         * The {@code RealParameter} used to retrieve the input values.
         */
    public Categorize2Color(ColorParameter initialClass, ColorLiteral fallback, RealParameter lookupValue) {
        super(initialClass, fallback, lookupValue);
    }

        /**
         * Build a new {@code Categorize2Color} from a JAXB element.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
    public Categorize2Color(JAXBElement<CategorizeType> expr) throws InvalidStyle {
        CategorizeType t = expr.getValue();

        this.setFallbackValue(new ColorLiteral(t.getFallbackValue()));
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

        Iterator<Object> it = t.getThresholdAndValue().iterator();


        this.setClassValue(0, SeParameterFactory.createColorParameter((ParameterValueType)it.next()));

        // Fetch class values and thresholds
        while (it.hasNext()) {
            RealLiteral th =(RealLiteral) SeParameterFactory.createRealParameter((LiteralType)(it.next()));
            ColorParameter c = SeParameterFactory.createColorParameter((ParameterValueType)it.next());
            this.addClass(th, c);
        }

        if (t.getThresholdBelongsTo() == ThresholdBelongsToType.PRECEDING) {
            this.setThresholdsPreceding();
        } else {
            this.setThresholdsSucceeding();
        }

        super.setPropertyFromJaxB(t);
    }

    @Override
    public Color getColor(DataSource sds, long fid) throws ParameterException {

        if (sds == null) {
            throw new ParameterException("No feature");
        }

        return getParameter(sds, fid).getColor(sds, fid);
    }
}
