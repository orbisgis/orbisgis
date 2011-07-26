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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.d) ^ (Double.doubleToLongBits(this.d) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof InterpolationPoint){
            InterpolationPoint ip = (InterpolationPoint) o;
            return this.d == ip.d;
        }
        return false;
    }

    private double d;
    private Type v = null;
}
