/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.parameter;

import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2ColorLegend;

/**
 * This class analyzes instances of {@code ColorParameter} to determine if they
 * are literal values, or richer functions. It is the best way to find
 * particular analysis made on the color of a symbol, whatever it is.
 * @author alexis
 */
public class ColorParameterAnalyzer extends AbstractLiteralValidator {

        private ColorParameter cp;

        /**
         * Build a new {@code RealParameterAnalyzer}, using {@code input}.
         * The {@code List} that will be returned by {@code getLegends} is
         * built by this constructor.
         * @param input
         */
        public ColorParameterAnalyzer(ColorParameter input){
                //We want to store the RealParameter for further use...
                cp = input;
                //...and we want to analyze it.
                setLegend(analyzeColorParameter());
        }

        private LegendStructure analyzeColorParameter(){
                if (cp instanceof Recode2Color){
                        Recode2Color r2c = (Recode2Color)cp;
                        if(validateRecode(r2c)){
                                return new Recode2ColorLegend(r2c);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }
                } else if(cp instanceof Categorize2Color){
                        Categorize2Color c2c = (Categorize2Color) cp;
                        if(validateCategorize(c2c)){
                                return new Categorize2ColorLegend(c2c);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }
                } else if(cp instanceof ColorLiteral){
                        return new ColorLiteralLegend((ColorLiteral) cp);
                } else {
                        throw new UnsupportedOperationException("Not spported yet");
                }
        }

}
