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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * Class to improve the function list rendering.
 * Add icons corresponding to FunctionElement type.
 * @author ebocher
 */
public class FunctionListRenderer implements ListCellRenderer {

    private static final Color SELECTED = Color.lightGray;
    private static final Color DESELECTED = Color.white;
    private static final Color SELECTED_FONT = Color.white;
    private static final Color DESELECTED_FONT = Color.black;

    @Override
    public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        OurJPanel ourJPanel = new OurJPanel();
        ourJPanel.setNodeCosmetic((FunctionElement) value, isSelected, cellHasFocus);
        return ourJPanel;
    }  

    private class OurJPanel extends JPanel {

        private JLabel iconAndLabel;

        public OurJPanel() {
            FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
            fl.setHgap(0);
            setLayout(fl);
            iconAndLabel = new JLabel();
            add(iconAndLabel);
        }

        public void setNodeCosmetic(FunctionElement value, boolean selected,
                boolean hasFocus) {
            int type = value.getFunctionType();

            if (type == FunctionElement.BASIC_FUNCTION) {
                iconAndLabel.setIcon(OrbisGISIcon.BUILT_FUNCTION);
            } else if (type == FunctionElement.CUSTOM_FUNCTION) {
                iconAndLabel.setIcon(OrbisGISIcon.BUILT_QUERY);
            } else {
                iconAndLabel.setIcon(OrbisGISIcon.BUILT_QUERY_ERR);
            }           
            iconAndLabel.setText(value.getFunctionName());
            iconAndLabel.setVisible(true);



            if (selected) {
                this.setBackground(SELECTED);
                iconAndLabel.setForeground(SELECTED_FONT);
            } else {
                this.setBackground(DESELECTED);
                iconAndLabel.setForeground(DESELECTED_FONT);
            }
        }
    }
}
