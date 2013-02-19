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
package org.orbisgis.legend.structure.fill;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * Generic {@code LegendStructure} representation of a SolidFill. The analysis can mainly
 * be made on the color, for a SolidFill.</p>
 * <p>{@code SolidFill} should be monovariate elements : we should only
 * play with the color, not with the opacity.
 * @author Alexis Guéganno
 */
public class SolidFillLegend implements FillLegend {

        private SolidFill fill;
        private LegendStructure colorLegend;
        private LegendStructure opacityLegend;

        /**
         * Build a {@code SolidFillLegend} using the {@code Fill} and {@code
         * LegendStructure} instances given in parameter.
         * @param fill
         * @param colorLegend
         */
        public SolidFillLegend(SolidFill fill, LegendStructure colorLegend, LegendStructure rll) {
                this.fill = fill;
                this.colorLegend = colorLegend;
                this.opacityLegend = rll;
        }

        /**
         * Retrieve the {@code LegendStructure} that is associated to the color of the
         * inner {@code Fill}
         * @return
         */
        public LegendStructure getFillColorLegend() {
                return colorLegend;
        }

        /**
         * Gets the opacity associated to the inner {@link SolidFill}. As we don't recognize analysis madeon the opacity
         * field of the SolidFill instances, we know that we won't be able to build SolidfillLegend with an opacity that can't be
         * recognized as a {@link RealLiteralLegend}.
         * @return
         */
        public LegendStructure getFillOpacityLegend(){
            return opacityLegend;
        }

        /**
         * Get the {@code Fill} that backs up this {@code LegendStructure}.
         * @return
         */
        @Override
        public SolidFill getFill() {
                return fill;
        }

}
