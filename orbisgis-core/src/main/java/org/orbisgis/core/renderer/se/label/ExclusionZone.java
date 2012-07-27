/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se.label;

import java.util.HashSet;
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
 * @author Alexis Guéganno, Maxence Laurent
 */
public abstract class ExclusionZone implements SymbolizerNode, UomNode {
        private SymbolizerNode parent;
        private Uom uom;

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

        @Override
        public abstract HashSet<String> dependsOnFeature();
}