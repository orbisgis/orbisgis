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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.ui;

import bsh.EvalError;
import bsh.Interpreter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.findReplace.FindReplaceDialog;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;

import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshActionsListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshCompletionKeyListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleAction;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleListener;
import org.orbisgis.utils.I18N;

public class BshConsolePanel extends JPanel {

        private JButton btExecute = null;
        private JButton btClear = null;
        private JButton btOpen = null;
        private JButton btSave = null;
        private JButton btFindReplace = null;
        private BshActionsListener actionAndKeyListener;
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private JToolBar toolBar;
        private JLabel statusMessage;
        private Timer timer;
        private FindReplaceDialog findReplaceDialog;
        private Interpreter interpreter = new Interpreter();
        private ByteArrayOutputStream scriptOutput;

        /**
         * Creates a console for sql.
         */
        public BshConsolePanel(BshConsoleListener listener) {

                try {
                        interpreter.set("bshEditor", this);

                        scriptOutput = new ByteArrayOutputStream();

                        PrintStream outStream = new PrintStream(scriptOutput);
                        interpreter.setOut(outStream);

                        DataManager dm = Services.getService(DataManager.class);

                        interpreter.setClassLoader(dm.getDataSourceFactory().getClass().getClassLoader());
                        interpreter.set("dsf", dm.getDataSourceFactory());
                        interpreter.eval("setAccessibility(true)");
                        interpreter.getNameSpace().importCommands(
                                "org.orbisgis.core.ui.plugins.views.beanShellConsole.commands");

                } catch (EvalError e) {
                        ErrorMessages.error(
                                I18N.getString("orbisgis.org.orbisgis.beanshell.CannotInitializeBeanshell"),
                                e);
                }

                actionAndKeyListener = new BshActionsListener(listener, this);

                setLayout(new BorderLayout());
                add(getCenterPanel(), BorderLayout.CENTER);
                if (listener.showControlButtons()) {
                        add(getButtonToolBar(), BorderLayout.NORTH);
                }
                setButtonsStatus();
                add(getStatusToolBar(), BorderLayout.SOUTH);

        }

        // getters
        private JToolBar getButtonToolBar() {
                final JToolBar northPanel = new JToolBar();
                northPanel.add(getBtExecute());
                northPanel.add(getBtClear());
                northPanel.add(getBtOpen());
                northPanel.add(getBtSave());
                northPanel.add(getBtFindReplace());
                setBtExecute();
                setBtClear();
                setBtSave();
                setBtFindReplace();
                northPanel.setFloatable(false);
                northPanel.setBorderPainted(false);
                northPanel.setOpaque(false);
                return northPanel;
        }

        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
                        scriptPanel.getDocument().addDocumentListener(actionAndKeyListener);
                        scriptPanel.setLineWrap(true);
                        scriptPanel.addKeyListener(new BshCompletionKeyListener(this));
                        centerPanel = new RTextScrollPane(scriptPanel);
                }
                return centerPanel;
        }

        private JToolBar getStatusToolBar() {

                if (toolBar == null) {
                        toolBar = new JToolBar();
                        statusMessage = new JLabel();
                        toolBar.add(statusMessage);
                        toolBar.setFloatable(false);

                        timer = new Timer(4000, new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        setStatusMessage("");
                                }
                        });
                        timer.setRepeats(false);
                }

                return toolBar;
        }

        public void setStatusMessage(String message) {
                if (message.isEmpty()) {
                        statusMessage.setText(message);
                        return;
                } else {
                        timer.restart();
                        statusMessage.setText(message);
                }
        }

        private JButton getBtExecute() {
                if (null == btExecute) {
                        btExecute = new BshConsoleButton(BshConsoleAction.EXECUTE,
                                actionAndKeyListener);
                }
                return btExecute;
        }

        private JButton getBtClear() {
                if (null == btClear) {
                        btClear = new BshConsoleButton(BshConsoleAction.CLEAR,
                                actionAndKeyListener);
                }
                return btClear;
        }

        private JButton getBtOpen() {
                if (null == btOpen) {
                        btOpen = new BshConsoleButton(BshConsoleAction.OPEN,
                                actionAndKeyListener);
                }
                return btOpen;
        }

        private JButton getBtSave() {
                if (null == btSave) {
                        btSave = new BshConsoleButton(BshConsoleAction.SAVE,
                                actionAndKeyListener);
                }
                return btSave;
        }

        private JButton getBtFindReplace() {
                if (null == btFindReplace) {
                        btFindReplace = new BshConsoleButton(BshConsoleAction.FIND_REPLACE,
                                actionAndKeyListener);
                }
                return btFindReplace;
        }

        public String getText() {
                return scriptPanel.getText();
        }

        // setters
        private void setBtExecute() {
                if (0 == getText().length()) {
                        getBtExecute().setEnabled(false);
                } else {
                        getBtExecute().setEnabled(true);
                }
        }

        private void setBtClear() {
                if (0 == getText().length()) {
                        getBtClear().setEnabled(false);
                } else {
                        getBtClear().setEnabled(true);
                }
        }

        private void setBtFindReplace() {
                if (0 == getText().length()) {
                        getBtFindReplace().setEnabled(false);
                } else {
                        getBtFindReplace().setEnabled(true);
                }
        }

        private void setBtOpen() {
        }

        private void setBtSave() {
                if (0 == getText().length()) {
                        getBtSave().setEnabled(false);
                } else {
                        getBtSave().setEnabled(true);
                }
        }

        public void setButtonsStatus() {
                setBtExecute();
                setBtClear();
                setBtOpen();
                setBtSave();
                setBtFindReplace();
        }

        public void setText(String text) {
                scriptPanel.setText(text);
                setButtonsStatus();
        }

        public void insertString(String string) throws BadLocationException {
                scriptPanel.getDocument().insertString(
                        scriptPanel.getDocument().getLength(), string, null);
        }

        public RSyntaxTextArea getTextComponent() {
                return scriptPanel;
        }

        /**
         * Returns the beanshell interpreter
         * @return
         */
        public Interpreter getInterpreter() {
                return interpreter;
        }

        /**
         * Retruns the ouptputstream
         * @return
         */
        public ByteArrayOutputStream getScriptOutput() {
                return scriptOutput;
        }


        /**
         * Open one instanceof the find replace dialog
         */
        public void openFindReplaceDialog() {
                if (findReplaceDialog == null) {
                        findReplaceDialog = new FindReplaceDialog(getTextComponent());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
}
