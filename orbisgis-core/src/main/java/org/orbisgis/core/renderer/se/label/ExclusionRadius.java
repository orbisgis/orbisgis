/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * An {@code ExclusionZone} where the forbidden area is defined as a circle. It is 
 * defined thanks to radius value. Its meaning is of course dependant of the inner
 * UOM instance. The resulting exclusion zone is a circle centered on the point associated
 * to the {@code LabelPoint}, 
 * @author Maxence Laurent
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

                if (getUom() != null) {
                        r.setUom(getUom().toString());
                }
                if (radius != null) {
                        r.setRadius(radius.getJAXBParameterValueType());
                }

                ObjectFactory of = new ObjectFactory();
                return of.createExclusionRadius(r);
        }

        @Override
        public HashSet<String> dependsOnFeature() {
                if (radius != null) {
                        return radius.dependsOnFeature();
                }
                return new HashSet<String>();
        }

        @Override
        public UsedAnalysis getUsedAnalysis(){
                UsedAnalysis ua = new UsedAnalysis();
                ua.include(radius);
                return ua;
        }
}
