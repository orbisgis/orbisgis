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
 * @author Alexis Guéganno
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
