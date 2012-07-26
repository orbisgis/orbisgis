/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.ui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.orbisgis.view.components.renderers.ListLaFRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Class to improve the function list rendering. Add icons corresponding to
 * FunctionElement type.
 *
 * @author Erwan Bocher
 */
public class FunctionListRenderer extends ListLaFRenderer {
        private static final long serialVersionUID = 1L;

        public FunctionListRenderer(JList list) {
                super(list);
        }

        
        private static Icon getFunctionIcon(FunctionElement value) {
                int type = value.getFunctionType();
                if (type == FunctionElement.BASIC_FUNCTION) {
                        return OrbisGISIcon.getIcon("builtinfunctionmap");
                } else if (type == FunctionElement.CUSTOM_FUNCTION) {
                        return OrbisGISIcon.getIcon("builtincustomquerymap");
                } else {
                        return OrbisGISIcon.getIcon("builtincustomquerymaperror");
                }
        }
        @Override
        public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component nativeCell = lookAndFeelRenderer.getListCellRendererComponent(jlist, value, index, isSelected, cellHasFocus);
                if(nativeCell instanceof JLabel) {
                        JLabel renderingComp = (JLabel) nativeCell;
                        FunctionElement sqlFunction = (FunctionElement)value;
                        renderingComp.setIcon(getFunctionIcon(sqlFunction));
                        renderingComp.setText(sqlFunction.getFunctionName());
                        renderingComp.setToolTipText(sqlFunction.getToolTip());
                }
                return nativeCell;
        }
}
