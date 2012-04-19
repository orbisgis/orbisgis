/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.categorize;

import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.legend.LegendStructure;

/**
 * {@code LegendStructure} that describes a classification made by intervals on a real
 * parameter.
 * @author alexis
 */
public class Categorize2RealLegend implements LegendStructure {

        private Categorize2Real categorize;

        /**
         * Build a new {@code Categorize2Legend} instance, using the given
         * {@code Categorize2Real}.
         * @param c2r
         */
        public Categorize2RealLegend(Categorize2Real c2r){
                categorize = c2r;
        }

        /**
         * Get the {@code Categorize} instance associated to this {@code LegendStructure}
         * specialization.
         * @return
         */
        public Categorize2Real getCategorize() {
                return categorize;
        }
        
}
