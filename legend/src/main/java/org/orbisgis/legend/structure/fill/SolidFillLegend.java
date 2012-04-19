/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.fill;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.LegendStructure;

/**
 * Generic {@code LegendStructure} representation of a SolidFill. The analysis can mainly
 * be made on the color, for a SolidFill.</p>
 * <p>{@code SolidFill} should be monovariate elements : we should only
 * play with the color, not with the opacity.
 * @author alexis
 */
public class SolidFillLegend implements LegendStructure {

        private SolidFill fill;

        private LegendStructure colorLegend;

        /**
         * Build a {@code SolidFillLegend} using the {@code Fill} and {@code
         * LegendStructure} instances given in parameter.
         * @param fill
         * @param colorLegend
         */
        public SolidFillLegend(SolidFill fill, LegendStructure colorLegend) {
                this.fill = fill;
                this.colorLegend = colorLegend;
        }

        /**
         * Retrieve the {@code LegendStructure} that is associated to the color of the
         * inner {@code Fill}
         * @return
         */
        public LegendStructure getColorLegend() {
                return colorLegend;
        }

        /**
         * Get the {@code Fill} that backs up this {@code LegendStructure}.
         * @return
         */
        public SolidFill getFill() {
                return fill;
        }

}
