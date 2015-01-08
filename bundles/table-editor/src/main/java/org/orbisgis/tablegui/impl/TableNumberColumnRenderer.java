/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.table;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.Point;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Custom rendering for number columns in the table editor
 * @author Nicolas Fortin
 */
public class TableNumberColumnRenderer extends TableDefaultColumnRenderer {
        private NumberFormat decimalFormat;
        private static final Logger LOGGER = Logger.getLogger(TableNumberColumnRenderer.class);
        
        public TableNumberColumnRenderer(JTable table,Point popupCellAdress) {
                super(table, Number.class,popupCellAdress);
                decimalFormat = NumberFormat.getInstance(Locale.getDefault());
                decimalFormat.setGroupingUsed(false);
                decimalFormat.setMaximumFractionDigits(16);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component lafComp = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                if(lafComp instanceof JLabel && o!=null) {
                        try {
                                JLabel lafTF = (JLabel)lafComp;
                                lafTF.setText(decimalFormat.format(o));
                        } catch( IllegalArgumentException ex) {
                                LOGGER.debug(ex.getLocalizedMessage(),ex);
                                //ignore
                        }
                }                
                return lafComp;
        }
        
}
