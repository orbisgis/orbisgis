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
package org.orbisgis.view.toc.actions.cui.legend.panels;

import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.components.OnVertexOnInteriorButtonGroup;
import org.orbisgis.view.toc.actions.cui.legend.components.SymbolHeightSpinner;
import org.orbisgis.view.toc.actions.cui.legend.components.SymbolUOMComboBox;
import org.orbisgis.view.toc.actions.cui.legend.components.SymbolWidthSpinner;
import org.orbisgis.view.toc.actions.cui.legend.components.WKNComboBox;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Point" settings panel.
 *
 * @author Adam Gouge
 */
public class PointPanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(PointPanel.class);

    private final boolean displayUOM;
    private final int geometryType;

    private OnVertexOnInteriorButtonGroup onVertexOnInteriorButtonGroup;
    private SymbolUOMComboBox symbolUOMComboBox;
    private WKNComboBox wknComboBox;
    private SymbolWidthSpinner symbolWidthSpinner;
    private SymbolHeightSpinner symbolHeightSpinner;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param displayUOM   Whether the symbol UOM combo box should be displayed
     * @param geometryType The type of geometry linked to this legend
     */
    public PointPanel(UniqueSymbolPoint legend,
                      CanvasSE preview,
                      String title,
                      boolean displayUOM,
                      int geometryType) {
        super(legend, preview, title);
        this.displayUOM = displayUOM;
        this.geometryType = geometryType;
        init();
        addComponents();
    }

    @Override
    protected UniqueSymbolPoint getLegend() {
        return (UniqueSymbolPoint) legend;
    }

    @Override
    protected void init() {
        if (displayUOM) {
            if (geometryType != SimpleGeometryType.POINT) {
                onVertexOnInteriorButtonGroup =
                        new OnVertexOnInteriorButtonGroup(getLegend(), preview);
            }
            symbolUOMComboBox = new SymbolUOMComboBox(getLegend(), preview);
        }
        wknComboBox = new WKNComboBox(getLegend(), preview);
        symbolWidthSpinner = new SymbolWidthSpinner(getLegend(), preview);
        symbolHeightSpinner = new SymbolHeightSpinner(getLegend(), preview);
    }

    @Override
    protected void addComponents() {
        if (displayUOM) {
            // If geometryType != POINT, we must let the user choose if
            // he wants to draw symbols on interior point or on vertices.
            if (geometryType != SimpleGeometryType.POINT) {
                add(new JLabel(I18N.tr(PLACE_SYMBOL_ON)), "span 1 2");
                add(onVertexOnInteriorButtonGroup, "span 1 2");
            }
            // Unit of measure - symbol size
            add(new JLabel(I18N.tr(SYMBOL_SIZE_UNIT)));
            add(symbolUOMComboBox, COMBO_BOX_CONSTRAINTS);
        }
        // Well-known name
        add(new JLabel(I18N.tr(SYMBOL)));
        add(wknComboBox, COMBO_BOX_CONSTRAINTS);
        // Symbol width
        add(new JLabel(I18N.tr(WIDTH)));
        add(symbolWidthSpinner, "growx");
        // Mark height
        add(new JLabel(I18N.tr(HEIGHT)));
        add(symbolHeightSpinner, "growx");
    }
}
