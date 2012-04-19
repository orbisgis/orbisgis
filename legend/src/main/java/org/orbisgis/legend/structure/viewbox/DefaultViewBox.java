/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.NumericLegend;

/**
 * {@code ViewBox} instances can all be described as being associated to two
 * random {@code NumericLegend} instances. Consequently, this class is the base
 * legend description of {@code ViewBox}, and can be reused freely for more
 * accurate descriptions.
 * @author alexis
 */
public class DefaultViewBox implements LegendStructure{

        private ViewBox viewBox;
        private NumericLegend height;
        private NumericLegend width;

        /**
         * Build a new {@code DefaultViewBox} with the given parameters, using
         * directly the two needed {@code NumericLegend} instances.
         * @param height
         * @param width
         * @param view
         */
        public DefaultViewBox(NumericLegend height, NumericLegend width, ViewBox view){
                viewBox = view;
                this.height = height;
                this.width = width;
        }

        /**
         * Build a new {@code DefaultViewBox}. We have just one {@code
         * NumericLegend} here. Its meaning is dependant on {@code isHeight}. If
         * {@code isHeight}, {@code nl} is supposed to be the description of the
         * height of {@code view}. If not {@ode isHeight}, it is the description
         * of the width.
         * @param nl
         * @param isHeight
         * @param view
         */
        public DefaultViewBox(NumericLegend nl, boolean isHeight, ViewBox view){
                if(isHeight){
                        height = nl;
                        width = null;
                } else {
                        height = null;
                        width = nl;
                }
                viewBox = view;
        }

        /**
         * Gets the {@code ViewBox} associated to this LegendStructure.
         * @return
         */
        public ViewBox getViewBox() {
                return viewBox;
        }

        /**
         * Gets the {@code LegendStructure} associated to the height of this {@code
         * ViewBox}.
         * @return
         */
        public NumericLegend getHeightLegend() {
                return height;
        }

        /**
         * Gets the {@code LegendStructure} associated to the width of this {@code
         * ViewBox}.
         * @return
         */
        public NumericLegend getWidthLegend() {
                return width;
        }

        /**
         * Gets the {@code LegendStructure} associated to the height of this {@code
         * ViewBox}.
         * @return
         */
        public void setHeightLegend(NumericLegend nl) {
                height = nl;
        }

        /**
         * Gets the {@code LegendStructure} associated to the width of this {@code
         * ViewBox}.
         * @param nl
         */
        public void setWidthLegend(NumericLegend nl) {
                width = nl;
        }

}
