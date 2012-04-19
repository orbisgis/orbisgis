/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.categorize;

import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.legend.LegendStructure;

/**
 *
 * @author alexis
 */
public class Categorize2StringLegend implements LegendStructure {

        private Categorize2String categorize;

        /**
         * Build a new {@code Categorize2StringLegend}, using the SE {@code
         * Categorize2String} given in argument.
         * @param categorize
         */
        public Categorize2StringLegend(Categorize2String categorize) {
                this.categorize = categorize;
        }

        /**
         * Get the {@code Categorize} instance that is associated to this {@code
         * LegendStructure}.
         * @return
         */
        public Categorize2String getCategorize() {
                return categorize;
        }

}
