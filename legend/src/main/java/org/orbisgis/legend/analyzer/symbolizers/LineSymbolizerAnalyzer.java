/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.symbolizers;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * LineSymbolizer} instances. 
 * @author alexis
 */
public class LineSymbolizerAnalyzer extends AbstractAnalyzer {

    /**
     * Build a new instance of this {@code Analyzer} using the given {@code
     * Linesymbolizer}. The obtained {@code LegendStructure}, if any, will be retrievable
     * from the {@code getLegend()} method just after initialization.
     * @param symbolizer
     */
    public LineSymbolizerAnalyzer(LineSymbolizer symbolizer){
        setLegend(analyze(symbolizer));
    }

    private LegendStructure analyze(LineSymbolizer symbolizer){
        Stroke str = symbolizer.getStroke();
        if(str instanceof PenStroke){
            //We can analyze the PenStroke to know what it is.
            PenStrokeAnalyzer psa = new PenStrokeAnalyzer((PenStroke) str);
            LegendStructure leg = psa.getLegend();
            if(leg instanceof ConstantPenStrokeLegend){
                return new UniqueSymbolLine(symbolizer, (ConstantPenStrokeLegend) leg);
            } else if(leg instanceof ProportionalStrokeLegend){
                return new ProportionalLine(symbolizer, (ProportionalStrokeLegend) leg);
            } else {
                throw new UnsupportedOperationException("We are not able to "
                        + "find any known pattern in this symbolizer");
            }
        } else {
            throw new UnsupportedOperationException("We are not able to anlyze"
                    + "strokes other than PenStroke");
        }
    }

}
