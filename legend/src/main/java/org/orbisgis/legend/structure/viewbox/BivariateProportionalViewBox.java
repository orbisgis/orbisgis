/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.legend.structure.interpolation.InterpolationLegend;

/**
 * When using a {@code ViewBox} where both dimensions are associated with an
 * interpolation which is both linear and made on the raw values, we are working
 * with a bivariate proportional symbol.
 * @author alexis
 */
public class BivariateProportionalViewBox extends DefaultViewBox {

        /**
         * Build a new {@code BivariateProportionalViewBox} from the
         * interpolations given in parameters, and the ViewBox associated to
         * this {@code Legend}.
         * @param height
         * @param width
         * @param view
         */
        public BivariateProportionalViewBox(InterpolationLegend height, InterpolationLegend width,
                        ViewBox view){
                super(height, width, view);
        }

}
