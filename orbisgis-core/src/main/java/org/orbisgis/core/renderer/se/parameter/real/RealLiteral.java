package org.orbisgis.core.renderer.se.parameter.real;

import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditRealLiteralPanel;

public class RealLiteral extends Literal implements RealParameter{

    public static final RealLiteral ZERO = new RealLiteral(0.0);
    private double v;
	private Double min;
	private Double max;

    public RealLiteral(){
        v = 1.0;
    }

    public RealLiteral(double literal){
        v = literal;
    }

    public RealLiteral(String d){
        this.v = new Double(d);
    }

    public RealLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public double getValue(Feature feat){
        return v;
    }

    public void setValue(double value){
        v = value;
    }


    @Override
    public boolean dependsOnFeature(){
        return false;
    }


    @Override
    public String toString(){
        Double v2 = v;
        return v2.toString();
    }

	@Override
	public JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel){
		return new EditRealLiteralPanel(this);
	}

	@Override
	public void setMinValue(Double min) {
		this.min = min;
	}

	@Override
	public void setMaxValue(Double max) {
		this.max = max;
	}

	public Double getMinValue() {
		return min;
	}

	public Double getMaxValue() {
		return max;
	}
}
