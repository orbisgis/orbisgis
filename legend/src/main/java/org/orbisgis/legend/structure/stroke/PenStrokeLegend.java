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
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;

/**
 * A {@code LegendStructure} that gathers all the analysis that can be made inside a
 * {@code PenStroke}. When a more accurate analysis can be recognized, it's
 * really better to instanciate the corresponding specialization of this class.
 * @author alexis
 */
public class PenStrokeLegend implements LegendStructure {

        private PenStroke penStroke;

        //They can be three analysis in a PenStroke :
        private LegendStructure widthAnalysis;
        private LegendStructure fillAnalysis;
        private LegendStructure dashAnalysis;

        /**
         * Build a new, default, {@code PenStrokeLegend} instance.
         * @param ps
         */
        public PenStrokeLegend(PenStroke ps, LegendStructure width, LegendStructure fill, LegendStructure dash){
                penStroke = ps;
                widthAnalysis = width;
                fillAnalysis = fill;
                dashAnalysis = dash;
        }


        /**
         * Retrieve the PenStroke contained in this {@code LegendStructure}.
         * @return
         */
        public PenStroke getPenStroke() {
                return penStroke;
        }

        /**
         * Get the analysis made on the dash array. Rememeber that this array
         * can be {@code null}.
         * @return
         */
        public LegendStructure getDashAnalysis() {
                return dashAnalysis;
        }

        /**
         * Replace the analysis that has been made on the dash array.
         * @param ls
         */
        protected void setDashAnalysis(LegendStructure ls) {
            dashAnalysis = ls;
        }

        /**
         * Get the analysis made on the {@code Fill} used to draw the associated
         * {@code PenStroke}.
         * @return
         */
        public LegendStructure getFillAnalysis() {
                return fillAnalysis;
        }

        /**
         * Get the analysis made on the {@code Width} associated to this {@code
         * PenStroke}.
         * @return
         */
        public LegendStructure getWidthAnalysis() {
                return widthAnalysis;
        }
}
