/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.fill;

import java.awt.Color;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;

/**
 * A {@code Legend} that represents a {@code SolidFill} where the color is a
 * {@code Literal} instance.
 * @author alexis
 */
public class ConstantSolidFillLegend extends SolidFillLegend implements ConstantFillLegend {

        /**
         * Build a new {@code ConstantSolidFillLegend} using the {@code SolidFill}
         * and {@code ColorLiteralLegend} given in parameter.
         * @param fill
         * @param colorLegend
         */
        public ConstantSolidFillLegend(SolidFill fill, ColorLiteralLegend colorLegend) {
                super(fill, colorLegend);
        }

        /**
         * Get the {@code Color} used to paint the inner {@code SolidFill}.
         * @return
         */
        public Color getColor(){
            ColorLiteralLegend cll = (ColorLiteralLegend) getColorLegend();
            return cll.getColor();
        }

        /**
         * Set the {@code Color} used to paint the inner {@code SolidFill}.
         * @param col
         */
    public void setColor(Color col) {
            ColorLiteralLegend cll = (ColorLiteralLegend) getColorLegend();
            cll.setColor(col);
    }

}
