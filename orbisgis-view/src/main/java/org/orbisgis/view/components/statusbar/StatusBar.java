 /*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.view.components.statusbar;

import java.awt.BorderLayout;
import javax.swing.*;

/**
 * Root class for all status bar in OrbisGIS
 */
public class StatusBar extends JPanel {
        protected JPanel horizontalBar;
        private final int outerBarBorder;
        private final int horizontalEmptyBorder;

        public StatusBar(int outerBarBorder, int horizontalEmptyBorder) {
                super(new BorderLayout());
                this.outerBarBorder = outerBarBorder;
                this.horizontalEmptyBorder = horizontalEmptyBorder;                horizontalBar = new JPanel();
                horizontalBar.setLayout(new BoxLayout(horizontalBar, BoxLayout.X_AXIS));
                setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),BorderFactory.createEmptyBorder(outerBarBorder, outerBarBorder, outerBarBorder, outerBarBorder)));
                add(horizontalBar,BorderLayout.EAST);    
        }
               

        /**
         * Append a component on the right of the status bar
         * @param component 
         */
        public void addComponent(JComponent component) {
                addComponent(component,true);
        }
        /**
         * Append a component on the right of the status bar
         * @param component 
         * @param addSeparator Add a separator at the left of the component
         */
        public void addComponent(JComponent component,boolean addSeparator) {
                if(addSeparator && horizontalBar.getComponentCount()!=0) {
                        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
                        horizontalBar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                        horizontalBar.add(separator);
                        horizontalBar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                }
                horizontalBar.add(component);
        }        
}
