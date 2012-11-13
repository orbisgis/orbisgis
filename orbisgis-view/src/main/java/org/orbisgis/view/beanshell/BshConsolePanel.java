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
package org.orbisgis.view.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.buildpath.DirLibraryInfo;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.components.findReplace.FindReplaceDialog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * BeanShell console GUI
 * 
 */
public class BshConsolePanel extends JPanel {
        private static final I18n I18N = I18nFactory.getI18n(BshConsolePanel.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+BshConsolePanel.class);
        private static final int MESSAGE_CLEAR_INTERVAL = 10;
        private static final String MESSAGEBASE = "%d | %d | %s";
        private final BeanShellLog infoLogger = new BeanShellLog(LOGGER,Level.INFO);
        private final BeanShellLog errorLogger = new BeanShellLog(LOGGER,Level.ERROR);
        
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private JLabel statusMessage=new JLabel();
        private FindReplaceDialog findReplaceDialog;
        private Interpreter interpreter = new Interpreter();
        private ActionCommands actions = new ActionCommands();
        private JavaLanguageSupport jls;
        private Timer messageClearTimer;
        private int line = 0;
        private int character = 0;
        private String currentStatusMessage = "";
        
        /**
         * Creates a console for sql.
         */
        public BshConsolePanel() {
                try {
                        interpreter.setOut(new PrintStream(infoLogger));
                        interpreter.setErr(new PrintStream(errorLogger));
                        DataManager dm = Services.getService(DataManager.class);
                        interpreter.setClassLoader(dm.getDataSourceFactory().getClass().getClassLoader());
                        interpreter.set("dsf", dm.getDataSourceFactory());
                        interpreter.eval("setAccessibility(true)");
                        interpreter.getNameSpace().importCommands(
                                "org.orbisgis.view.beanshell.commands");

                } catch (EvalError e) {
                        LOGGER.error(I18N.tr("Cannot initialize beanshell"),e);
                }
                setLayout(new BorderLayout());
                add(getCenterPanel(), BorderLayout.CENTER);
                add(statusMessage,BorderLayout.SOUTH);
        }
        /**
         * Clear the message shown
         */
        public void onClearMessage() {
                setStatusMessage("");
        }
        /**
         * @return ToolBar to command this editor
         */
        public JToolBar getButtonToolBar() {
                return actions.getEditorToolBar(true);
        }
        /**
         * Create actions instances
         * 
         * Each action is put in the Popup menu and the tool bar
         * Their shortcuts is registered also in the editor
         */
        private void initActions() {
                actions.addAction(new DefaultAction(I18N.tr("Execute"),
                        I18N.tr("Execute the java script"),
                        OrbisGISIcon.getIcon("execute"),
                        EventHandler.create(ActionListener.class, this, "onExecute"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK)));
        }

        /**
         * Update the row:column label
         */
        public void onScriptPanelCaretUpdate() {
                line = scriptPanel.getCaretLineNumber() + 1;
                character = scriptPanel.getCaretOffsetFromLineStart();
                setStatusMessage(currentStatusMessage);
        }
        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        initActions();
                        LanguageSupportFactory lsf = LanguageSupportFactory.get();
                        jls = (JavaLanguageSupport) lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        try {
                                setCurrentLibraryInfos(jls.getJarManager());
                                
                        } catch (IOException ioe) {
                                throw new RuntimeException(ioe);
                        }
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setLineWrap(true);
                        lsf.register(scriptPanel);
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
                        scriptPanel.addCaretListener(EventHandler.create(CaretListener.class,this,"onScriptPanelCaretUpdate"));
                        scriptPanel.clearParsers();
                        actions.setAccelerators(scriptPanel);
                        actions.feedPopupMenu(scriptPanel.getPopupMenu());
                        centerPanel = new RTextScrollPane(scriptPanel);
                }
                return centerPanel;
        }

        private void setStatusMessage(String message) {
                currentStatusMessage = message;
                if(messageClearTimer==null) {
                        messageClearTimer = new Timer(MESSAGE_CLEAR_INTERVAL,EventHandler.create(ActionListener.class, this, "onClearMessage"));
                        messageClearTimer.setRepeats(false);
                }
                if (!message.isEmpty()) {
                        messageClearTimer.restart();
                }
                statusMessage.setText(String.format(MESSAGEBASE, line, character, message));
        }

        

        public void freeResources() {
                if (jls != null) {
                        jls.uninstall(scriptPanel);
                }
                if(messageClearTimer!=null) {
                        messageClearTimer.stop();
                }
        }
        private void setCurrentLibraryInfos(JarManager jls) throws IOException {
                // current JRE
                jls.addCurrentJreClassFileSource();                
                String cp = System.getProperty("java.class.path");
                String ps = System.getProperty("path.separator");
                String[] paths = cp.split(ps);
                for (int i = 0; i < paths.length; i++) {
                        File p = new File(paths[i]);
                        LibraryInfo l;
                        if (p.getName().toLowerCase().endsWith(".jar")) {
                                // this is a jar file
                                l = new JarLibraryInfo(p);
                                jls.addClassFileSource(l);
                        } else if (p.isDirectory()) {
                                // this is a folder
                                l = new DirLibraryInfo(p);
                                jls.addClassFileSource(l);
                        }
                        // else we just ignore it
                }
        }

        /**
         * Expose the map context in the beanshell interpreter
         * @param mc MapContext instance
         */
        public void setMapContext(MapContext mc) {
                try {
                        interpreter.set("mc", mc);
                } catch(EvalError ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
        }
        /**
         * User click on execute script button
         */
        public void onExecute() {
                try {
                        String text = scriptPanel.getText().trim();
                        if(!text.isEmpty()) {
                                interpreter.eval(text);
                                infoLogger.flush();
                                errorLogger.flush();
                        }
                } catch (IOException e) {
                        LOGGER.error(
                                e.getLocalizedMessage(),
                                e);
                } catch (IllegalArgumentException e) {
                        LOGGER.error(
                                I18N.tr("Cannot execute the script"),
                                e);
                } catch (EvalError e) {
                        LOGGER.error(
                                I18N.tr("The script is not valid"),
                                e);
                }
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
         *
         * @return
         */
        public Interpreter getInterpreter() {
                return interpreter;
        }

        /**
         * Open one instanceof the find replace dialog
         */
        public void openFindReplaceDialog() {
                if (findReplaceDialog == null) {
                        findReplaceDialog = new FindReplaceDialog(getTextComponent(),UIFactory.getMainFrame());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
        
        
}
