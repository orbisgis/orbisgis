/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.legend.analyzer.symbolizers;

import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;

import java.util.List;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * PointSymbolizer} instances. It's basically searching for the configurations
 * that can be found in its inner {@code Graphic} instance.
 * @author Alexis Guéganno
 */
public class PointSymbolizerAnalyzer extends SymbolizerTypeAnalyzer {

    /**
     * Build a new {@code Analyzer} from the given PointSymbolizer.
     * @param symbolizer
     */
    public PointSymbolizerAnalyzer(PointSymbolizer symbolizer){
        setLegend(analyze(symbolizer));
    }

    private LegendStructure analyze(PointSymbolizer sym) {
        //We validate the graphic
        GraphicCollection gc = sym.getGraphicCollection();
        if (gc.getNumGraphics() != 1) {
            throw new UnsupportedOperationException("We don't manage mixed graphic yet.");
        }
        Graphic g = gc.getGraphic(0);
        if (validateGraphic(g)) {
            analyzeParameters(sym);
            boolean b = isAnalysisLight() && isAnalysisUnique() && isFieldUnique();
            if(b){
                    //We know we can recognize the analysis. We just have to check
                    //there is something that is not a literal...
                    UsedAnalysis ua = getUsedAnalysis();
                    List<SeParameter> an = ua.getAnalysis();
                    if(an.isEmpty()){
                            //Unique Symbol
                            return new UniqueSymbolPoint(sym);
                    } else {
                            SeParameter p = an.get(0);
                            if(p instanceof Recode){
                                    throw new UnsupportedOperationException("Not yet !");
                            } else if(p instanceof Categorize){
                                    throw new UnsupportedOperationException("Not yet !");
                            } else if(p instanceof RealParameter && validateInterpolateForProportionalPoint((RealParameter) p)){
                                    //We need to analyze the ViewBox and its Interpolate instance(s)
                                    return new ProportionalPoint(sym);
                            }
                    }
            } else {
                throw new UnsupportedOperationException(getStatus());
            }
        }
        throw new UnsupportedOperationException("We can only work with MarkGraphic instances for now.");
    }

    /**
     * Checks that the given RealParameter is an instance of {@link Interpolate2Real} that can be used to build a
     * proportional point, ie that it is made on the square root of a numeric attribute.
     * @param rp
     * @return
     */
    public boolean validateInterpolateForProportionalPoint(RealParameter rp){
        if(rp instanceof Interpolate2Real){
            RealParameter look =  ((Interpolate2Real)rp).getLookupValue();
            if(look instanceof RealFunction){
                RealFunction rf = (RealFunction) look;
                List<RealParameter> ops = rf.getOperands();
                if(!ops.isEmpty() && rf.getOperator().equals(RealFunction.Operators.SQRT)
                                && ops.size() == 1 && ops.get(0) instanceof RealAttribute){
                    return true;
                }
            }
        }
        return false;
    }

}
