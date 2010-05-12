/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ExclusionRectangleType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class ExclusionRectangle extends ExclusionZone {

    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
    }

    @Override
    public JAXBElement<ExclusionRectangleType> getJAXBInstance() {
        ExclusionRectangleType r = new ExclusionRectangleType();

        if (uom != null) {
            r.setUnitOfMeasure(uom.toString());
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
    private RealParameter x;
    private RealParameter y;
}
