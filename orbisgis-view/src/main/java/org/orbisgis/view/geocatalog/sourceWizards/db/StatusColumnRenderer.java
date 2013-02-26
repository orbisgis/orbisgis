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
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Cell renderer designed for {@code DataBaseTableModel}.
 * @author Alexis Guéganno
 * @author Erwan Bocher
 */
public class StatusColumnRenderer extends DefaultTableCellRenderer {
        private static final Icon WAITING_ICON = OrbisGISIcon.getIcon("help");
        private static final Icon OK_ICON = OrbisGISIcon.getIcon("emoticon_smile");
        private static final Icon ERROR_ICON = OrbisGISIcon.getIcon("emoticon_unhappy");
       
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                //As defined in DataBaseTableModel#COLUMN_NAMES.
                if (value instanceof DataBaseRow.ExportStatus) {
                        switch ((DataBaseRow.ExportStatus) value) {
                                case OK:
                                        return new JLabel(OK_ICON);
                                case ERROR:
                                        return new JLabel(ERROR_ICON);
                                 case UNKNOWN:
                                        return new JLabel(WAITING_ICON);
                                default:
                                        return component;
                                
                        }
                } else {
                        Color clr = new Color(255, 255, 255);
                        component.setBackground(clr);
                        component.setForeground(new Color(0, 0, 0));
                }
                return component;

        }        
        
}
