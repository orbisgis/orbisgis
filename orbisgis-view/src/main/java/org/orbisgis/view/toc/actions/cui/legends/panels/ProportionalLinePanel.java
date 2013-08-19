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
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.components.*;
import org.orbisgis.view.toc.actions.cui.legends.ui.PnlUniqueLineSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Proportional Line" settings panel.
 *
 * @author Adam Gouge
 */
public class ProportionalLinePanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(ProportionalLinePanel.class);

    private DataSource dataSource;

    private PLineFieldsComboBox pLineFieldsComboBox;
    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private MaxSizeSpinner maxSizeSpinner;
    private MinSizeSpinner minSizeSpinner;

    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    /**
     * Constructor
     *
     * @param legend     Legend
     * @param preview    Preview
     * @param dataSource DataSource
     */
    public ProportionalLinePanel(ProportionalLine legend,
                                 CanvasSE preview,
                                 DataSource dataSource) {
        super(legend, preview, I18N.tr(PnlUniqueLineSE.LINE_SETTINGS));
        this.dataSource = dataSource;
        init();
        addComponents();
    }

    @Override
    protected ProportionalLine getLegend() {
        return (ProportionalLine) legend;
    }

    @Override
    protected void init() {
        ProportionalStrokeLegend strokeLegend = getLegend().getStrokeLegend();
        ConstantSolidFill fillAnalysis = (ConstantSolidFill) strokeLegend.getFillAnalysis();

        pLineFieldsComboBox = new PLineFieldsComboBox(dataSource, getLegend(), preview);
        colorLabel = new ColorLabel(fillAnalysis, preview);
        lineUOMComboBox = new LineUOMComboBox(getLegend(), preview);
        try {
            maxSizeSpinner = new MaxSizeSpinner(getLegend(), preview);
            minSizeSpinner = new MinSizeSpinner(getLegend(), maxSizeSpinner);
            maxSizeSpinner.setMinSizeSpinner(minSizeSpinner);
        } catch (ParameterException e) {
            e.printStackTrace();
        }
        lineOpacitySpinner = new LineOpacitySpinner(fillAnalysis, preview);
        dashArrayField = new DashArrayField(strokeLegend, preview);
    }

    @Override
    protected void addComponents() {
        // Field
        add(new JLabel(I18N.tr(NUMERIC_FIELD)));
        add(pLineFieldsComboBox, COMBO_BOX_CONSTRAINTS);
        // Color
        add(new JLabel(I18N.tr("Color")));
        add(colorLabel);
        // Unit of Measure - line width
        add(new JLabel(I18N.tr(LINE_WIDTH_UNIT)));
        add(lineUOMComboBox, COMBO_BOX_CONSTRAINTS);
        // Max width
        add(new JLabel(I18N.tr("Max width")));
        add(maxSizeSpinner, "growx");
        // Min width
        add(new JLabel(I18N.tr("Min width")));
        add(minSizeSpinner, "growx");
        // Opacity
        add(new JLabel(I18N.tr(OPACITY)));
        add(lineOpacitySpinner, "growx");
        // Dash array
        add(new JLabel(I18N.tr(DASH_ARRAY)));
        add(dashArrayField, "growx");
    }
}
