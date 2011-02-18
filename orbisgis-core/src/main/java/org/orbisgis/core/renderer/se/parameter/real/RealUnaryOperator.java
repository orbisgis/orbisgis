package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.UnaryOperatorType;
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

    public RealUnaryOperator(JAXBElement<UnaryOperatorType> expr) throws InvalidStyle {
		this();
        UnaryOperatorType t = expr.getValue();

        this.setOperand(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>)t.getExpression()));

        String operator = expr.getName().getLocalPart();

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

        UnaryOperatorType o = new UnaryOperatorType();

        o.setExpression(this.getOperand().getJAXBExpressionType());

        ObjectFactory of = new ObjectFactory();

        switch (op) {
            case SQRT:
                return of.createSqrt(o);
            case LN:
                return of.createLn(o);
            case LOG:
                return of.createLog(o);
        }
        return null;
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
