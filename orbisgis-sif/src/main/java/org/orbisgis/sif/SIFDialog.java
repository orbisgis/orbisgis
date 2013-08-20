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
package org.orbisgis.sif;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SIFDialog extends AbstractOutsideFrame {

        protected JButton btnOk;
        protected JButton btnCancel;
        private SimplePanel simplePanel;
        protected JPanel pnlButtons;

        public SIFDialog(Window owner, boolean okCancel) {
                super(owner);
                init(okCancel);
        }

        private void init(boolean okCancel) {
                this.setLayout(new BorderLayout());

                btnOk = new JButton(I18N.tr("OK"));
                btnOk.setBorderPainted(false);
                btnOk.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                exit(true);
                        }
                });
                getRootPane().setDefaultButton(btnOk);
                btnCancel = new JButton(I18N.tr("Cancel"));
                btnCancel.setBorderPainted(false);
                btnCancel.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                exit(false);
                        }
                });
                pnlButtons = new JPanel();
                pnlButtons.add(btnOk);
                if (okCancel) {
                        pnlButtons.add(btnCancel);
                }
                this.add(pnlButtons, BorderLayout.SOUTH);


                this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        public void setComponent(SimplePanel simplePanel) {
                this.simplePanel = simplePanel;
                this.add(simplePanel, BorderLayout.CENTER);
                this.setIconImage(getSimplePanel().getIconImage());
        }

        @Override
        protected SimplePanel getSimplePanel() {
                return simplePanel;
        }

        @Override
        public void keyTyped(KeyEvent ke) {
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }
}
