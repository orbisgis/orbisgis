package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;


public class Categorize2String extends Categorize<StringParameter, StringLiteral> implements StringParameter {

    public Categorize2String(StringParameter initialClass, StringLiteral fallback, RealParameter lookupValue){
        super(initialClass, fallback, lookupValue);
    }

    @Override
    public String getValue(DataSource ds, long fid){
        try{
            return getParameter(ds, fid).getValue(ds, fid);
        }
        catch(ParameterException ex){
            return this.fallbackValue.getValue(null, fid);
        }
    }

}
