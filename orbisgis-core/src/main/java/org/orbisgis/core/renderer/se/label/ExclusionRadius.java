/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ExclusionRadiusType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class ExclusionRadius extends ExclusionZone {

    ExclusionRadius(JAXBElement<ExclusionRadiusType> ert) {
        ExclusionRadiusType e = ert.getValue();

        if (e.getRadius() != null){
            this.radius = SeParameterFactory.createRealParameter(e.getRadius());
        }

        if (e.getUnitOfMeasure() != null){
            this.uom = Uom.fromOgcURN(e.getUnitOfMeasure());
        }
    }

    public RealParameter getRadius() {
        return radius;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
    }

    @Override
    public JAXBElement<ExclusionRadiusType> getJAXBElement() {
        ExclusionRadiusType r = new ExclusionRadiusType();

        if (uom != null) {
            r.setUnitOfMeasure(uom.toString());
        }
        if (radius != null) {
            r.setRadius(radius.getJAXBParameterValueType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createExclusionRadius(r);
    }
    private RealParameter radius;
}
