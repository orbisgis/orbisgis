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

import org.apache.log4j.Logger;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.*;
import java.beans.EventHandler;

/**
 * A text field which updates the the line dash array. The input must be a
 * space-separated list of integers specifying the lengths of the dashes.
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 */
public class DashArrayField extends JTextField {

    private static final I18n I18N = I18nFactory.getI18n(DashArrayField.class);
    private static final Logger LOGGER = Logger.getLogger(DashArrayField.class);

    /**
     * Constructor
     *
     * @param legend  Legend
     * @param preview Preview
     */
    public DashArrayField(final ConstantColorAndDashesPSLegend legend,
                          CanvasSE preview) {
        super();
        setText(legend.getDashArray());
        setHorizontalAlignment(JFormattedTextField.RIGHT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setDashArray(((JTextField) e.getSource()).getText());
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField jtf = (JTextField) e.getSource();
                String tmp = jtf.getText();
                legend.setDashArray(tmp);
                if (!tmp.equals(legend.getDashArray())) {
                    LOGGER.warn(I18N.tr("Could not validate your input."));
                    jtf.setText(legend.getDashArray());
                }
            }
        });
        addFocusListener(
                EventHandler.create(FocusListener.class, preview, "imageChanged"));
    }
}
