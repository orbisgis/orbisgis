/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.interpolation;

import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;

/**
 * Analysis associated to an interpolation made on the square root of a numeric
 * value.</p>
 * <p>Note that we don't care that one of the values given to compute the
 * interpolation is 0 or not. We do it, that's all.
 * @author alexis
 */
public class SqrtInterpolationLegend extends InterpolationLegend {

        /**
         * Build a new Legend using the given {@code Interpolate2Real} instance.
         * @param inter
         */
        public SqrtInterpolationLegend(Interpolate2Real inter){
                super(inter);
        }

}
