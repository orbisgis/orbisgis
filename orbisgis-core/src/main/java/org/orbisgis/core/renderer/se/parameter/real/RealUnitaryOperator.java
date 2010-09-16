package org.orbisgis.core.renderer.se.parameter.real;

import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.UnitaryOperatorType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;

public final class RealUnitaryOperator implements RealParameter {

	private Double min;
	private Double max;

    private RealParameter v;
    private RealUnitaryOperatorType op;

    public RealUnitaryOperator(JAXBElement<UnitaryOperatorType> expr) {
        UnitaryOperatorType t = expr.getValue();

        this.setOperand(SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>)t.getExpression()));

        String operator = expr.getName().getLocalPart();

        if (operator.equals("Log10")){
            this.op = RealUnitaryOperatorType.LOG_10;
        }
        else if (operator.equals("Sqrt")){
            this.op = RealUnitaryOperatorType.SQRT;
        }
    }

	@Override
	public JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel){
		throw new UnsupportedOperationException("Not yet implemented ("+ this.getClass() + " )");
	}

	@Override
	public void setMinValue(Double min) {
		this.min = min;
	}

	@Override
	public void setMaxValue(Double max) {
		this.max = max;
	}

    public enum RealUnitaryOperatorType {
        SQRT, LOG_10;
    }

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
    public double getValue(Feature feat) throws ParameterException {
        double value = v.getValue(feat);

        switch (op) {
            case SQRT:
                return Math.sqrt(value);
            case LOG_10:
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

        UnitaryOperatorType o = new UnitaryOperatorType();

        o.setExpression(this.getOperand().getJAXBExpressionType());

        ObjectFactory of = new ObjectFactory();

        switch (op) {
            case SQRT:
                return of.createSqrt(o);
            case LOG_10:
                return of.createLog10(o);
        }
        return null;
    }
}
