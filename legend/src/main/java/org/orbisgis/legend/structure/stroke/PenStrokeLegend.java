/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
