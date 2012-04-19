/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.symbolizers;

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.FillAnalyzer;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * LineSymbolizer} instances. 
 * @author alexis
 */
public class AreaSymbolizerAnalyzer extends AbstractAnalyzer {

    /**
     * Build a new instance of this {@code Analyzer} using the given {@code
     * Linesymbolizer}. The obtained {@code LegendStructure}, if any, will be retrievable
     * from the {@code getLegend()} method just after initialization.
     * @param symbolizer
     */
    public AreaSymbolizerAnalyzer(AreaSymbolizer symbolizer){
        setLegend(analyze(symbolizer));
    }

    private LegendStructure analyze(AreaSymbolizer symbolizer){
        Stroke str = symbolizer.getStroke();
        LegendStructure psleg = null;
        if(str instanceof PenStroke){
            //We can analyze the PenStroke to know what it is.
            PenStrokeAnalyzer psa = new PenStrokeAnalyzer((PenStroke) str);
            psleg = psa.getLegend();
        }
        if(str == null || psleg instanceof ConstantPenStrokeLegend){
            //We retrieve the fill
            Fill f = symbolizer.getFill();
            LegendStructure fls = null;
            if(f != null){
                fls = new FillAnalyzer(f).getLegend();
            }
            if(fls instanceof ConstantSolidFillLegend || f == null){
                return new UniqueSymbolArea(symbolizer);
            }
            throw new UnsupportedOperationException("We just find unique symbols for now.");
        } else {
            throw new UnsupportedOperationException("We are not able to anlyze"
                    + "strokes other than PenStroke");
        }
    }

}
