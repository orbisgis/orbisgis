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
 * @author Alexis Guéganno
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
