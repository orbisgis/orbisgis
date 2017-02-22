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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        private static final Logger LOGGER = LoggerFactory.getLogger(TableNumberColumnRenderer.class);
        
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
