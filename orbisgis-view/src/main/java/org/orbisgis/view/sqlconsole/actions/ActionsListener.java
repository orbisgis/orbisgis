/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class ActionsListener implements ActionListener, DocumentListener {

        private SQLConsolePanel consolePanel;
        private ConsoleListener listener;
        private final static Logger LOGGER = Logger.getLogger("gui." + ActionsListener.class);
        protected final static I18n I18N = I18nFactory.getI18n(ActionsListener.class);    
        public ActionsListener(ConsoleListener listener, SQLConsolePanel consolePanel) {
                this.consolePanel = consolePanel;
                this.listener = listener;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                switch (new Integer(e.getActionCommand())) {
                        case ConsoleAction.EXECUTE:
                                listener.execute(consolePanel.getText());
                                break;
                        case ConsoleAction.CLEAR:
                                if (consolePanel.getScriptPanel().getText().trim().length() > 0) {
                                        int answer = JOptionPane.showConfirmDialog(consolePanel,
                                                I18N.tr("Do you want to clear the contents of the console?"),
                                                I18N.tr("Clear script"), JOptionPane.YES_NO_OPTION);
                                        if (answer == JOptionPane.YES_OPTION) {
                                                consolePanel.getScriptPanel().setText("");
                                        }
                                }
                                break;
                        case ConsoleAction.OPEN:
                                try {
                                        String script = listener.open();
                                        if (script != null) {
                                                int answer = JOptionPane.NO_OPTION;
                                                if (consolePanel.getText().trim().length() > 0) {
                                                        answer = JOptionPane.showConfirmDialog(
                                                                consolePanel,
                                                                I18N.tr("Do you want to clear all before loading the file ?"),
                                                                I18N.tr("Open file"),
                                                                JOptionPane.YES_NO_CANCEL_OPTION);
                                                }

                                                if (answer == JOptionPane.YES_OPTION) {
                                                        consolePanel.getScriptPanel().setText("");
                                                }

                                                if (answer != JOptionPane.CANCEL_OPTION) {
                                                        consolePanel.insertString(script);
                                                }
                                        }
                                } catch (BadLocationException e1) {
                                        LOGGER.error(I18N.tr("Cannot add script"), e1);
                                } catch (IOException e1) {
                                        LOGGER.error(I18N.tr("IO error."), e1);
                                }
                                break;
                        case ConsoleAction.SAVE:
                                try {
                                        boolean saved = listener.save(consolePanel.getText());
                                        if (saved) {
                                                consolePanel.setStatusMessage(I18N.tr("The file has been saved."));
                                        } else {
                                                consolePanel.setStatusMessage("");
                                        }
                                } catch (IOException e1) {
                                        LOGGER.error(I18N.tr("IO error."), e1);
                                }

                                break;
                        case ConsoleAction.FIND_REPLACE:
                                if (consolePanel.getText().trim().length() > 0) {                                       
                                        consolePanel.openFindReplaceDialog();
                                }
                                break;

                }
                setButtonsStatus();
        }

        public void setButtonsStatus() {
                //consolePanel.setButtonsStatus();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
                insertUpdate(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
                setButtonsStatus();
                listener.change();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
        }
}
