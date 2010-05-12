/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ExclusionRadiusType;
import org.orbisgis.core.renderer.persistance.se.ExclusionZoneType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class ExclusionRadius extends ExclusionZone {

    public RealParameter getRadius() {
        return radius;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
    }

    @Override
    public JAXBElement<ExclusionRadiusType> getJAXBInstance() {
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
