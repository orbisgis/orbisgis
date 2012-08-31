/*
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
package org.orbisgis.view.map.mapsManager;

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import org.apache.log4j.Logger;

/**
 *
 * @author Nicolas Fortin
 */
public class MapsManager extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = Logger.getLogger(MapsManager.class);
        private JTree tree;
        private JScrollPane scrollPane;

        public MapsManager() {
                super(new BorderLayout());
                tree = new JTree(new String[]{"Kate","Lisa","Cindy"});
                scrollPane = new JScrollPane(tree,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                add(scrollPane,BorderLayout.EAST);
                setBorder(BorderFactory.createEtchedBorder());
        }
        public int getMinimalTreeHeight() {                
                Insets borders = getInsets();
                Insets sBorders = scrollPane.getInsets();
                return tree.getPreferredSize().height+
                        borders.top+
                        borders.bottom+
                        sBorders.top+
                        sBorders.bottom;
        }
}
