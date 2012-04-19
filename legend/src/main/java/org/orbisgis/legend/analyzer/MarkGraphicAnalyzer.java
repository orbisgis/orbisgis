/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.parameter.StringParameterAnalyzer;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.structure.graphic.MarkGraphicLegend;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;
import org.orbisgis.legend.structure.viewbox.DefaultViewBox;
import org.orbisgis.legend.structure.viewbox.MonovariateProportionalViewBox;

/**
 * Analyzer dedicated to the study of {@code MarkGraphic} instances.
 * @author alexis
 */
public class MarkGraphicAnalyzer extends AbstractAnalyzer {

    /**
     * Build the analyzer using the given {@code MarkGraphic} instance. The
     * analysis is made inline.
     * @param mark
     */
    public MarkGraphicAnalyzer(MarkGraphic mark){
        setLegend(analyzeMarkGraphic(mark));
    }

    private LegendStructure analyzeMarkGraphic(MarkGraphic mark){
        //We will analyze each of the four important parameters (the stroke,
        //the fill, the wellknown name and the viewbox) to determine what can be
        //this MarkGraphic.
        //The fill.
        LegendStructure fillLeg = null;
        if(mark.getFill()!= null){
            fillLeg = new FillAnalyzer(mark.getFill()).getLegend();
        }
        boolean constantFill = fillLeg == null || fillLeg instanceof ConstantSolidFillLegend;
        //The stroke.
        LegendStructure strokeLeg = null;
        Stroke stroke = mark.getStroke();
        if(stroke instanceof PenStroke){
            strokeLeg = new PenStrokeAnalyzer((PenStroke) stroke).getLegend();
        }
        boolean constantStroke = strokeLeg == null || strokeLeg instanceof ConstantPenStrokeLegend;
        //The form.
        LegendStructure wknLegend = null;
        StringParameter wkn = mark.getWkn();
        if(wkn != null){
            wknLegend = new StringParameterAnalyzer(wkn).getLegend();
        }
        boolean constantWkn = wknLegend == null || wknLegend instanceof StringLiteralLegend;
        //The size
        DefaultViewBox vbl = null;
        ViewBox vb = mark.getViewBox();
        if(vb != null){
            vbl = (DefaultViewBox) new ViewBoxAnalyzer(vb).getLegend();
        }
        boolean constantForm = constantWkn && constantFill && constantStroke;
        if(constantForm){
            return analyzeConstantForm(mark, (StringLiteralLegend) wknLegend,
                    vbl, (ConstantSolidFillLegend) fillLeg,
                    (ConstantPenStrokeLegend) strokeLeg);
        }
        //If we are here, we are dealing with a configuration we can't recognize.
        //Let's return a simple MarkGraphicLegend
        return new MarkGraphicLegend(mark, wknLegend, vbl, fillLeg, strokeLeg);
    }
    
    /**
     * To be used with ConstantFormWKN instances.
     */
    private LegendStructure analyzeConstantForm(MarkGraphic mark, StringLiteralLegend wknLegend,
                    DefaultViewBox vbl, ConstantSolidFillLegend fillLeg,
                    ConstantPenStrokeLegend strokeLeg){
        if(vbl == null || vbl instanceof ConstantViewBox){
            return new ConstantWKNLegend(mark, (StringLiteralLegend) wknLegend,
                    (ConstantViewBox) vbl, (ConstantSolidFillLegend) fillLeg,
                    (ConstantPenStrokeLegend) strokeLeg);
        } else if(vbl instanceof MonovariateProportionalViewBox) {
            return new ProportionalWKNLegend(mark, wknLegend,
                    (MonovariateProportionalViewBox) vbl, fillLeg, strokeLeg);
        }
        return new MarkGraphicLegend(mark, wknLegend, vbl, fillLeg, strokeLeg);

    }

}
