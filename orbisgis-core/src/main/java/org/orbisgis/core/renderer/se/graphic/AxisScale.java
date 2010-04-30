package org.orbisgis.core.renderer.se.graphic;

public class AxisScale {


    public double getValue(){
        return value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public double getData(){
        return data;
    }

    public void setData(double data){
        this.data = data;
    }


    private double data;
    private double value;
}
