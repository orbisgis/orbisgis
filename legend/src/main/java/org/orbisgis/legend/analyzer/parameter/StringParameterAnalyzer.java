/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.parameter;

import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2StringLegend;

/**
 *
 * @author alexis
 */
public class StringParameterAnalyzer extends AbstractLiteralValidator {

        private StringParameter sp;

        /**
         * Build a new {@code Analyzer} from the given {@code StringParameter}.
         * Note that the thematic analysis extraction is made during
         * initialization.
         * @param sp
         */
        public StringParameterAnalyzer(StringParameter sp) {
                this.sp = sp;
                setLegend(analyzeParameter());
        }

        private LegendStructure analyzeParameter(){
                if(sp instanceof StringLiteral){
                        return new StringLiteralLegend((StringLiteral)sp);
                } else if(sp instanceof Categorize2String){
                        if(validateCategorize((Categorize2String) sp)){
                                return new Categorize2StringLegend((Categorize2String) sp);
                        }
                } else if(sp instanceof Recode2String){
                        if(validateRecode((Recode2String) sp)){
                                return new Recode2StringLegend((Recode2String) sp);
                        }
                } else {
                        throw new UnsupportedOperationException("We've been unable to recognize this StringParameter.");
                }
                throw new UnsupportedOperationException("We've been unable to recognize this StringParameter.");
        }

}
