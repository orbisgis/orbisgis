package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public class Categorize2Real extends Categorize<RealParameter, RealLiteral> implements RealParameter {

    public Categorize2Real(RealParameter initialClass, RealLiteral fallback, RealParameter lookupValue){
        super(initialClass, fallback, lookupValue);
    }

    @Override
    public double getValue(DataSource ds, long fid){
        try{
            return getParameter(ds, fid).getValue(ds, fid);
        }
        catch(ParameterException ex){
            return this.fallbackValue.getValue(null, 0);
        }
    }
}
