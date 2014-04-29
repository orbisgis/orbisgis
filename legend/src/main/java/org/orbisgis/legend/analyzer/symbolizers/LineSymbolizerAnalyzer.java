/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.coremap.renderer.se.parameter.Recode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.coremap.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.coremap.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.thematic.categorize.CategorizedLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.legend.thematic.recode.RecodedLine;

import java.util.List;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * LineSymbolizer} instances.
 *
 * @author Alexis Guéganno
 */
public class LineSymbolizerAnalyzer extends SymbolizerTypeAnalyzer {

    /**
     * Build a new instance of this {@code Analyzer} using the given {@code
     * LineSymbolizer}. The obtained {@code LegendStructure}, if any, will be retrievable
     * from the {@code getLegend()} method just after initialization.
     *
     * @param symbolizer
     */
    public LineSymbolizerAnalyzer(LineSymbolizer symbolizer) {
        setLegend(analyze(symbolizer));
    }

    private LegendStructure analyze(LineSymbolizer symbolizer) {
        //We validate the Stroke :
        Stroke str = symbolizer.getStroke();
        if (validateStroke(str)) {
            //We validate the analysis made in the symbolizer.
            analyzeParameters(symbolizer);
            if (isAnalysisLight() && isFieldUnique() && isAnalysisUnique()) {
                //We know we can recognize the analysis. We just have to check
                //there is something that is not a literal...
                UsedAnalysis ua = getUsedAnalysis();
                List<SeParameter> an = ua.getAnalysis();
                if (an.isEmpty()) {
                    //Unique Symbol
                    return new UniqueSymbolLine(symbolizer);
                } else {
                    SeParameter p = an.get(0);
                    if (p instanceof Recode) {
                        return new RecodedLine(symbolizer);
                    } else if (p instanceof Categorize) {
                        return new CategorizedLine(symbolizer);
                    } else if (p instanceof RealParameter && validateLinearInterpolate((RealParameter)p)) {
                        //We need to analyze the width and its Interpolate instance
                        return new ProportionalLine(symbolizer);
                    }
                }

            } else {
                throw new UnsupportedOperationException(getStatus());
            }

        }
        throw new UnsupportedOperationException("We are not able to analyze"
                    + "strokes other than PenStroke");
    }

    /**
     * Check that the given {@link RealParameter} is a valid linear interpolation so that it can be used safely to build
     * a proportional line.
     * @param parameter
     * @return
     */
    public boolean validateLinearInterpolate(RealParameter parameter){
        if(parameter instanceof Interpolate2Real){
                return ((Interpolate2Real)parameter).getLookupValue() instanceof RealAttribute;
        }
        return false;
    }

}
