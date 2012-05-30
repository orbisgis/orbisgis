/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;

/**
 * {@code PenStroke} is defined using a {@code Width} attribute, which is a
 * RealAttribute. Consequently, it can be defined as linearly interpolated upon
 * some numeric attribute. This way, we obtain a "proportional line" analysis.
 * @author alexis
 */
public class ProportionalStrokeLegend extends PenStrokeLegend {


        /**
         * Build a new {@code ProportionalStrokeLegend}, using the given {@code
         * PenStroke}.
         * @param penStroke
         */
        public ProportionalStrokeLegend(PenStroke penStroke, LinearInterpolationLegend width,
                    LegendStructure fill, LegendStructure dashes) {
                super(penStroke, width, fill, dashes);
        }

        /**
         * Get the data of the second interpolation point
         * @return
         */
        public double getFirstData() {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getFirstData();
        }

        /**
         * Get the data of the first interpolation point
         * @return
         */
        public double getSecondData() {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getSecondData();
        }

        /**
         * Set the data of the second interpolation point
         * @param d
         */
        public void setFirstData(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setFirstData(d);
        }

        /**
         * Set the data of the first interpolation point
         * @param d
         */
        public void setSecondData(double d){
            ((LinearInterpolationLegend)getWidthAnalysis()).setSecondData(d);
        }

        /**
         * Get the value of the first interpolation point, as a {@code double}. The
         * interpolation value is supposed to be a {@code RealLiteral} instance. If
         * it is not, an exception should have been thrown at initialization.
         * @return
         */
        public double getFirstValue() throws ParameterException {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getFirstValue();
        }

        /**
         * Set the value of the first interpolation point, as a {@code double}.
         * @param d
         */
        public void setFirstValue(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setFirstValue(d);
        }
        
        /**
         * Get the value of the second interpolation point, as a {@code double}. The
         * interpolation value is supposed to be a {@code RealLiteral} instance. If
         * it is not, an exception should have been thrown at initialization.
         * @return
         */
        public double getSecondValue() throws ParameterException {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getSecondValue();
        }

        /**
         * Set the value of the second interpolation point, as a {@code double}.
         * @param d 
         */
        public void setSecondValue(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setSecondValue(d);
        }
        
}
