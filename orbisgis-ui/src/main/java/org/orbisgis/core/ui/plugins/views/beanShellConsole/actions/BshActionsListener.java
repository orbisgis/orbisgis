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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.beanShellConsole.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.ui.BshConsolePanel;
import org.orbisgis.utils.I18N;

public class BshActionsListener implements ActionListener, DocumentListener {

        private BshConsolePanel consolePanel;
        private BshConsoleListener listener;

        public BshActionsListener(BshConsoleListener listener, BshConsolePanel consolePanel) {
                this.consolePanel = consolePanel;
                this.listener = listener;
        }

        public void actionPerformed(ActionEvent e) {
                switch (new Integer(e.getActionCommand())) {
                        case BshConsoleAction.EXECUTE:
                                listener.execute(consolePanel.getText());
                                break;
                        case BshConsoleAction.CLEAR:
                                if (consolePanel.getText().trim().length() > 0) {
                                        int answer = JOptionPane.showConfirmDialog(consolePanel,
                                                I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.clearTheConsole"), //$NON-NLS-1$
                                                I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.clearScript"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
                                        if (answer == JOptionPane.YES_OPTION) {
                                                consolePanel.setText(""); //$NON-NLS-1$
                                        }
                                }
                                break;
                        case BshConsoleAction.OPEN:
                                try {
                                        String script = listener.open();
                                        if (script != null) {
                                                int answer = JOptionPane.NO_OPTION;
                                                if (consolePanel.getText().trim().length() > 0) {
                                                        answer = JOptionPane.showConfirmDialog(
                                                                consolePanel,
                                                                I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.clearAllBeforeOpenFile"), //$NON-NLS-1$
                                                                I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.openFile"), //$NON-NLS-1$
                                                                JOptionPane.YES_NO_CANCEL_OPTION);
                                                }

                                                if (answer == JOptionPane.YES_OPTION) {
                                                        consolePanel.setText(""); //$NON-NLS-1$
                                                }

                                                if (answer != JOptionPane.CANCEL_OPTION) {
                                                        consolePanel.insertString(script);
                                                }
                                        }
                                } catch (BadLocationException e1) {
                                        Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.cannotAddScript"), e1); //$NON-NLS-1$
                                } catch (IOException e1) {
                                        Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.ioError"), e1); //$NON-NLS-1$
                                }
                                break;
                        case BshConsoleAction.SAVE:
                                try {
                                        boolean saved = listener.save(consolePanel.getText());
                                        if (saved) {
                                                consolePanel.setStatusMessage(I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.fileSaved"));
                                        } else {
                                                consolePanel.setStatusMessage("");
                                        }
                                } catch (IOException e1) {
                                        Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.ui.bshActionsListener.ioError"), e1); //$NON-NLS-1$
                                }

                                break;

                        case BshConsoleAction.FIND_REPLACE:
                                if (consolePanel.getText().trim().length() > 0) {
                                        consolePanel.openFindReplaceDialog();
                                }
                                break;
                }

                setButtonsStatus();
        }

        public void setButtonsStatus() {
                consolePanel.setButtonsStatus();
        }

        public void changedUpdate(DocumentEvent e) {
                insertUpdate(e);
        }

        public void insertUpdate(DocumentEvent e) {
                setButtonsStatus();
                listener.change();
        }

        public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
        }
}
