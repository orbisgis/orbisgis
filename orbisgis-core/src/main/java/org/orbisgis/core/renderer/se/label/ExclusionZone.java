/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;

import net.opengis.se._2_0.core.ExclusionRadiusType;
import net.opengis.se._2_0.core.ExclusionRectangleType;
import net.opengis.se._2_0.core.ExclusionZoneType;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 * An {@code ExclusionZone} defines an area around a {@code PointLabel} where other 
 * labels' displaying will be forbidden.
 * @author alexis, maxence
 */
public abstract class ExclusionZone implements SymbolizerNode, UomNode {
        protected SymbolizerNode parent;
        protected Uom uom;

        /**
         * Gets a JAXB representation of this {@code ExclusionZone}
         * @return 
         */
        public abstract JAXBElement<? extends ExclusionZoneType> getJAXBElement();

        /**
         * Build an {@code ExclusionZone} from a JAXBElement.
         * @param ezt
         * @return
         * Whether a {@code ExclusionRadius}, or a {@code ExclusionRectangle}, but as a {@code ExclusionZone}.
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public static ExclusionZone createFromJAXBElement(JAXBElement<? extends ExclusionZoneType> ezt) throws InvalidStyle {
                if (ezt.getDeclaredType() == ExclusionRadiusType.class) {
                        return new ExclusionRadius((JAXBElement<ExclusionRadiusType>) ezt);
                } else if (ezt.getDeclaredType() == ExclusionRectangleType.class) {
                        return new ExclusionRectangle((JAXBElement<ExclusionRectangleType>) ezt);
                } else {
                        return null;
                }
        }

        @Override
        public Uom getUom() {
                if (uom == null) {
                        return parent.getUom();
                } else {
                        return uom;
                }
        }

        @Override
        public Uom getOwnUom() {
                return uom;
        }

        @Override
        public void setUom(Uom uom) {
                this.uom = uom;
        }

        @Override
        public SymbolizerNode getParent() {
                return parent;
        }

        @Override
        public void setParent(SymbolizerNode node) {
                parent = node;
        }

    /**
     * Get a String representation of the list of features this {@code ExclusionZone}
     * depends on.
     * @return
     * The features this {@code ExclusionZone} depends on, in a {@code String}.
     */
        public abstract String dependsOnFeature();
}
