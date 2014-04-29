/**
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
package org.orbisgis.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class SimplePanel extends JPanel {

        private UIPanel uiPanel;
        private AbstractOutsideFrame outsideFrame;
        private Component firstFocus;

        /**
         * This is the default constructor
         */
        public SimplePanel(AbstractOutsideFrame frame, UIPanel panel) {
                this.uiPanel = panel;
                this.outsideFrame = frame;
                initialize(panel);
        }

        /**
         * This method initializes this
         *
         * @return void
         */
        private void initialize(UIPanel panel) {
                Component comp = panel.getComponent();
                fillFirstComponent(comp);
                this.setLayout(new BorderLayout());
                this.add(comp, BorderLayout.CENTER);
        }

        private boolean fillFirstComponent(Component comp) {
                if (comp instanceof Container) {
                        Container cont = (Container) comp;
                        for (int i = 0; i < cont.getComponentCount(); i++) {
                                if (fillFirstComponent(cont.getComponent(i))) {
                                        return true;
                                }
                        }

                        return false;
                } else {
                        firstFocus = comp;
                        this.addComponentListener(new ComponentAdapter() {

                                @Override
                                public void componentShown(ComponentEvent e) {
                                        firstFocus.requestFocus();
                                }
                        });

                        return true;
                }
        }

        public ImageIcon getIcon() {
                URL iconURL = uiPanel.getIconURL();
                if (iconURL == null) {
                        iconURL = UIFactory.getDefaultIcon();
                }

                if (iconURL != null) {
                        return new ImageIcon(iconURL);
                } else {
                        return UIFactory.getDefaultImageIcon();
                }
        }

        public Image getIconImage() {
                ImageIcon ii = getIcon();
                if (ii == null) {
                        return null;
                } else {
                        return ii.getImage();
                }
        }

        public UIPanel getUIPanel() {
                return uiPanel;
        }

        public AbstractOutsideFrame getOutsideFrame() {
                return outsideFrame;
        }
}
