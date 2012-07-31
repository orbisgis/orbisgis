/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
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
import org.orbisgis.legend.structure.fill.FillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.structure.graphic.MarkGraphicLegend;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.StrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;
import org.orbisgis.legend.structure.viewbox.DefaultViewBox;
import org.orbisgis.legend.structure.viewbox.MonovariateProportionalViewBox;

/**
 * Analyzer dedicated to the study of {@code MarkGraphic} instances.
 * @author Alexis Guéganno
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
        FillLegend fillLeg = null;
        if(mark.getFill()!= null){
                FillAnalyzer fa = new FillAnalyzer(mark.getFill());
            fillLeg = (FillLegend)fa.getLegend();
        }
        boolean constantFill = fillLeg == null || fillLeg instanceof ConstantSolidFillLegend;
        //The stroke.
        StrokeLegend strokeLeg = null;
        Stroke stroke = mark.getStroke();
        if(stroke instanceof PenStroke){
            PenStrokeAnalyzer psa = new PenStrokeAnalyzer((PenStroke) stroke);
            strokeLeg = (StrokeLegend) psa.getLegend();
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
