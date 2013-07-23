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
package org.orbisgis.view.toc.actions.cui.legend;

import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.categorize.CategorizedArea;
import org.orbisgis.legend.thematic.categorize.CategorizedLine;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.legend.thematic.recode.RecodedArea;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.legend.thematic.recode.RecodedPoint;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legends.*;

/**
 * A helper class to associate an {@link ILegendPanel} to a {@link Legend}.
 */
public class ILegendPanelFactory {

    /**
     * Return a new ILegendPanel instance associated to the given Legend.
     *
     * @param lc     LegendContext
     * @param legend Legend
     *
     * @return Associated ILegendPanel
     */
    public static ILegendPanel getILegendPanel(LegendContext lc, Legend legend) {
        ILegendPanel ilp = getILegendPanel(lc, legend.getLegendTypeName());
        ilp.initialize(lc, legend);
        return ilp;
    }

    /**
     * Return a new ILegendPanel instance associated to the Legend with the
     * given name.
     *
     *
     * @param lc
     * @param legendName Legend name
     * @return Associated ILegendPanel
     */
    public static ILegendPanel getILegendPanel(LegendContext lc, String legendName) {
        ILegendPanel ilp = null;
        if (legendName.equals(UniqueSymbolPoint.NAME)) {
            ilp = new PnlUniquePointSE();
        } else if (legendName.equals(UniqueSymbolLine.NAME)) {
            ilp = new PnlUniqueLineSE();
        } else if (legendName.equals(UniqueSymbolArea.NAME)) {
            ilp = new PnlUniqueAreaSE();
        } else if (legendName.equals(ProportionalPoint.NAME)) {
            ilp = new PnlProportionalPointSE();
        } else if (legendName.equals(ProportionalLine.NAME)) {
            ilp = new PnlProportionalLineSE();
        } else if (legendName.equals(RecodedPoint.NAME)) {
            ilp = new PnlRecodedPoint();
        } else if (legendName.equals(RecodedLine.NAME)) {
            ilp = new PnlRecodedLine();
        } else if (legendName.equals(RecodedArea.NAME)) {
            ilp = new PnlRecodedArea();
        } else if (legendName.equals(CategorizedPoint.NAME)) {
            ilp = new PnlCategorizedPoint();
        } else if (legendName.equals(CategorizedLine.NAME)) {
            ilp = new PnlCategorizedLine();
        } else if (legendName.equals(CategorizedArea.NAME)) {
            ilp = new PnlCategorizedArea();
        } else {
            throw new UnsupportedOperationException("No available " +
                    "ILegendPanel for legend" + legendName + ".");
        }
        ilp.initialize(lc);
        return ilp;
    }
}
