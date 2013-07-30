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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.thematic.*;
import org.orbisgis.legend.thematic.categorize.CategorizedArea;
import org.orbisgis.legend.thematic.categorize.CategorizedLine;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.recode.RecodedArea;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.legend.thematic.recode.RecodedPoint;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.legend.thematic.uom.SymbolUom;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class);

    /**
     * Update the inner CanvasSE. It updates its symbolizer and forces the image to be redrawn.
     */
    public static void updatePreview(MappedLegend legend,
                                     CanvasSE preview,
                                     TablePanel tablePanel) {
        preview.setSymbol(getFallbackSymbolizer(legend));
        if (tablePanel != null) {
            tablePanel.updateTable();
        } else {
            LOGGER.error("Can't update table panel because it is null.");
        }
    }

    public static Symbolizer getFallbackSymbolizer(MappedLegend legend) {
        SymbolizerLegend symLeg;
        SymbolParameters fallbackParameters = legend.getFallbackParameters();
        if (legend instanceof CategorizedLine ||
                legend instanceof RecodedLine) {
            symLeg = new UniqueSymbolLine((LineParameters) fallbackParameters);
        } else if (legend instanceof CategorizedArea ||
                legend instanceof RecodedArea) {
            symLeg =  new UniqueSymbolArea((AreaParameters) fallbackParameters);
        } else if (legend instanceof CategorizedPoint ||
                legend instanceof RecodedPoint) {
            symLeg = new UniqueSymbolPoint((PointParameters) fallbackParameters);
            // Set SymbolUom and OnVertexOnCentroid parameters from the legend.
            ((SymbolUom) symLeg).setSymbolUom(((SymbolUom) legend).getSymbolUom());
            if(((OnVertexOnCentroid) legend).isOnVertex()){
                ((OnVertexOnCentroid) symLeg).setOnVertex();
            } else {
                ((OnVertexOnCentroid) symLeg).setOnCentroid();
            }
        } else {
            throw new IllegalArgumentException("Legend must be Categorized or Recoded");
        }
        // Set StrokeUom from the legend.
        symLeg.setStrokeUom(legend.getStrokeUom());
        return symLeg.getSymbolizer();
    }
}
