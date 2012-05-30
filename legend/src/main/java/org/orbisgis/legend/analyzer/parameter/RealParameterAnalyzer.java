/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.parameter;

import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator;
import org.orbisgis.legend.analyzer.function.InterpolationAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2RealLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2RealLegend;

/**
 * This class analyzes instances of {@code RealParameter} to determine if they
 * are literal values, or richer function.
 * @author alexis
 */
public class RealParameterAnalyzer extends AbstractLiteralValidator{

        private RealParameter rp;

        /**
         * Build a new {@code RealParameterAnalyzer}, using {@code input}.
         * @param input
         */
        public RealParameterAnalyzer(RealParameter input){
                //We want to store the RealParameter for further use...
                rp = input;
                //...and we want to analyze it.
                setLegend(analyzeRealParameter());
        }

        private LegendStructure analyzeRealParameter(){
                if(rp instanceof Interpolate2Real){
                        InterpolationAnalyzer ia = new InterpolationAnalyzer((Interpolate2Real) rp);
                        return ia.getLegend();
                } else if(rp instanceof RealLiteral){
                        return new RealLiteralLegend((RealLiteral) rp);
                } else if(rp instanceof Categorize2Real){
                        Categorize2Real c2r = (Categorize2Real) rp;
                        if(validateCategorize(c2r)){
                                return new Categorize2RealLegend(c2r);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }
                } else if(rp instanceof Recode2Real){
                        Recode2Real r2r = (Recode2Real) rp;
                        if(validateRecode(r2r)){
                                return new Recode2RealLegend(r2r);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }

                } else {
                        throw new UnsupportedOperationException("Not spported yet");
                }
        }

}
