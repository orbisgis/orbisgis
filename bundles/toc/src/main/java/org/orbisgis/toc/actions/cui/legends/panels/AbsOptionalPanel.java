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

import org.orbisgis.legend.LegendStructure;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Root class for unique symbol panels which may be optional, namely Unique
 * Area and Unique Line (Border).
 *
 * @author Adam Gouge
 */
public abstract class AbsOptionalPanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(AbsOptionalPanel.class);

    protected final boolean showCheckBox;
    protected JCheckBox enableCheckBox;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param showCheckBox Draw the Enable checkbox?
     */
    public AbsOptionalPanel(LegendStructure legend,
                            CanvasSE preview,
                            String title,
                            boolean showCheckBox) {
        super(legend, preview, title);
        this.showCheckBox = showCheckBox;
        if (showCheckBox) {
            initEnableCheckBox();
        }
    }

    /**
     * Initialize the "Enable" checkbox.
     */
    private void initEnableCheckBox() {
        enableCheckBox = new JCheckBox(I18N.tr("Enable"));
        enableCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickOptionalCheckBox();
            }
        });
        enableCheckBox.setSelected(true);
    }

    /**
     * Action taken when the optional checkbox is (de)selected.
     */
    protected abstract void onClickOptionalCheckBox();

    /**
     * Enable or disable all fields (used when the checkbox is clicked).
     *
     * @param enable True if the fields are to be enabled.
     */
    protected abstract void setFieldsState(boolean enable);
}
