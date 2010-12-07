package org.orbisgis.core.renderer.se.parameter.real;

import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.BinaryOperatorType;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
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

	public RealBinaryOperator(JAXBElement<BinaryOperatorType> expr) throws InvalidStyle {
		ctx = RealParameterContext.realContext;

		BinaryOperatorType t = expr.getValue();

		this.setLeftValue(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>) t.getExpression().get(0)));
		this.setRightValue(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>) t.getExpression().get(1)));

		String operator = expr.getName().getLocalPart();

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
	public double getValue(Feature feat) throws ParameterException {
		double lVal = l.getValue(feat);
		double rVal = r.getValue(feat);

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

		switch (op) {
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
}
