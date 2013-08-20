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

import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.SymbolizerLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolLine;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.components.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Line" settings panel.
 *
 * @author Adam Gouge
 */
public class LinePanel extends AbsOptionalPanel {

    private static final I18n I18N = I18nFactory.getI18n(LinePanel.class);

    private ConstantPenStroke penStrokeMemory;

    private final boolean displayUom;

    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private LineWidthSpinner lineWidthSpinner;
    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param showCheckBox Draw the Enable checkbox?
     */
    public LinePanel(IUniqueSymbolLine legend,
                     CanvasSE preview,
                     String title,
                     boolean showCheckBox,
                     boolean displayUom) {
        super(legend, preview, title, showCheckBox);
        this.displayUom = displayUom;
        init();
        addComponents();
    }

    @Override
    protected IUniqueSymbolLine getLegend() {
        return (IUniqueSymbolLine) legend;
    }

    @Override
    protected void init() {
        penStrokeMemory = getLegend().getPenStroke();
        colorLabel = new ColorLabel(penStrokeMemory.getFillLegend(), preview);
        if (displayUom) {
            lineUOMComboBox =
                    new LineUOMComboBox((SymbolizerLegend) legend, preview);
        }
        lineWidthSpinner =
                new LineWidthSpinner(penStrokeMemory, preview);
        lineOpacitySpinner =
                new LineOpacitySpinner(penStrokeMemory.getFillLegend(), preview);
        if (penStrokeMemory instanceof ConstantColorAndDashesPSLegend) {
            dashArrayField =
                    new DashArrayField((ConstantColorAndDashesPSLegend) penStrokeMemory, preview);
        } else {
            throw new IllegalStateException("Legend " +
                    getLegend().getLegendTypeName() + " must have a " +
                    "ConstantColorAndDashesPSLegend penstroke in order to " +
                    "initialize the DashArrayField.");
        }
    }

    @Override
    protected void addComponents() {
        // Enable checkbox (if optional).
        if (showCheckBox) {
            add(enableCheckBox, "align l");
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Line color
        add(colorLabel);
        // Unit of measure - line width
        if (displayUom) {
            add(new JLabel(I18N.tr(LINE_WIDTH_UNIT)));
            add(lineUOMComboBox, COMBO_BOX_CONSTRAINTS);
        }
        // Line width
        add(new JLabel(I18N.tr(WIDTH)));
        add(lineWidthSpinner, "growx");
        // Line opacity
        add(new JLabel(I18N.tr(OPACITY)));
        add(lineOpacitySpinner, "growx");
        // Dash array
        add(new JLabel(I18N.tr(DASH_ARRAY)));
        add(dashArrayField, "growx");
    }

    @Override
    protected void onClickOptionalCheckBox() {
        if (enableCheckBox.isSelected()) {
            getLegend().setPenStroke(penStrokeMemory);
            setFieldsState(true);
        } else {
            // Remember the old configuration.
            penStrokeMemory = getLegend().getPenStroke();
            getLegend().setPenStroke(new NullPenStrokeLegend());
            setFieldsState(false);
        }
        preview.imageChanged();
    }

    @Override
    protected void setFieldsState(boolean enable) {
        ComponentUtil.setFieldState(enable, colorLabel);
        if (displayUom) {
            if (lineUOMComboBox != null) {
                ComponentUtil.setFieldState(enable, lineUOMComboBox);
            }
        }
        ComponentUtil.setFieldState(enable, lineWidthSpinner);
        ComponentUtil.setFieldState(enable, lineOpacitySpinner);
        ComponentUtil.setFieldState(enable, dashArrayField);
    }
}
