package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public class RealUnitaryOperator implements RealParameter {

    public RealUnitaryOperator() {
    }

    public RealUnitaryOperator(RealParameter value, RealUnitaryOperatorType op) {
        v = value;
        this.op = op;
    }

    public RealParameter getOperand() {
        return v;
    }

    public void setOperand(RealParameter value) {
        v = value;
    }

    public void setOperator(RealUnitaryOperatorType operator) {
        op = operator;
    }

    public RealUnitaryOperatorType getOperator() {
        return op;
    }

    @Override
    public boolean dependsOnFeature() {
        return v.dependsOnFeature();
    }

    @Override
    public double getValue(DataSource ds, long fid) throws ParameterException {
        double value = v.getValue(ds, fid);

        switch (op) {
            case SQRT:
                return Math.sqrt(value);
            case LOG:
                return Math.log10(value); // TODO quelle base ?
            case SIN: // have to convert a clockwise angle [°] in counterclockwise radian
                value *= -(2 * Math.PI / 360.0);
                return Math.sin(value);
            case COS: // have to convert a clockwise angle [°] in counterclockwise radian
                value *= -(2 * Math.PI / 360.0);
                return Math.cos(value);
            default:
                return value;
        }
    }
    private RealParameter v;
    private RealUnitaryOperatorType op;
}
