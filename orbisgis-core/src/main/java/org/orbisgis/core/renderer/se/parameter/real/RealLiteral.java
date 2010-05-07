package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;

public class RealLiteral implements RealParameter{

    public RealLiteral(){
        v = 1.0;
    }

    public RealLiteral(double literal){
        v = literal;
    }

    @Override
    public double getValue(DataSource ds, long fid){
        return v;
    }

    public void setValue(double value){
        v = value;
    }


    @Override
    public boolean dependsOnFeature(){
        return false;
    }

    public static final RealLiteral ZERO = new RealLiteral(0.0);

    private double v;
}
