/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.interpolation;

import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;

/**
 * Analysis associated to a linear interpolation. This structure can be defined
 * as an interpolation on any numerci, unaltered, value.</p>
 * <p>Note that we don't care that one of the values given to compute the
 * interpolation is 0 or not. We do it, that's all.
 * @author alexis
 */
public class LinearInterpolationLegend extends InterpolationLegend {

        /**
         * Build a new Legend using the given {@code Interpolate2Real} instance.
         * @param inter
         */
        public LinearInterpolationLegend(Interpolate2Real inter){
                super(inter);
        }
}
