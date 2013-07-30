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

import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 30/07/13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class AreaPanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(AreaPanel.class);

    private ConstantSolidFill fillLegendMemory;

    private ColorLabel colorLabel;
    private LineOpacitySpinner fillOpacitySpinner;

    public AreaPanel(IUniqueSymbolArea legend,
                     CanvasSE preview,
                     String title,
                     boolean isAreaOptional) {
        super(legend, preview, title, isAreaOptional);
        init();
        addComponents();
    }

    @Override
    protected IUniqueSymbolArea getLegend() {
        return (IUniqueSymbolArea) legend;
    }

    @Override
    protected void init() {
        fillLegendMemory = getLegend().getFillLegend();
        colorLabel = new ColorLabel(fillLegendMemory, preview);
        fillOpacitySpinner = new LineOpacitySpinner(fillLegendMemory, preview);
    }

    @Override
    public void addComponents() {
        if (isOptional) {
            add(enableCheckBox, "align l");
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Color
        add(colorLabel);
        // Opacity
        add(new JLabel(I18N.tr(AbstractFieldPanel.OPACITY)));
        add(fillOpacitySpinner, "growx");
    }

    @Override
    protected void onClickOptionalCheckBox() {
        if (enableCheckBox.isSelected()) {
            getLegend().setFillLegend(fillLegendMemory);
            setFieldsState(true);
        } else {
            // Remember the old configuration.
            fillLegendMemory = getLegend().getFillLegend();
            getLegend().setFillLegend(new NullSolidFillLegend());
            setFieldsState(false);
        }
        preview.imageChanged();
    }

    @Override
    protected void setFieldsState(boolean state) {
        ComponentUtil.setFieldState(state, colorLabel);
        ComponentUtil.setFieldState(state, fillOpacitySpinner);
    }
}
