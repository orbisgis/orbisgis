/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.symbolizers;

import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.MarkGraphicAnalyzer;
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * PointSymbolizer} instances. It's basically searching for the configurations
 * that can be found in its inner {@code Graphic} instance.
 * @author alexis
 */
public class PointSymbolizerAnalyzer extends AbstractAnalyzer {

    /**
     * Build a new {@code Analyzer} from the given PointSymbolizer.
     * @param symbolizer
     */
    public PointSymbolizerAnalyzer(PointSymbolizer symbolizer){
        setLegend(analyze(symbolizer));
    }

    private LegendStructure analyze(PointSymbolizer symbolizer){
        //We must retrieve the inner graphic, assuming there is only one.
        GraphicCollection graphs = symbolizer.getGraphicCollection();
        if(graphs.getNumGraphics() ==1 ){
            //We can start our analysis. We basically analyse the present Graphic.
            Graphic graphic = graphs.getGraphic(0);
            if(graphic instanceof MarkGraphic){
                MarkGraphicAnalyzer mga = new MarkGraphicAnalyzer((MarkGraphic) graphic);
                LegendStructure gl = mga.getLegend();
                if(gl instanceof ConstantWKNLegend){
                    return new UniqueSymbolPoint(symbolizer, (ConstantWKNLegend) gl);
                } else if(gl instanceof ProportionalWKNLegend){
                    return new ProportionalPoint(symbolizer, (ProportionalWKNLegend) gl);
                } else {
                    throw new UnsupportedOperationException("Soon !");
                }
            } else {
                throw new UnsupportedOperationException("We can just analyze MarkGraphic.");
            }
        } else {
            throw new UnsupportedOperationException("Can't analyze a PointSymbolizer"
                    + "that contains more than one Graphic instance");
        }
    }

}
