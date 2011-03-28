/*
package org.orbisgis.core.renderer.se.parameter.real;

import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ExpressionType;
import net.opengis.fes._2.FunctionType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public final class RealBinaryOperator implements RealParameter {

	private RealBinaryOperatorType op;
	private RealParameter l;
	private RealParameter r;
	private RealParameterContext ctx;

	public enum RealBinaryOperatorType {

		ADD, SUB, MUL, DIV
	}

	public RealBinaryOperator() {
		ctx = RealParameterContext.realContext;
		setLeftValue(new RealLiteral(0.0));
		setRightValue(new RealLiteral(0.0));

		op = RealBinaryOperatorType.ADD;
	}

	public RealBinaryOperator(RealParameter l, RealParameter r, RealBinaryOperatorType op) {
		this.op = op;
		ctx = RealParameterContext.realContext;
		setLeftValue(l);
		setRightValue(r);
	}

	public RealBinaryOperator(JAXBElement<FunctionType> expr) throws InvalidStyle {
		ctx = RealParameterContext.realContext;

        FunctionType f = new FunctionType();

		this.setLeftValue(SeParameterFactory.createRealParameter((JAXBElement<? extends Object>) f.getExpression().get(0)));
		this.setRightValue(SeParameterFactory.createRealParameter((JAXBElement<? extends Object>) f.getExpression().get(1)));

		String operator = f.getName();

		if (operator.equals("Add")) {
			this.op = RealBinaryOperatorType.ADD;
		} else if (operator.equals("Mul")) {
			this.op = RealBinaryOperatorType.MUL;
		} else if (operator.equals("Sub")) {
			this.op = RealBinaryOperatorType.SUB;
		} else if (operator.equals("Div")) {
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
		if (l != null) {
			l.setContext(ctx);
		}
	}

	public void setRightValue(RealParameter value) {
		r = value;
		if (r != null) {
			r.setContext(ctx);
		}
	}

	public void setOperator(RealBinaryOperatorType operator) {
		op = operator;
	}

	public RealBinaryOperatorType getOperator() {
		return op;
	}

	@Override
	public String dependsOnFeature() {
        return (l.dependsOnFeature() + " " + r.dependsOnFeature()).trim();
	}

	**
	 *
	 * @param ds
	 * @param fid
	 * @return
	 * @throws ParameterException
	 *
	@Override
	public double getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
		double lVal = l.getValue(sds, fid);
		double rVal = r.getValue(sds, fid);

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
        FunctionType f = new FunctionType();

		List<JAXBElement<?>> ex = f.getExpression();
		ex.add(l.getJAXBExpressionType());
		ex.add(r.getJAXBExpressionType());

        ObjectFactory of = new ObjectFactory();

		switch (op) {
            case ADD:
                f.setName("add");
                break;
			case SUB:
                f.setName("sub");
                break;
			case DIV:
                f.setName("div");
                break;
			case MUL:
                f.setName("mul");
                break;
		}

        return of.createFunction(f);
	}

	@Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

	@Override
	public String toString() {
		String str = "(" + this.l.toString();
		if (this.op == RealBinaryOperatorType.ADD) {
			str += " + ";
		} else if (this.op == RealBinaryOperatorType.SUB) {
			str += " - ";
		} else if (this.op == RealBinaryOperatorType.MUL) {
			str += "*";
		} else if (this.op == RealBinaryOperatorType.DIV) {
			str += "/";
		}

		str += this.r.toString() + ")";

		return str;
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;

		if (this.r != null){
			r.setContext(ctx);
		}

		if (this.l != null){
			l.setContext(ctx);
		}
	}

	@Override
	public RealParameterContext getContext(){
		return ctx;
	}

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}

    */