package org.orbisgis.core.renderer.se.parameter.real;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import net.opengis.fes._2.ExpressionType;
import net.opengis.fes._2.FunctionType;
import net.opengis.fes._2.ObjectFactory;
import org.gdms.data.SpatialDataSourceDecorator;

import net.opengis.se._2_0.core.ParameterValueType;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public final class RealUnaryOperator implements RealParameter {

	private Double min;
	private Double max;

    private RealParameter v;
    private RealUnitaryOperatorType op;
	private RealParameterContext ctx;

    public enum RealUnitaryOperatorType {
        SQRT, LOG, LN;
    }

    public RealUnaryOperator() {
		ctx = RealParameterContext.realContext;
    }

    public RealUnaryOperator(RealParameter value, RealUnitaryOperatorType op) {
		this();
        this.op = op;
		setOperand(value);
    }

    public RealUnaryOperator(JAXBElement<FunctionType> expr) throws InvalidStyle {
		this();
        FunctionType t = expr.getValue();

        this.setOperand(SeParameterFactory.createRealParameter((JAXBElement<? extends Object>)t.getExpression()));

        String operator = t.getName();

        if (operator.equals("Log")){
            this.op = RealUnitaryOperatorType.LOG;
        }
        else if (operator.equals("Ln")){
            this.op = RealUnitaryOperatorType.LN;
        }
        else if (operator.equals("Sqrt")){
            this.op = RealUnitaryOperatorType.SQRT;
        }
    }

    public RealParameter getOperand() {
        return v;
    }

    public void setOperand(RealParameter value) {
        v = value;

		if (v != null){
			v.setContext(ctx);
		}
    }

    public void setOperator(RealUnitaryOperatorType operator) {
        op = operator;
    }

    public RealUnitaryOperatorType getOperator() {
        return op;
    }

    @Override
    public String dependsOnFeature() {
        return v.dependsOnFeature();
    }

    @Override
    public double getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
        double value = v.getValue(sds, fid);

        switch (op) {
            case SQRT:
                return Math.sqrt(value);
            case LN:
                return Math.log(value);
            case LOG:
                return Math.log10(value); // TODO quelle base ?
            default:
                return value;
        }
    }

    @Override
    public ParameterValueType getJAXBParameterValueType() {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }

    @Override
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {

        FunctionType f = new FunctionType();
        List<JAXBElement<?>> expr = f.getExpression();

        expr.add(this.getOperand().getJAXBExpressionType());
        //f.setExpression(this.getOperand().getJAXBExpressionType());

        switch (op) {
            case SQRT:
                f.setName("sqrt");
                break;
            case LN:
                f.setName("ln");
                break;
            case LOG:
                f.setName("log");
                break;
        }
        ObjectFactory of = new ObjectFactory();
        return of.createFunction(f);
    }

	@Override
	public String toString(){
		return this.op.toString() + "(" + this.v.toString() + ")";
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;
		if (v != null){
			v.setContext(ctx);
		}
	}

	@Override
	public RealParameterContext getContext() {
		return ctx;
	}

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
