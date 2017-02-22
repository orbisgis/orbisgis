/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.orbisgis.coremap.renderer.se.AreaSymbolizer;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.coremap.renderer.se.parameter.Recode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.thematic.categorize.CategorizedArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.recode.RecodedArea;

import java.util.List;

/**
 * This {@code Analyzer} realization is dedicated to the study of {@code
 * AreaSymbolizer} instances.
 * @author Alexis Guéganno
 */
public class AreaSymbolizerAnalyzer extends SymbolizerTypeAnalyzer {

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
        if(validateStrokeAndFill(symbolizer.getStroke(),symbolizer.getFill())){
            analyzeParameters(symbolizer);
            boolean b = isAnalysisLight() && isAnalysisUnique() && isFieldUnique();
            if(b){
                //We know we can recognize the analysis. We just have to check
                //there is something that is not a literal...
                UsedAnalysis ua = getUsedAnalysis();
                List<SeParameter> an = ua.getAnalysis();
                if (an.isEmpty()) {
                    //Unique Symbol
                    return new UniqueSymbolArea(symbolizer);
                } else {
                    SeParameter p = an.get(0);
                    if (p instanceof Recode) {
                        return new RecodedArea(symbolizer);
                    } else if (p instanceof Categorize) {
                        return new CategorizedArea(symbolizer);
                    }
                }
            } else {
                throw new UnsupportedOperationException(getStatus());
            }
        }
        throw new UnsupportedOperationException("We can recognize patterns made with PenStroke and SolidFill" +
                    "instances only");

    }

}
