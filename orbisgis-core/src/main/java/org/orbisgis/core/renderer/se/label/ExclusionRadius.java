/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;

import net.opengis.se._2_0.core.ExclusionRadiusType;
import net.opengis.se._2_0.core.ObjectFactory;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * An {@code ExclusionZone} where the forbidden area is defined as a circle. It is 
 * defined thanks to radius value. Its meaning is of course dependant of the inner
 * UOM instance. The resulting exclusion zone is a circle centered on the point associated
 * to the {@code LabelPoint}, 
 * @author maxence
 */
public final class ExclusionRadius extends ExclusionZone {

        private RealParameter radius;

        /**
         * Build a new {@code ExclusionRadius} with radius 3.
         */
        public ExclusionRadius() {
                setRadius(new RealLiteral(3));
        }

        /**
         * Build a new {@code ExclusionRadius} with radius value set to {@code radius}.
         */
        public ExclusionRadius(double radius) {
                setRadius(new RealLiteral(radius));
        }

        /**
         * Build a new {@code ExclusionRadius} from the given JAXBElement.
         */
        ExclusionRadius(JAXBElement<ExclusionRadiusType> ert) throws InvalidStyle {
                ExclusionRadiusType e = ert.getValue();

                if (e.getRadius() != null) {
                        setRadius(SeParameterFactory.createRealParameter(e.getRadius()));
                }

                if (e.getUom() != null) {
                        setUom(Uom.fromOgcURN(e.getUom()));
                }
        }

        /**
         * Get the radius defining this {@code ExclusionRadius}
         * @return 
         * The radius as a {@code RealParameter}.
         */
        public RealParameter getRadius() {
                return radius;
        }

        /**
         * Set the radius defining this {@code ExclusionRadius}
         * @param radius 
         */
        public void setRadius(RealParameter radius) {
                this.radius = radius;
                if (this.radius != null) {
                        this.radius.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                }
        }

        @Override
        public JAXBElement<ExclusionRadiusType> getJAXBElement() {
                ExclusionRadiusType r = new ExclusionRadiusType();

                if (uom != null) {
                        r.setUom(uom.toString());
                }
                if (radius != null) {
                        r.setRadius(radius.getJAXBParameterValueType());
                }

                ObjectFactory of = new ObjectFactory();
                return of.createExclusionRadius(r);
        }

        @Override
        public String dependsOnFeature() {
                if (radius != null) {
                        return radius.dependsOnFeature();
                }
                return "";
        }
}
