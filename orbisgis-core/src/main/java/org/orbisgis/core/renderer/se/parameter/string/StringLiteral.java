package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class StringLiteral extends Literal implements StringParameter{

    public StringLiteral(){
        v = "";
    }

    public StringLiteral(String value){
        v = value;
    }

    @Override
    public String getValue(DataSource ds, long fid){
        return v;
    }

    public void setValue(String value){
        v = value;
    }

    @Override
    public boolean dependsOnFeature(){
        return false;
    }

    @Override
    public String toString(){
        return v;
    }

    private String v;
}
