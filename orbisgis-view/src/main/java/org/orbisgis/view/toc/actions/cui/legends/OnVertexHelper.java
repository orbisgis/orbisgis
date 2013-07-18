/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */

package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.thematic.SymbolizerLegend;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.legend.thematic.recode.RecodedPoint;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * Helper class for creating the "On vertex"/"On centroid" button group panel.
 *
 * @author Adam Gouge
 */
public class OnVertexHelper {

    private static final String VERTEX = I18n.marktr("Vertex");
    private static final String CENTROID = I18n.marktr("Centroid");

    /**
     * Called by listeners to update the fallback symbol preview (and the
     * table preview if necessary).
     *
     * @param panel    The panel. Usually "this"
     * @param onVertex True if the symbol is to be placed on vertices; false
     *                 if on the centroid
     */
    public static void changeOnVertex(AbstractFieldPanel panel,
                                      boolean onVertex) {
        CanvasSE prev = panel.getPreview();
        Symbolizer symbol = prev.getSymbol();
        if (symbol instanceof PointSymbolizer) {
            ((PointSymbolizer) symbol).setOnVertex(onVertex);
            prev.imageChanged();
            if (panel instanceof PnlAbstractTableAnalysis) {
                ((PnlAbstractTableAnalysis) panel).updateTable();
            }
        }
    }

    /**
     * Returns the panel used to configure if the symbol must be drawn on
     * the vertices or on the centroid.
     *
     * @param panel  The panel. Usually "this"
     * @param legend A ProportionalPoint, UniqueSymbolPoint, CategorizedPoint
     *               or RecodedPoint
     *
     * @return On vertex / On centroid configuration panel
     */
    public static JPanel pnlOnVertex(AbstractFieldPanel panel,
                                     SymbolizerLegend legend,
                                     I18n I18N) {

        // Make sure we are working with a valid panel. That is, one that
        // implements onClickVertex() and onClickCentroid() methods.
        if (!(panel instanceof PnlProportionalPointSE ||
                panel instanceof PnlUniquePointSE ||
                panel instanceof PnlCategorizedPoint ||
                panel instanceof PnlRecodedPoint)) {
            throw new IllegalArgumentException(
                    "On vertex/On centroid config panel is not available " +
                            "for " + legend.getLegendTypeName() + " panels.");
        }

        // Make sure we are working with a valid Legend. That is, one that
        // implements setOnVertex() and setOnCentroid() methods.
        if (!(legend instanceof ProportionalPoint ||
                legend instanceof UniqueSymbolPoint ||
                legend instanceof CategorizedPoint ||
                legend instanceof RecodedPoint)) {
            throw new IllegalArgumentException(
                    "On vertex/On centroid config panel is not available " +
                            "for legend" + legend.getLegendTypeName() + ".");
        }

        // Create the buttons and add the listeners.
        JRadioButton bVertex = new JRadioButton(I18N.tr(VERTEX));
        bVertex.addActionListener(
                EventHandler.create(ActionListener.class, legend, "setOnVertex"));
        bVertex.addActionListener(
                EventHandler.create(ActionListener.class, panel, "onClickVertex"));
        JRadioButton bCentroid = new JRadioButton(I18N.tr(CENTROID));
        bCentroid.addActionListener(
                EventHandler.create(ActionListener.class, legend, "setOnCentroid"));
        bCentroid.addActionListener(
                EventHandler.create(ActionListener.class, panel, "onClickCentroid"));

        // Select the right button.
        Symbolizer symbol = legend.getSymbolizer();
        if (symbol instanceof PointSymbolizer) {
            boolean onVertex = ((PointSymbolizer) symbol).isOnVertex();
            bVertex.setSelected(onVertex);
            bCentroid.setSelected(!onVertex);
        }

        // Make the button group.
        ButtonGroup bg = new ButtonGroup();
        bg.add(bVertex);
        bg.add(bCentroid);

        // Make and return the panel.
        JPanel jp = new JPanel(new MigLayout("wrap 1"));
        jp.add(bVertex);
        jp.add(bCentroid);
        return jp;
    }
}
