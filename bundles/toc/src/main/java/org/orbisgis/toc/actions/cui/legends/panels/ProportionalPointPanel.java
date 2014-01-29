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

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.components.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Proportional Point" settings panel.
 *
 * @author Adam Gouge
 */
public class ProportionalPointPanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(ProportionalPointPanel.class);

    private DataSource dataSource;
    private int geometryType;

    private PPointFieldsComboBox pPointFieldsComboBox;
    private SymbolUOMComboBox symbolUOMComboBox;
    private WKNComboBox wknComboBox;
    private MaxSizeSpinner maxSizeSpinner;
    private MinSizeSpinner minSizeSpinner;

    private OnVertexOnCentroidButtonGroup onVertexOnCentroidButtonGroup;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param dataSource   DataSource
     * @param geometryType The type of geometry linked to this legend
     */
    public ProportionalPointPanel(ProportionalPoint legend,
                                  CanvasSE preview,
                                  String title,
                                  DataSource dataSource,
                                  int geometryType) {
        super(legend, preview, title);
        this.dataSource = dataSource;
        this.geometryType = geometryType;
        init();
        addComponents();
    }

    @Override
    public ProportionalPoint getLegend() {
        return (ProportionalPoint) legend;
    }

    @Override
    protected void init() {
        pPointFieldsComboBox = new PPointFieldsComboBox(dataSource, getLegend(), preview);
        symbolUOMComboBox = new SymbolUOMComboBox(getLegend(), preview);
        wknComboBox = new WKNComboBox(getLegend(), preview);
        try {
            maxSizeSpinner = new MaxSizeSpinner(getLegend(), preview);
            minSizeSpinner = new MinSizeSpinner(getLegend(), maxSizeSpinner);
            maxSizeSpinner.setMinSizeSpinner(minSizeSpinner);
        } catch (ParameterException e) {
            e.printStackTrace();
        }
        if (geometryType != SimpleGeometryType.POINT) {
            onVertexOnCentroidButtonGroup =
                    new OnVertexOnCentroidButtonGroup(getLegend(), preview);
        }
    }

    @Override
    protected void addComponents() {
        // Field
        add(new JLabel(I18N.tr(NUMERIC_FIELD)));
        add(pPointFieldsComboBox, COMBO_BOX_CONSTRAINTS);
        // Unit of measure - symbol size
        add(new JLabel(I18N.tr(SYMBOL_SIZE_UNIT)));
        add(symbolUOMComboBox, COMBO_BOX_CONSTRAINTS);
        // Symbol
        add(new JLabel(I18N.tr(SYMBOL)));
        add(wknComboBox, COMBO_BOX_CONSTRAINTS);
        // Max size
        add(new JLabel(I18N.tr("Max. size")));
        add(maxSizeSpinner, "growx");
        // Min size
        add(new JLabel(I18N.tr("Min. size")));
        add(minSizeSpinner, "growx");
        // If geometryType != POINT, we must let the user choose if he
        // wants to draw symbols on centroid or on vertices.
        if (geometryType != SimpleGeometryType.POINT) {
            add(new JLabel(I18N.tr(PLACE_SYMBOL_ON)), "span 1 2");
            add(onVertexOnCentroidButtonGroup, "span 1 2");
        }
    }
}
