/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * Represents a ViewBox that does not change, whatever the processed data is.
 *
 * @author alexis
 */
public class ConstantViewBox extends DefaultViewBox {

        /**
         * Build a new instance of {@code ConstantViewBox}. We directly use {@code
         * RealLiteralLegend} instances.
         *
         * @param height
         * @param width
         * @param view
         */
        public ConstantViewBox(RealLiteralLegend height, RealLiteralLegend width, ViewBox view) {
                super(height, width, view);
        }

        /**
         * As {@code ViewBox} instances can be defined with only one dimension
         * parameter, we must be able to treat this case.
         * @param realLiteralLegend
         * @param isheight
         * @param vb 
         */
        public ConstantViewBox(RealLiteralLegend realLiteralLegend, boolean isheight, ViewBox vb) {
                super(realLiteralLegend, isheight, vb);
        }

        /**
         * Gets the width of the associated {@code ViewBox}. As it is defined
         * as a {@code Literal}, its value is constant whatever the input data
         * are.
         * @return
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         */
        public Double getWidth() {
            Double ret = null;
            RealLiteralLegend rll = (RealLiteralLegend) getWidthLegend();
            if(rll != null){
                ret = rll.getDouble();
            }
            return ret;
        }

        /**
         * Gets the height of the associated {@code ViewBox}. As it is defined
         * as a {@code Literal}, its value is constant whatever the input data
         * are.
         * @return
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         */
        public Double getHeight() {
            Double ret = null;
            RealLiteralLegend rll = (RealLiteralLegend) getHeightLegend();
            if(rll != null){
                ret = rll.getDouble();
            }
            return ret;
        }

        /**
         * Set the Width of this ViewBox.
         * @param d
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         * @throws IllegalArgumentException
         * If {@code d} is null and {@code getHeight()} returns null too. At
         * least one of the dimensions must be not null.
         */
        public void setHeight(Double d) {
            if(d==null){
                if(getWidth() != null){
                    setHeightLegend(null);
                    getViewBox().setHeight(null);
                } else {
                    throw new IllegalArgumentException("you're not supposed to"
                            + "set both height and width of a viewbox to null.");
                }
            } else {
                RealLiteralLegend rll = (RealLiteralLegend) getHeightLegend();
                if(rll == null){
                    RealLiteral rlit = new RealLiteral(d);
                    getViewBox().setHeight(rlit);
                    rll = new RealLiteralLegend(rlit);
                    setHeightLegend(rll);
                }
                rll.setDouble(d);
            }
        }

        /**
         * Set the Width of this ViewBox.
         * @param d
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         * @throws IllegalArgumentException
         * If {@code d} is null and {@code getHeight()} returns null too. At
         * least one of the dimensions must be not null.
         */
        public void setWidth(Double d) {
            if(d==null){
                if(getHeight() != null){
                    setWidthLegend(null);
                    getViewBox().setWidth(null);
                } else {
                    throw new IllegalArgumentException("you're not supposed to"
                            + "set both height and width of a viewbox to null.");
                }
            } else {
                RealLiteralLegend rll = (RealLiteralLegend) getWidthLegend();
                if(rll == null){
                    RealLiteral rlit = new RealLiteral(d);
                    getViewBox().setWidth(rlit);
                    rll = new RealLiteralLegend(rlit);
                    setWidthLegend(rll);
                }
                rll.setDouble(d);
            }
        }
}
