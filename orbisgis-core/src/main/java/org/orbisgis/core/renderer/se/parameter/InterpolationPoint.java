package org.orbisgis.core.renderer.se.parameter;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterpolationPoint<Type> implements Comparable {

    public Type getValue() {
        return v;
    }

    public void setValue(Type value) {
        this.v = value;
    }

    public RealParameter getData() {
        return d;
    }

    public void setData(RealParameter data) {
        this.d = data;
    }

    @Override
    public int compareTo(Object o) {

        InterpolationPoint<Type> ip = (InterpolationPoint<Type>) o;

        // In the case points depends on features...
        if (ip.d.dependsOnFeature() || this.d.dependsOnFeature()) {
            return 0;
        } else {
            try {
                double l = ip.d.getValue(null, 0);
                double r = this.d.getValue(null, 0);

                if (l < r) {
                    return 1;
                } else if (l > r) {
                    return -1;
                } else {
                    return 0;
                }
            } catch (ParameterException ex) {
                Logger.getLogger(Category.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }
    }
    private RealParameter d = null;
    private Type v = null;
}
