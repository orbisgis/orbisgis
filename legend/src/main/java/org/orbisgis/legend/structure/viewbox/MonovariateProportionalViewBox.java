/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;

/**
 * The representation of a monovariate proportional symbol, that has been
 * defined using a ViewBox. Such an analysis can be recognized if there is only
 * one dimension (ie height OR width) defined in the underlying {@code ViewBox},
 * and it is associated to an {@code Interpolation} that can be recognized as a
 * {@code SqrtInterpolationLegend}.
 * 
 * @author alexis
 */
public class MonovariateProportionalViewBox extends DefaultViewBox {
        
        private boolean onH;

        /**
         * Build a new {@code MonovariateProportionalViewBox} using the given
         * {@code SqrtInterpolationLegend} and {@code boolean} instances. {@code
         * onHeight} is used to know if the interpolation is made on the height
         * ({@code onHeight} is true) or on the width ({@code onHeight} is
         * false) of the associated {@link ViewBox};
         * @param inter
         * @param view 
         * @param onHeight
         */
        public MonovariateProportionalViewBox(SqrtInterpolationLegend inter, boolean onHeight,
                        ViewBox view){
                super(inter, onHeight, view);
                onH = onHeight;
        }

        /**
         * Get the {@link SqrtInterpolationLegend} associated to this {@code
         * Legend}.
         * @return
         * A {@link SqrtInterpolationLegend} instance.
         */
        public SqrtInterpolationLegend getInterpolation() {
                SqrtInterpolationLegend sqi;
                if(onH){
                        sqi = (SqrtInterpolationLegend) getHeightLegend();
                } else {
                        sqi = (SqrtInterpolationLegend) getWidthLegend();
                }
                return sqi;
        }

        /**
         * This method lets us check if the interpolation is made on the width
         * or on the height of the {@link ViewBox}.
         * @return
         * {@code true} if the interpolation is made on the height of the
         * {@code ViewBox}, ie if {@code getViewBox().getHeight()} returns a
         * not-null instance of {@code Interpolate}.
         */
        public boolean isOnHeight() {
                return onH;
        }

        /**
         * Gets the data associated to the first interpolation point.
         * @return
         */
        public double getFirstData() {
            return getInterpolation().getFirstData();
        }

        /**
         * Sets the data associated to the first interpolation point.
         * @return
         */
        public void setFirstData(double d) {
            getInterpolation().setFirstData(d);
        }

        /**
         * Gets the data associated to the second interpolation point.
         * @return
         */
        public double getSecondData() {
            return getInterpolation().getSecondData();
        }

        /**
         * Sets the data associated to the second interpolation point.
         * @return
         */
        public void setSecondData(double d) {
            getInterpolation().setSecondData(d);
        }

        /**
         * Get the value of the first interpolation point as a double. We are not
         * supposed to work here with {@code RealParameter} other than {@code
         * RealLiteral}, so we retrieve directly the {@code double} it contains.
         * @return
         * @throws ParameterException
         * If a problem is encountered while retrieving the double value.
         */
        public double getFirstValue() throws ParameterException {
           return  getInterpolation().getFirstValue();
        }

        /**
         * Set the value of the first interpolation point as a double. We are not
         * supposed to work here with {@code RealParameter} other than {@code
         * RealLiteral}, so we give directly the {@code double} it must contain.
         * @param d
         * @throws ParameterException
         * If a problem is encountered while retrieving the double value.
         */
        public void setFirstValue(double d) {
            getInterpolation().setFirstValue(d);
        }

        /**
         * Get the value of the second interpolation point as a double. We are not
         * supposed to work here with {@code RealParameter} other than {@code
         * RealLiteral}, so we retrieve directly the {@code double} it contains.
         * @return
         * @throws ParameterException
         * If a problem is encountered while retrieving the double value.
         */
        public double getSecondValue() throws ParameterException {
            return getInterpolation().getSecondValue();
        }


        /**
         * Set the value of the second interpolation point as a double. We are not
         * supposed to work here with {@code RealParameter} other than {@code
         * RealLiteral}, so we give directly the {@code double} it must contain.
         * @param d
         * @throws ParameterException
         * If a problem is encountered while retrieving the double value.
         */
        public void setSecondValue(double d) {
            getInterpolation().setSecondValue(d);
        }

}
