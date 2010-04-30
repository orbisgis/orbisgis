package org.orbisgis.core.renderer.se.parameter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class Category<ToType> implements Comparable {

    public Category(ToType value, RealParameter threshold) {
        this.value = value;
        this.threshold = threshold;
    }

    public RealParameter getThreshold() {
        return threshold;
    }

    public void setThreshold(RealParameter threshold) {
        this.threshold = threshold;
    }

    public ToType getClassValue() {
        return value;
    }

    public void setClassValue(ToType value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        // if thresold depends on features, they cannot be sorted ! (so they're equals...)
        // TODO type checking !
        Category<ToType> c = (Category<ToType>) o;
        if (this.threshold.dependsOnFeature() || c.threshold.dependsOnFeature()) {
            return 0;
        } else {
            double t1;
            double t2;
            try {
                t1 = this.threshold.getValue(null, 0);
                t2 = c.threshold.getValue(null, 0);

                if (t1 < t2) {
                    return -1;
                }
                else if (t2 < t1) {
                    return 1;
                }
                else {
                    return 0;
                }
            } catch (ParameterException ex) {
                Logger.getLogger(Category.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }

        }
    }
    private RealParameter threshold;
    private ToType value;
}
