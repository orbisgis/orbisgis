package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;

public class StringLiteral implements StringParameter{

    public StringLiteral(){
        v = "";
    }

    public StringLiteral(String value){
        v = value;
    }

    @Override
    public String getValue(DataSource ds, int fid){
        return v;
    }

    public void setValue(String value){
        v = value;
    }

    @Override
    public boolean dependsOnFeature(){
        return false;
    }

    private String v;
}
