/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.tablegui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import org.orbisgis.sif.components.renderers.TableLaFCellRenderer;

/**
 *
 * @author Nicolas Fortin
 */
public class TableDefaultColumnRenderer extends TableLaFCellRenderer {
        private Point popupCellAdress;
        private final static Color NULL_COLOR_FOREGROUND = Color.RED.darker();
        private Color originalForeground;
        private boolean doResetForeground = false;
        private WKTWriter wktWriter = new WKTWriter(3);
        
        public TableDefaultColumnRenderer(JTable table,Class<?> type, Point popupCellAdress) {
                super(table, type);
                this.popupCellAdress = popupCellAdress;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component lafComp = lookAndFeelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(lafComp instanceof JLabel) {
                        JLabel lafTF = (JLabel)lafComp;  
                        // This cell is the one selected on popup
                        if(popupCellAdress.x == column && popupCellAdress.y == row) {
                                lafTF.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                        }
                        // If value is null, shown null and color text
                        
                        if(value == null) {
                                lafTF.setText("null");
                                if(!doResetForeground) {
                                        originalForeground = lafTF.getForeground();
                                }
                                lafTF.setForeground(NULL_COLOR_FOREGROUND);
                                doResetForeground = true;
                        } else {
                                // Restore the original color
                                if(doResetForeground) {
                                        lafTF.setForeground(originalForeground);
                                        doResetForeground = false;
                                }
                                if(value instanceof Geometry) {
                                    lafTF.setText(wktWriter.write((Geometry)value));
                                }
                        }
                }

                return lafComp;
        }
        
}
