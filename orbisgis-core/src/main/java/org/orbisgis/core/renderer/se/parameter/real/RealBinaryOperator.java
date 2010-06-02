package org.orbisgis.core.renderer.se.parameter.real;

import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.ogc.BinaryOperatorType;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public class RealBinaryOperator implements RealParameter {

    public enum RealBinaryOperatorType {
        ADD, SUB, MUL, DIV
    }

    public RealBinaryOperator() {
        l = new RealLiteral(0.0);
        r = new RealLiteral(0.0);
        op = RealBinaryOperatorType.ADD;
    }

    public RealBinaryOperator(RealParameter l, RealParameter r, RealBinaryOperatorType op) {
        this.l = l;
        this.r = r;
        this.op = op;
    }

    public RealBinaryOperator(JAXBElement<BinaryOperatorType> expr){

        BinaryOperatorType t = expr.getValue();

        this.setLeftValue(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>)t.getExpression().get(0)));
        this.setRightValue(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>)t.getExpression().get(1)));

        String operator = expr.getName().getLocalPart();
        
        if (operator.equals("Add")){
            this.op = RealBinaryOperatorType.ADD;
        }
        else if (operator.equals("Mul")){
            this.op = RealBinaryOperatorType.MUL;
        }
        else if (operator.equals("Sub")){
            this.op = RealBinaryOperatorType.SUB;
        }
        else if (operator.equals("Div")){
            this.op = RealBinaryOperatorType.DIV;
        }
    }

    public RealParameter getLeftValue() {
        return l;
    }

    public RealParameter getRightValue() {
        return r;
    }

    public void setLeftValue(RealParameter value) {
        l = value;
    }

    public void setRightValue(RealParameter value) {
        r = value;
    }

    public void setOperator(RealBinaryOperatorType operator) {
        op = operator;
    }

    public RealBinaryOperatorType getOperator() {
        return op;
    }

    @Override
    public boolean dependsOnFeature() {
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

        switch (op) {
            case SUB:
                return lVal - rVal;
            case DIV:
                if (rVal != 0.0) {
                    return lVal / rVal;
                } else {
                    throw new ParameterException("Division by zero");
                }
            case MUL:
                return lVal * rVal;
            case ADD:
            default:
                return lVal + rVal;

        }
    }

    @Override
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
        BinaryOperatorType o = new BinaryOperatorType();
        
        List<JAXBElement<?>> ex = o.getExpression();
        ex.add(l.getJAXBExpressionType());
        ex.add(r.getJAXBExpressionType());

        org.orbisgis.core.renderer.persistance.ogc.ObjectFactory of =
                new org.orbisgis.core.renderer.persistance.ogc.ObjectFactory();

        switch (op){
            case ADD:
                return of.createAdd(o);
            case SUB:
                return of.createSub(o);
            case DIV:
                return of.createDiv(o);
            case MUL:
                return of.createMul(o);
        }

        return null;
    }


    @Override
    public ParameterValueType getJAXBParameterValueType() {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }


    private RealBinaryOperatorType op;
    private RealParameter l;
    private RealParameter r;
}
