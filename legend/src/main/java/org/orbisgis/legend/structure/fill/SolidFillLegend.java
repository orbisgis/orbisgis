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
package org.orbisgis.legend.structure.fill;

import org.orbisgis.coremap.renderer.se.fill.SolidFill;
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
