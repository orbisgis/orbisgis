/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.literal;

import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.NumericLegend;

/**
 * {@code Legend} associated to a numeric constant, that is represented as a
 * {@code RealLiteral} in the SE model.
 * @author alexis
 */
public class RealLiteralLegend implements NumericLegend {

        private  RealLiteral rl;

        /**
         * Build a new {@code RealLiteralLegend} that is associated to the
         * {@code RealLiteral r}.
         * @param r
         */
        public RealLiteralLegend(RealLiteral r){
                rl = r;
        }

        /**
         * Get the {@code RealLiteral} associated with this {@code
         * RealLiteralLegend}.
         * @return
         */
        public RealLiteral getRealLiteral() {
                return rl;
        }

        /**
         * As we're working on a RealLiteral, we can retrieve the double value
         * that is returned whatever the input data are.
         * @return
         */
        public double getDouble() {
            return rl.getValue(null, 0);
        }

        /**
         * As we're working on a RealLiteral, we can set the double value
         * that is returned whatever the input data are.
         * @param width
         */
        public void setDouble(double width) {
            rl.setValue(width);
        }

}
