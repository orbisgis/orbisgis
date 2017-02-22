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
package org.orbisgis.view.toc.actions.cui.legend.components;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.OnVertexOnInterior;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.panels.TablePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button group for choosing whether the symbol should be placed on the vertices
 * or on the interior point.
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 * @author Erwan Bocher
 */
public final class OnVertexOnInteriorButtonGroup extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(OnVertexOnInteriorButtonGroup.class);

    private static final String VERTEX = I18n.marktr("Vertex");
    private static final String INTERIOR = I18n.marktr("Interior");

    private OnVertexOnInterior legend;
    private CanvasSE preview;
    private TablePanel tablePanel;

    /**
     * Constructor.
     *
     * @param legend     Legend
     * @param preview    Preview
     * @param tablePanel Table panel
     */
    public OnVertexOnInteriorButtonGroup(OnVertexOnInterior legend,
                                         CanvasSE preview,
                                         TablePanel tablePanel) {
        super(new MigLayout("wrap 1"));
        this.legend = legend;
        this.preview = preview;
        this.tablePanel = tablePanel;
        init();
    }

    public OnVertexOnInteriorButtonGroup(OnVertexOnInterior legend,
                                         CanvasSE preview) {
        this(legend, preview, null);
    }

    /**
     * Initializes the panel used to configure if the symbol must be drawn on
     * the vertices or on the interior point.
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

        JRadioButton bInterior = new JRadioButton(I18N.tr(INTERIOR));
        bInterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setOnInterior();
            }
        });
        bInterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickInterior();
            }
        });

        // Select the right button.
        Symbolizer symbol = ((Legend) legend).getSymbolizer();
        if (symbol instanceof PointSymbolizer) {
            boolean onVertex = ((PointSymbolizer) symbol).isOnVertex();
            bVertex.setSelected(onVertex);
            bInterior.setSelected(!onVertex);
        }

        // Make the button group.
        ButtonGroup bg = new ButtonGroup();
        bg.add(bVertex);
        bg.add(bInterior);

        // Make and return the panel.
        add(bVertex);
        add(bInterior);
    }

    /**
     * Called when the user wants to put the points on the vertices of the geometry.
     */
    private void onClickVertex() {
        changeOnVertex(true);
    }

    /**
     * Called when the user wants to put the points on the interior point of the geometry.
     */
    private void onClickInterior() {
        changeOnVertex(false);
    }

    /**
     * Called by listeners to update the fallback symbol preview (and the
     * table preview if necessary).
     *
     * @param onVertex True if the symbol is to be placed on vertices; false
     *                 if on the interior point
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
