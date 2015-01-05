/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.mainframe.impl;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.mainframe.api.MainStatusBar;
import org.orbisgis.mainframe.icons.MainFrameIcon;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.StatusBar;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The status bar of the MainFrame.
 */
public class MainFrameStatusBar extends StatusBar implements MainStatusBar {

        private static final I18n I18N = I18nFactory.getI18n(MainFrameStatusBar.class);
        //Layout parameters
        private static final int OUTER_BAR_BORDER = 1;
        private static final int HORIZONTAL_EMPTY_BORDER = 4;
        private static final int STATUS_BAR_HEIGHT = 30;
        private JPanel workspaceBar;

        public MainFrameStatusBar() {
                super(OUTER_BAR_BORDER, HORIZONTAL_EMPTY_BORDER);
                setPreferredSize(new Dimension(-1, STATUS_BAR_HEIGHT));
                setMinimumSize(new Dimension(1, STATUS_BAR_HEIGHT));
        }

        @Override
        public StatusBar getComponent() {
                return this;
        }

        /**
         * Initialize swing components
         */
        public void init(CoreWorkspace coreWorkspace) {
                workspaceBar = new JPanel(new MigLayout("insets 0 0 0 0"));
                JLabel workspacePath = new JLabel(coreWorkspace.getWorkspaceFolder());
                workspaceBar.add(workspacePath);
                JButton btnChangeWorkspace = new CustomButton(MainFrameIcon.getIcon("application_go"));
                btnChangeWorkspace.setToolTipText(I18N.tr("Switch to another workspace"));
                btnChangeWorkspace.addActionListener(EventHandler.create(ActionListener.class,this,"onChangeWorkspace"));
                workspaceBar.add(btnChangeWorkspace);
                addComponent(workspaceBar, SwingConstants.LEFT);
        }

        /**
         * The user click on change workspace button
         */
        public void onChangeWorkspace() {
                // TODO restart wkgui bundle
        }

        /**
         * Method to extend the mainframe status bar with a new component
         * @param component
         * @param orientation 
         */
        protected void addComponent(JComponent component, String orientation){
            workspaceBar.add(component, orientation);
        }
}
