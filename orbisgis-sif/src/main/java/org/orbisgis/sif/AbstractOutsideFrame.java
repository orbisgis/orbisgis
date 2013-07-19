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

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class AbstractOutsideFrame extends JDialog implements
        KeyListener {

        protected static final I18n I18N = I18nFactory.getI18n(AbstractOutsideFrame.class);
        private boolean accepted = false;

        public AbstractOutsideFrame(Window owner) {
                super(owner);
        }

        @Override
        public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ESCAPE) {
                        // Key pressed is the ESCAPE key. Hide this Dialog.
                        exit(false);
                }
        }

        protected abstract SimplePanel getSimplePanel();

        /**
         * Method to valid the panel
         *
         * @param ok
         */
        void exit(boolean ok) {
                boolean closePanel = !ok || validateInput();
                if (!closePanel) {
                        setVisible(true);
                } else {
                        setVisible(false);
                        dispose();
                }
                accepted = ok && closePanel;
        }

        /**
         * A method to validate the current panel
         *
         * @return
         */
        public boolean validateInput() {
                String err = getSimplePanel().getUIPanel().validateInput();
                if (err != null) {
                        JOptionPane.showMessageDialog(rootPane, err,
                                I18N.tr("Error"), JOptionPane.ERROR_MESSAGE);
                        return false;
                } else {
                        return true;
                }
        }

        public boolean isAccepted() {
                return accepted;
        }

        public void stateChanged(ChangeEvent evt) {
        }

        @Override
        public void keyTyped(KeyEvent ke) {
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }
}
