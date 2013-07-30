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

import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.view.toc.actions.cui.legends.PnlAbstractTableAnalysis;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class EnableStrokeCheckBox extends JCheckBox {

    private static final I18n I18N = I18nFactory.getI18n(EnableStrokeCheckBox.class);

    private EnablesStroke legend;
    private LineUOMComboBox lineUOMComboBox;

    public EnableStrokeCheckBox(EnablesStroke legend,
                                LineUOMComboBox lineUOMComboBox) {
        super(I18N.tr(PnlAbstractTableAnalysis.ENABLE_BORDER));
        this.legend = legend;
        this.lineUOMComboBox = lineUOMComboBox;
        init();
    }

    /**
     * Gets the checkbox used to set if the border will be drawn.
     *
     * @return The enable border checkbox.
     */
    private void init() {
        setSelected(legend.isStrokeEnabled());
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setStrokeEnabled(((JCheckBox) e.getSource()).isSelected());
                lineUOMComboBox.updatePreview();
            }
        });
    }
}
