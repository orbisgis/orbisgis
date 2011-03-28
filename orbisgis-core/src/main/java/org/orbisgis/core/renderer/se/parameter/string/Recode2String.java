package org.orbisgis.core.renderer.se.parameter.string;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.RecodeType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.omg.PortableServer.Servant;
import org.orbisgis.core.Services;
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
    public String getValue(SpatialDataSourceDecorator sds, long fid) {
        try {
            return getParameter(sds, fid).getValue(sds, fid);
        } catch (ParameterException ex) {
            Services.getOutputManager().println("Fallback:" + ex, Color.yellow);
            return this.fallbackValue.getValue( sds, fid);
        }
    }
}
