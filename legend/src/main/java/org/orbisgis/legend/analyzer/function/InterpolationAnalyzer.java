/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.function;

import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.structure.interpolation.InterpolationLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;

/**
 * Analyzes an interpolation node. This class will try to determine which legend
 * can be associated to the interpolation method used in this interpolation and
 * to the FES function applied on the input value (if any).
 * @author alexis
 */
public class InterpolationAnalyzer extends AbstractAnalyzer{

        /**
         * Build A new {@code InterpolationAnalyzer} from the {@code Interpolate}
         * given in parameter.
         * @param inter
         */
        public InterpolationAnalyzer(Interpolate inter){
                if(inter instanceof Interpolate2Real){
                        analyzeRealInterpolation((Interpolate2Real) inter);
                } else if(inter instanceof Interpolate2Color){
                        throw new UnsupportedOperationException("Raster are not supported yet");
                }
        }

        /**
         * We determine which case we are treating.
         * @param inter
         */
        private void analyzeRealInterpolation(Interpolate2Real inter) {
                RealParameter rp =  inter.getLookupValue();
                if(rp instanceof RealAttribute){
                        setLegend(new LinearInterpolationLegend(inter));
                } else if(rp instanceof RealFunction){
                        RealFunction rf = (RealFunction) rp;
                        if(rf.getOperator().equals(RealFunction.Operators.SQRT)){
                                setLegend(new SqrtInterpolationLegend(inter));
                        } else {
                                setLegend(new InterpolationLegend(inter));
                        }
                } else {
                        setLegend(new InterpolationLegend(inter));
                }
        }
}
