/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ExclusionRectangleType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
public final class ExclusionRectangle extends ExclusionZone {

    private RealParameter x;
    private RealParameter y;

    public ExclusionRectangle(){
        this.setX(new RealLiteral(3));
        this.setY(new RealLiteral(3));
    }

    ExclusionRectangle(JAXBElement<ExclusionRectangleType> ert) throws InvalidStyle {
        ExclusionRectangleType e = ert.getValue();

        if (e.getX() != null){
            setX(SeParameterFactory.createRealParameter(e.getX()));
        }

        if (e.getY() != null){
            setY(SeParameterFactory.createRealParameter(e.getY()));
        }

        if (e.getUom() != null){
            setUom(Uom.fromOgcURN(e.getUom()));
        }
    }

    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
		if (x != null){
			x.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
		}
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
		if (this.y != null){
			y.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
		}
    }

    @Override
    public JAXBElement<ExclusionRectangleType> getJAXBElement() {
        ExclusionRectangleType r = new ExclusionRectangleType();

        if (uom != null) {
            r.setUom(uom.toString());
        }

        if (x != null) {
            r.setX(x.getJAXBParameterValueType());
        }

        if (y != null) {
            r.setY(y.getJAXBParameterValueType());
        }

        ObjectFactory of = new ObjectFactory();

        return of.createExclusionRectangle(r);
    }

	@Override
	public String dependsOnFeature() {
        String result = "";

        if (x != null) {
                        result = x.dependsOnFeature();
                }
        if (y != null) {
                        result += " " + y.dependsOnFeature();
                }

        return result.trim();
	}

}
