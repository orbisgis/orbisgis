/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;

/**
 * This class is associated to instances of {@code PenStroke} where an interval
 * classification is made on the dash array.
 * @author alexis
 */
public class CategorizedDashesPSLegend extends PenStrokeLegend {

        /**
         * Build a new instance of {@code LegendStructure} that matches a configuration
         * where a categorization is made only on the dash array, and the other
         * elements are constant or null.
         * @param ps
         * @param width
         * @param fill
         * @param dash
         */
        public CategorizedDashesPSLegend(PenStroke ps, LegendStructure width, LegendStructure fill, LegendStructure dash) {
                super(ps, width, fill, dash);
        }
        
}
