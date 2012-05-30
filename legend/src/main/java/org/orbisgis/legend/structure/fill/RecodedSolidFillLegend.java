/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.fill;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.structure.recode.Recode2ColorLegend;

/**
 * A {@code Legend} that represents a {@code SolidFill} where the color is defined
 * accorgind to a {@code Recode} operation.
 * @author alexis
 */
public class RecodedSolidFillLegend extends SolidFillLegend {

        /**
         * Build a new {@code CategorizedSolidFillLegend} using the {@code 
         * SolidFill} and {@code Recode2ColorLegend} given in parameter.
         * @param fill
         * @param colorLegend
         */
        public RecodedSolidFillLegend(SolidFill fill, Recode2ColorLegend colorLegend) {
                super(fill, colorLegend);
        }

}
