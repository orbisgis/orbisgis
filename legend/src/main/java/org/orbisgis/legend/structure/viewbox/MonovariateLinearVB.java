/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.legend.NumericLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;

/**
 * A symbol can be said proportional is one of is dimensions variates linearly
 * upon a numeric field.
 * @author alexis
 */
public class MonovariateLinearVB extends DefaultViewBox {

        private boolean isHeight;

        /**
         * Build a new {@code MonovariateLinearVB} using the legends associated
         * to the height and width of the oridinal {@code ViewBox}. {@code
         * isHeight} is used to determine if the {@code InterpolationLegend} is
         * associated to the height or to the width.
         * @param height
         * @param width
         * @param view
         * @param isheight
         * If {@code true} we'll consider that the interpolation is made on
         * the height, and on the width otherwise.
         */
        public MonovariateLinearVB(NumericLegend height, NumericLegend width,
                        ViewBox view, boolean isheight){
                super(height, width, view);
                isHeight = isheight;
        }

        /**
         * If this method returns {@code true}, the interpolation is supposed to
         * be made on the height. If it returns {@code false}, the interpolation
         * is supposed to be mad on the width of the underlying {@code ViewBox}.
         * @return
         */
        public boolean isHeight(){
                return isHeight;
        }

        /**
         * Get the interpolation legend associated to this {@code Legend}.
         * @return
         */
        public LinearInterpolationLegend getInterpolation() {
                if(isHeight){
                        return (LinearInterpolationLegend) getHeightLegend();
                } else {
                        return (LinearInterpolationLegend) getWidthLegend();
                }
        }

}
