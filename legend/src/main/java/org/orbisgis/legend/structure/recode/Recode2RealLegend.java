/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.recode;

import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.legend.LegendStructure;

/**
 * {@code LegendStructure} specialization associated to {@code Recode2Real} instances.
 * @author alexis
 */
public class Recode2RealLegend implements LegendStructure {

        private Recode2Real recode;

        /**
         * Build a new {@code Recode2Real} instance, using the given {@code
         * Recode2Real}.
         * @param recode
         */
        public Recode2RealLegend(Recode2Real recode) {
                this.recode = recode;
        }

        /**
         * Get the {@code Recode2Real} associated to this {@code LegendStructure}
         * @return
         */
        public Recode2Real getRecode() {
                return recode;
        }

}
