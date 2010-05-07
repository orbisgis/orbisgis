package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public class RealBinaryOperator implements RealParameter{

    public RealBinaryOperator(){
        l = new RealLiteral(0.0);
        r = new RealLiteral(0.0);
        op = RealBinaryOperatorType.ADD;
    }

    public RealBinaryOperator(RealParameter l, RealParameter r, RealBinaryOperatorType op){
        this.l = l;
        this.r = r;
        this.op = op;
    }


    public RealParameter getLeftValue(){
        return l;
    }

    public RealParameter getRightValue(){
        return r;
    }


    public void setLeftValue(RealParameter value){
        l = value;
    }
    public void setRightValue(RealParameter value){
        r = value;
    }


    public void setOperator(RealBinaryOperatorType operator){
        op = operator;
    }

    public RealBinaryOperatorType getOperator(){
        return op;
    }


    @Override
    public boolean dependsOnFeature(){
        return (l.dependsOnFeature()) || (r.dependsOnFeature());
    }

    /**
     *
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     */
    @Override
    public double getValue(DataSource ds, long fid) throws ParameterException {
        double lVal = l.getValue(ds, fid);
        double rVal = r.getValue(ds, fid);

        switch (op){
            case SUB:
                return lVal - rVal;
            case DIV:
                if (rVal != 0.0){
                    return lVal / rVal;
                }
                else{
                    throw new ParameterException("Division by zero");
                }
            case MUL:
                return lVal * rVal;
            case ADD: default: 
                return lVal + rVal;

        }
    }


    private RealBinaryOperatorType op;

    private RealParameter l;
    private RealParameter r;


}
