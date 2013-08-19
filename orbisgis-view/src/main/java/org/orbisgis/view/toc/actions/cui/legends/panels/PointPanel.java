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

import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.components.*;
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

    private OnVertexOnCentroidButtonGroup onVertexOnCentroidButtonGroup;
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
                onVertexOnCentroidButtonGroup =
                        new OnVertexOnCentroidButtonGroup(getLegend(), preview);
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
            // he wants to draw symbols on centroid or on vertices.
            if (geometryType != SimpleGeometryType.POINT) {
                add(new JLabel(I18N.tr(PLACE_SYMBOL_ON)), "span 1 2");
                add(onVertexOnCentroidButtonGroup, "span 1 2");
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
