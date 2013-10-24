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
import org.orbisgis.view.toc.actions.cui.legends.ui.*;

/**
 * A helper class to associate an {@link ILegendPanel} to a {@link Legend}.
 */
public class ILegendPanelFactory {

    /**
     * Return a new ILegendPanel instance associated to the given Legend.
     * <p/>
     * Used in {@link org.orbisgis.view.toc.actions.cui.LegendTree#addRule}
     * and {@link org.orbisgis.view.toc.actions.cui.SimpleStyleEditor#addSymbolPanel}.
     *
     * @param lc     LegendContext
     * @param legend Legend
     * @return Associated ILegendPanel
     */
    public static ILegendPanel getILegendPanel(LegendContext lc, Legend legend) {
        if (legend instanceof UniqueSymbolPoint) {
            return new PnlUniquePointSE(lc, (UniqueSymbolPoint) legend);
        } else if (legend instanceof UniqueSymbolLine) {
            return new PnlUniqueLineSE((UniqueSymbolLine) legend);
        } else if (legend instanceof UniqueSymbolArea) {
            return new PnlUniqueAreaSE((UniqueSymbolArea) legend);
        } else if (legend instanceof ProportionalPoint) {
            return new PnlProportionalPointSE(lc, (ProportionalPoint) legend);
        } else if (legend instanceof ProportionalLine) {
            return new PnlProportionalLineSE(lc, (ProportionalLine) legend);
        } else if (legend instanceof RecodedPoint) {
            return new PnlRecodedPoint(lc, (RecodedPoint) legend);
        } else if (legend instanceof RecodedLine) {
            return new PnlRecodedLine(lc, (RecodedLine) legend);
        } else if (legend instanceof RecodedArea) {
            return new PnlRecodedArea(lc, (RecodedArea) legend);
        } else if (legend instanceof CategorizedPoint) {
            return new PnlCategorizedPoint(lc, (CategorizedPoint) legend);
        } else if (legend instanceof CategorizedLine) {
            return new PnlCategorizedLine(lc, (CategorizedLine) legend);
        } else if (legend instanceof CategorizedArea) {
            return new PnlCategorizedArea(lc, (CategorizedArea) legend);
        } else {
            throw new UnsupportedOperationException("No available " +
                    "ILegendPanel for legend " + legend.getLegendTypeName() + ".");
        }
    }

    /**
     * Return a new ILegendPanel instance associated to the Legend with the
     * given name.
     * <p/>
     * Used in {@link org.orbisgis.view.toc.actions.cui.LegendUIChooser#getSelectedPanel}.
     *
     * @param lc
     * @param legendName Legend name
     * @return Associated ILegendPanel
     */
    public static ILegendPanel getPanelForLegendUIChooser(LegendContext lc, String legendName) {
        if (legendName.equals(UniqueSymbolPoint.NAME)) {
            return new PnlUniquePointSE(lc);
        } else if (legendName.equals(UniqueSymbolLine.NAME)) {
            return new PnlUniqueLineSE();
        } else if (legendName.equals(UniqueSymbolArea.NAME)) {
            return new PnlUniqueAreaSE();
        } else if (legendName.equals(ProportionalPoint.NAME)) {
            return new PnlProportionalPointSE(lc);
        } else if (legendName.equals(ProportionalLine.NAME)) {
            return new PnlProportionalLineSE(lc);
        } else if (legendName.equals(RecodedPoint.NAME)) {
            return new PnlRecodedPoint(lc);
        } else if (legendName.equals(RecodedLine.NAME)) {
            return new PnlRecodedLine(lc);
        } else if (legendName.equals(RecodedArea.NAME)) {
            return new PnlRecodedArea(lc);
        } else if (legendName.equals(CategorizedPoint.NAME)) {
            return new PnlCategorizedPoint(lc);
        } else if (legendName.equals(CategorizedLine.NAME)) {
            return new PnlCategorizedLine(lc);
        } else if (legendName.equals(CategorizedArea.NAME)) {
            return new PnlCategorizedArea(lc);
        } else {
            throw new UnsupportedOperationException("No available " +
                    "ILegendPanel for legend" + legendName + ".");
        }
    }
}
