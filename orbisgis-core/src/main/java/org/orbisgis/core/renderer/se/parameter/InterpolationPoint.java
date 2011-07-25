package org.orbisgis.core.renderer.se.parameter;

public class InterpolationPoint<Type> implements Comparable {

    public Type getValue() {
        return v;
    }

    public void setValue(Type value) {
        this.v = value;
    }

    public double getData() {
        return d;
    }

    public void setData(double data) {
        this.d = data;
    }

    @Override
    public int compareTo(Object o) {
        InterpolationPoint<Type> ip = (InterpolationPoint<Type>) o;

        double l = ip.d;
        double r = this.d;

        if (l < r) {
            return 1;
        } else if (l > r) {
            return -1;
        } else {
            return 0;
        }
    }
    private double d;
    private Type v = null;
}
