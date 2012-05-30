/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.parameter.ColorParameterAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;
import org.orbisgis.legend.structure.fill.CategorizedSolidFillLegend;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2ColorLegend;

/**
 * Used to make analysis and produce {@code LegendStructure} on instances of {@code Fill}.
 * @author alexis
 */
public class FillAnalyzer extends AbstractAnalyzer{

        /**
         * Build a new {@code FillAnalyzer}, and directly analyses the {@code
         * Fill} given in argument.
         * @param fill
         */
        public FillAnalyzer(Fill fill) {
                setLegend(analyzeFill(fill));
        }

        private LegendStructure analyzeFill(Fill f){
                if(f instanceof SolidFill){
                        return analyzeSolidFill((SolidFill) f);
                }
                throw new UnsupportedOperationException("We can't do such an anlysis "
                        + "on Fill instances yet");

        }

        private LegendStructure analyzeSolidFill(SolidFill sf){
                ColorParameterAnalyzer colorPA = new ColorParameterAnalyzer(sf.getColor());
                LegendStructure colorLegend = colorPA.getLegend();
                if(colorLegend instanceof ColorLiteralLegend){
                        return new ConstantSolidFillLegend(sf, (ColorLiteralLegend) colorLegend);
                } else if(colorLegend instanceof Categorize2ColorLegend){
                        return new CategorizedSolidFillLegend(sf, (Categorize2ColorLegend) colorLegend);
                } else if(colorLegend instanceof Recode2ColorLegend){
                        return new RecodedSolidFillLegend(sf, (Recode2ColorLegend) colorLegend);
                }
                throw new UnsupportedOperationException("We can't do such an anlysis "
                        + "on Fill instances yet");
                
        }
}
