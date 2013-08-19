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
package org.orbisgis.view.toc.actions.cui.legends.components;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.OnVertexOnCentroid;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.panels.TablePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button group for choosing whether the symbol should be placed on the vertices
 * or on the centroid.
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 */
public final class OnVertexOnCentroidButtonGroup extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(OnVertexOnCentroidButtonGroup.class);

    private static final String VERTEX = I18n.marktr("Vertex");
    private static final String CENTROID = I18n.marktr("Centroid");

    private OnVertexOnCentroid legend;
    private CanvasSE preview;
    private TablePanel tablePanel;

    /**
     * Constructor.
     *
     * @param legend     Legend
     * @param preview    Preview
     * @param tablePanel Table panel
     */
    public OnVertexOnCentroidButtonGroup(OnVertexOnCentroid legend,
                                         CanvasSE preview,
                                         TablePanel tablePanel) {
        super(new MigLayout("wrap 1"));
        this.legend = legend;
        this.preview = preview;
        this.tablePanel = tablePanel;
        init();
    }

    public OnVertexOnCentroidButtonGroup(OnVertexOnCentroid legend,
                                         CanvasSE preview) {
        this(legend, preview, null);
    }

    /**
     * Initializes the panel used to configure if the symbol must be drawn on
     * the vertices or on the centroid.
     */
    private void init() {

        // Create the buttons and add the listeners.
        JRadioButton bVertex = new JRadioButton(I18N.tr(VERTEX));
        bVertex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setOnVertex();
            }
        });
        bVertex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickVertex();
            }
        });

        JRadioButton bCentroid = new JRadioButton(I18N.tr(CENTROID));
        bCentroid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setOnCentroid();
            }
        });
        bCentroid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickCentroid();
            }
        });

        // Select the right button.
        Symbolizer symbol = ((Legend) legend).getSymbolizer();
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
        add(bVertex);
        add(bCentroid);
    }

    /**
     * Called when the user wants to put the points on the vertices of the geometry.
     */
    private void onClickVertex() {
        changeOnVertex(true);
    }

    /**
     * Called when the user wants to put the points on the centroid of the geometry.
     */
    private void onClickCentroid() {
        changeOnVertex(false);
    }

    /**
     * Called by listeners to update the fallback symbol preview (and the
     * table preview if necessary).
     *
     * @param onVertex True if the symbol is to be placed on vertices; false
     *                 if on the centroid
     */
    private void changeOnVertex(boolean onVertex) {
        Symbolizer symbol = preview.getSymbol();
        if (symbol instanceof PointSymbolizer) {
            ((PointSymbolizer) symbol).setOnVertex(onVertex);
            preview.imageChanged();
            if (tablePanel != null) {
                tablePanel.updateTable();
            }
        }
    }
}
