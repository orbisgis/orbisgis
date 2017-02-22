/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se.label;

import net.opengis.se._2_0.core.ExclusionRadiusType;
import net.opengis.se._2_0.core.ExclusionRectangleType;
import net.opengis.se._2_0.core.ExclusionZoneType;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.UomNode;
import org.orbisgis.coremap.renderer.se.common.Uom;

import javax.xml.bind.JAXBElement;

/**
 * An {@code ExclusionZone} defines an area around a {@code PointLabel} where other 
 * labels' displaying will be forbidden.
 * @author Alexis Guéganno, Maxence Laurent
 */
public abstract class ExclusionZone extends AbstractSymbolizerNode implements UomNode {
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
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
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
                if (uom != null) {
                        return uom;
                } else if(getParent() instanceof UomNode){
                        return ((UomNode)getParent()).getUom();
                } else {
                        return Uom.PX;
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
}
