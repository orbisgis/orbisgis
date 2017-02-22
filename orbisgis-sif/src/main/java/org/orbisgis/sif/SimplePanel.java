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
