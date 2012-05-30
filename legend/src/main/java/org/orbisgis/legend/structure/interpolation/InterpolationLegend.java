/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.interpolation;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.NumericLegend;

/**
 * The default representation of an interpolation, in the legend. If obtained
 * during an analysis, it is supposed to mean that non of the other, more
 * accurate cases, has been recognized in the input {@code Interpolate2Real}
 * instanceused by the analyzer.
 * @author alexis
 */
public class InterpolationLegend implements NumericLegend {

        private Interpolate2Real interp;

        /**
         * Build a new {@code InterpolationLegend}
         * @param inter
         */
        public InterpolationLegend(Interpolate2Real inter){
                interp = inter;
        }

        /**
         * Get the {@code Interpolate2Real} instance associated to this
         * {@code InterpolationLegend}.
         * @return
         */
        public Interpolate2Real getInterpolation(){
                return interp;
        }

        /**
         * Gets the data associated to the first interpolation point.
         * @return
         */
        public double getFirstData() {
            return interp.getInterpolationPoint(0).getData();
        }
        
        /**
         * Sets the data associated to the first interpolation point.
         * @return
         */
        public void setFirstData(double d) {
            interp.getInterpolationPoint(0).setData(d);
        }

        /**
         * Gets the data associated to the second interpolation point.
         * @param d
         */
        public double getSecondData() {
            return interp.getInterpolationPoint(1).getData();
        }

        /**
         * Sets the data associated to the second interpolation point.
         * @param d
         */
        public void setSecondData(double d) {
            interp.getInterpolationPoint(1).setData(d);
        }

        /**
         * Gets the value associated to the first interpolation point, as a double.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @return
         * @throws ParameterException
         * If the inner interpolation contain a value that is not a literal.
         */
        public double getFirstValue() throws ParameterException {
            return interp.getInterpolationPoint(0).getValue().getValue(null, 0);
        }

        /**
         * Sets the value associated to the first interpolation point.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @param d
         * @throws ClassCastException
         * If the inner interpolation contain a value that is not a literal.
         */
        public void setFirstValue(double d){
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(0).getValue();
            rl.setValue(d);
        }

        /**
         * Gets the value associated to the second interpolation point, as a double.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @return
         * @throws ParameterException
         * If the inner interpolation contain a value that is not a literal.
         */
        public double getSecondValue() throws ParameterException {
            return interp.getInterpolationPoint(1).getValue().getValue(null, 0);
        }

        /**
         * Sets the value associated to the first interpolation point.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @param d
         * @throws ClassCastException
         * If the inner interpolation contain a value that is not a literal.
         */
        public void setSecondValue(double d){
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(1).getValue();
            rl.setValue(d);
        }

}
