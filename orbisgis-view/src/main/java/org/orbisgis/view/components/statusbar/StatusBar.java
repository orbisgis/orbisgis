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
package org.orbisgis.view.components.statusbar;

import java.awt.BorderLayout;
import javax.swing.*;

/**
 * Root class for all status bar in OrbisGIS.
 */
public class StatusBar extends JPanel {
        protected JPanel rightToolbar;
        protected JPanel leftToolbar;
        private final int horizontalEmptyBorder;

        public StatusBar(int outerBarBorder, int horizontalEmptyBorder) {
                super(new BorderLayout());
                this.horizontalEmptyBorder = horizontalEmptyBorder;
                rightToolbar = new JPanel();
                rightToolbar.setLayout(
                        new BoxLayout(rightToolbar, BoxLayout.X_AXIS));
                leftToolbar = new JPanel();
                leftToolbar.setLayout(
                        new BoxLayout(leftToolbar, BoxLayout.X_AXIS));
                setBorder(
                        BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(outerBarBorder,
                        outerBarBorder, outerBarBorder, outerBarBorder)));
                add(rightToolbar, BorderLayout.EAST);
                add(leftToolbar, BorderLayout.WEST);
        }
               

        /**
         * Append a component on the right or the left the status bar
         * @param component 
         */
        public void addComponent(JComponent component, int position) {
                if (position == SwingConstants.LEFT) {
                        addComponentOnTheLeftToolBar(component, true);
                } else if (position == SwingConstants.RIGHT) {
                        addComponentOnTheRigthToolBar(component, true);
                }
        }
        /**
         * Append a component on the right status bar
         * @param component 
         * @param addSeparator Add a separator at the left of the component
         */
        public void addComponentOnTheRigthToolBar(JComponent component,boolean addSeparator) {
                if(addSeparator && rightToolbar.getComponentCount()!=0) {
                        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
                        rightToolbar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                        rightToolbar.add(separator);
                        rightToolbar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                }
                rightToolbar.add(component);
        }       
        
        
        /**
         * Append a component on the left status bar
         * @param component 
         * @param addSeparator Add a separator at the left of the component
         */
        public void addComponentOnTheLeftToolBar(JComponent component,boolean addSeparator) {
                if(addSeparator && leftToolbar.getComponentCount()!=0) {
                        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
                        leftToolbar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                        leftToolbar.add(separator);
                        leftToolbar.add(Box.createHorizontalStrut(horizontalEmptyBorder));
                }
                leftToolbar.add(component);
        } 
}
