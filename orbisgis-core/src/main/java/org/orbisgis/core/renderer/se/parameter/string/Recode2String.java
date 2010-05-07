package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.Recode;

public class Recode2String extends Recode<StringParameter, StringLiteral> implements StringParameter {

    public Recode2String(StringLiteral fallback, StringParameter lookupValue){
        super(fallback, lookupValue);
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
