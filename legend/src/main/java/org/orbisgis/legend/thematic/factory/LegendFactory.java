/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.factory;

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.analyzer.symbolizers.AreaSymbolizerAnalyzer;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;

/**
 * Factory dedicated to the generation of {@code Legend} instances. It uses
 * {@code Symbolizers} in input, and produce the corresponding {@code Legend}.
 * @author alexis
 */
public final class LegendFactory {

        private LegendFactory(){}

        /**
         * Try to build a {@code Legend} instance from the given {@code
         * Symbolizer}.
         * @param sym
         * @return
         */
        public static Legend getLegend(Symbolizer sym){
                if(sym instanceof PointSymbolizer){
                        return getLegend((PointSymbolizer) sym);
                } else if(sym instanceof LineSymbolizer){
                        return getLegend((LineSymbolizer) sym);
                } else if(sym instanceof AreaSymbolizer){
                        return getLegend((AreaSymbolizer) sym);
                } else {
                        throw new UnsupportedOperationException("not supported yet");
                }

        }

        private static Legend getLegend(PointSymbolizer sym){
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

        private static Legend getLegend(LineSymbolizer sym){
                LineSymbolizerAnalyzer psa = new LineSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

        private static Legend getLegend(AreaSymbolizer sym){
                AreaSymbolizerAnalyzer psa = new AreaSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

}
