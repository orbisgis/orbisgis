package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.se.parameter.MapItem;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

public class Recode2Real extends Recode<RealParameter, RealLiteral> implements RealParameter {

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
    public double getValue(DataSource ds, long fid){
        // Should always depend on features !
        try{
            return getParameter(ds, (int)fid).getValue(ds, fid);
        }
        catch(ParameterException ex){
            // Since fallback value is a literal, the following is secure
            return this.fallbackValue.getValue(null, 0);
        }
    }

}

