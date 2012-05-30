/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.recode;

import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.legend.LegendStructure;

/**
 * LegendStructure associated to a {@code ColorParameter} set using a {@code
 * Recode2Color} instance.
 * @author alexis
 */
public class Recode2ColorLegend implements LegendStructure {

        private Recode2Color rc;

        /**
         * Build this {@code Recode2ColorLegend}, using the {@code Recode2Color}
         * instance given in argument.
         * @param rc
         */
        public Recode2ColorLegend(Recode2Color rc) {
                this.rc = rc;
        }

        /**
         * Get the {@code Recode2Color} instance associated to this {@code
         * Recode2ColorLegend}.
         * @return
         */
        public Recode2Color getRecode() {
                return rc;
        }

}
