package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.RecodeType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * <code>Recode</code> implementation that maps input values to color values.
 * @author maxence, alexis
 */
public class Recode2Color extends Recode<ColorParameter, ColorLiteral> implements ColorParameter {

        /**
         * Creates a new instance of <code>Recode2Color</code>. The default result value
         * will be <code>fallback</code>, and the values that need to be processed
         * will be retrieved using <code>lookupValue</code>
         * @param fallback
         * @param lookupValue 
         */
        public Recode2Color(ColorLiteral fallback, StringParameter lookupValue) {
                super(fallback, lookupValue);
        }

        /**
         * Creates a new instance of <code>Recode2Color</code>. All the needed objects
         * will be created using the JAXB element given in parameter. Particularly,
         * the <code>MapItem</code>s used in the current recode will be retrieved 
         * from this XML representation.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Recode2Color(JAXBElement<RecodeType> expr) throws InvalidStyle {
                RecodeType t = expr.getValue();

                this.setFallbackValue(new ColorLiteral(t.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

                for (MapItemType mi : t.getMapItem()) {
                        this.addMapItem(new StringLiteral(mi.getKey().getContent().get(0).toString()),
                                SeParameterFactory.createColorParameter(mi.getValue()));
                }
        }

        @Override
        public Color getColor(DataSource sds, long fid) throws ParameterException {
                //If we can't retrieve any information in sds, getParameter will provide a 
                //default value, so we won't obtain any error.
                return getParameter(sds, fid).getColor(sds, fid);
        }
}
