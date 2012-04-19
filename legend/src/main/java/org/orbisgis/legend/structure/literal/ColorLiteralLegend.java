/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.literal;

import java.awt.Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.legend.LegendStructure;

/**
 * {@code LegendStructure} associated to a numeric constant, that is represented as a
 * {@code ColorLiteral} in the SE model.
 *
 * @author alexis
 */
public class ColorLiteralLegend implements LegendStructure {

        private ColorLiteral cl;

        /**
         * Build a new {@code RealLiteralLegend} that is associated to the
         * {@code ColorLiteral r}.
         * @param r
         */
        public ColorLiteralLegend(ColorLiteral literal){
                cl = literal;
        }

        /**
         * Get the {@code ColorLiteral} associated with this {@code
         * RealLiteralLegend}.
         * @return
         */
        public ColorLiteral getColoraLiteral(){
                return cl;
        }

        /**
         * Gets the {@code Color} contained in the inner {@code ColorLiteral}.
         * @return
         */
        public Color getColor() {
            return cl.getColor(null, 0);
        }

        /**
         * Sets the {@code Color} contained in the inner {@code ColorLiteral}.
         * @param col
         */
        public void setColor(Color col) {
            cl.setColor(col);
        }
}
