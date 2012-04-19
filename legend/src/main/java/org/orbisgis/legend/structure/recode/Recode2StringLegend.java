/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.recode;

import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.legend.LegendStructure;

/**
 * Specialization of {@code LegendStructure} that is used to represent value
 * classifications that are used as {@code StringParameter} instances.
 * @author alexis
 */
public class Recode2StringLegend implements LegendStructure{

        private Recode2String recode;

        /**
         * Build a new instance of {@code Recode2StringLegend}, using the {@code
         * Recode} given in argument.
         * @param recode
         */
        public Recode2StringLegend(Recode2String recode) {
                this.recode = recode;
        }

        /**
         * Get the {@code Recode} instance associated to this {@code LegendStructure}.
         * @return
         */
        public Recode2String getRecode() {
                return recode;
        }

}
