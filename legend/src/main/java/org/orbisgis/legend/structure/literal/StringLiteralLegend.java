/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.literal;

import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.LegendStructure;

/**
 * LegendStructure that can be associated to a simple string literal.
 * @author alexis
 */
public class StringLiteralLegend implements LegendStructure{

        private StringLiteral literal;

        /**
         * Build a new {@code StringLiteralLegend} instance, using the literal
         * given in parameter.
         * @param literal
         */
        public StringLiteralLegend(StringLiteral literal) {
                this.literal = literal;
        }

        /**
         * Get the {@code Literal} associated to this {@code LegendStructure}
         * instance.
         * @return
         */
        public StringLiteral getLiteral() {
                return literal;
        }

        /**
         * Set the {@code Literal} associated to this {@code LegendStructure}
         * instance.
         * @param sl
         */
        public void setLiteral(StringLiteral sl) {
            literal = sl;
        }

}
