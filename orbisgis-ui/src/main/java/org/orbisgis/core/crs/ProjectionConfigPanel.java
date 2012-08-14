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
package org.orbisgis.core.crs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gdms.data.DataSourceFactory;

public final class ProjectionConfigPanel extends JDialog implements ActionListener {

        private ProjectionTable projectionTab;
        private JPanel buttonPanel;
        private JButton yesButton = null;
        private JButton noButton = null;

        public ProjectionConfigPanel(DataSourceFactory dsf, JFrame frame, boolean modal) {
                super(frame, modal);
                getContentPane().add(getProjectionPanel(dsf), BorderLayout.CENTER);
                getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
                setAlwaysOnTop(true);
                setSize(300, 500);
                setLocationRelativeTo(frame);
        }

        public JPanel getButtonPanel() {
                buttonPanel = new JPanel();
                yesButton = new JButton("Ok");
                yesButton.setActionCommand("OK");
                yesButton.addActionListener(this);
                buttonPanel.add(yesButton);
                noButton = new JButton("Cancel");
                noButton.setActionCommand("CANCEL");
                noButton.addActionListener(this);
                buttonPanel.add(noButton);
                return buttonPanel;
        }

        public JPanel getProjectionPanel(DataSourceFactory dsf) {
                if (projectionTab == null) {
                        projectionTab = new ProjectionTable(dsf);
                }
                return projectionTab;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("OK".equals(command)) {
                        //TODO apply projection
                        
                        //Save the CRS history
                        projectionTab.saveCRSHistory();
                } else if ("CANCEL".equals(command)) {
                        setVisible(false);
                }
        }

        public static void main(String[] args) {
                new ProjectionConfigPanel(new DataSourceFactory(), null, true).setVisible(true);

        }
}
