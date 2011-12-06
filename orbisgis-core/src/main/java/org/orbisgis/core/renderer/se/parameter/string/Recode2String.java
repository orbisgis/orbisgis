package org.orbisgis.core.renderer.se.parameter.string;

import java.awt.Color;
import java.util.HashMap;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.RecodeType;
import org.gdms.data.DataSource;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * <code>Recode</code> implementation that maps input values to {@code String} values.
 * @author maxence, alexis
 */
public final class Recode2String extends Recode<StringParameter, StringLiteral> implements StringParameter {

        private String[] restriction;
        
        /**
         * Creates a new instance of <code>Recode2String</code>. The default result value
         * will be <code>fallback</code>, and the values that need to be processed
         * will be retrieved using <code>lookupValue</code>
         * @param fallback
         * @param lookupValue 
         */
        public Recode2String(StringLiteral fallback, StringParameter lookupValue) {
                super(fallback, lookupValue);
        }

        /**
         * Creates a new instance of <code>Recode2String</code>. All the needed objects
         * will be created using the JAXB element given in parameter. Particularly,
         * the <code>MapItem</code>s used in the current recode will be retrieved 
         * from this XML representation.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Recode2String(JAXBElement<RecodeType> expr) throws InvalidStyle {
                RecodeType t = expr.getValue();

                this.setFallbackValue(new StringLiteral(t.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createStringParameter(t.getLookupValue()));

                for (MapItemType mi : t.getMapItem()) {
                        this.addMapItem(new StringLiteral(mi.getKey().getContent().get(0).toString()),
                                SeParameterFactory.createStringParameter(mi.getValue()));
                }
        }

        @Override
        public String getValue(DataSource sds, long fid) {
                try {
                        return getParameter(sds, fid).getValue(sds, fid);
                } catch (ParameterException ex) {
                        Services.getOutputManager().println("Fallback:" + ex, Color.yellow);
                        return this.getFallbackValue().getValue(sds, fid);
                }
        }

        @Override
        public final int addMapItem(Literal key, StringParameter value) {
                value.setRestrictionTo(restriction);
                return super.addMapItem(key, value);
        }

        @Override
        public void setRestrictionTo(String[] list) {
            HashMap ola;
                restriction = list.clone();
                for (int i = 0; i < this.getNumMapItem(); i++) {
                        getMapItemValue(i).setRestrictionTo(list);
                }
        }
}
