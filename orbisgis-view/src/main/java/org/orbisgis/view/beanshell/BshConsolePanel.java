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
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
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
import org.orbisgis.view.components.DefaultAction;
import org.orbisgis.view.components.findReplace.FindReplaceDialog;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * BeanShell console GUI
 * 
 */
public class BshConsolePanel extends JPanel {
        private static final I18n I18N = I18nFactory.getI18n(BshConsolePanel.class);
        private static final Logger LOGGER = Logger.getLogger(BshConsolePanel.class);
        
        private JButton btExecute = null;
        private JButton btClear = null;
        private JButton btOpen = null;
        private JButton btSave = null;
        private JButton btFindReplace = null;
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private JToolBar toolBar;
        private JLabel statusMessage;
        private Timer timer;
        private FindReplaceDialog findReplaceDialog;
        private Interpreter interpreter = new Interpreter();
        //Actions
        private List<DefaultAction> actions = new ArrayList<DefaultAction>();
        //Keep buttons reference to enable/disable them
        private Map<DefaultAction,ArrayList<AbstractButton>> actionButtons = new HashMap<DefaultAction,ArrayList<AbstractButton>>();
        
        /**
         * Creates a console for sql.
         */
        public BshConsolePanel() {

                try {
                        interpreter.setOut(new PrintStream(new BeanShellLog(LOGGER,Level.INFO),true));
                        interpreter.setErr(new PrintStream(new BeanShellLog(LOGGER,Level.ERROR),true));
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
        }

        /**
         * Register action button, to enable/disable them later
         * @param button A button with a registered action instance of DefaultAction
         */
        private void registerActionButton(AbstractButton button) {
                DefaultAction action = (DefaultAction) button.getAction();
                if(!actionButtons.containsKey(action)) {
                        actionButtons.put(action, new ArrayList<AbstractButton>());
                }
                actionButtons.get(action).add(button);
        }
        /**
         * Create a new toolbar.
         * @return The toolbar of this panel
         */ 
        public JToolBar getButtonToolBar() {
                final JToolBar northPanel = new JToolBar();

                return northPanel;
        }

        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        LanguageSupportFactory lsf = LanguageSupportFactory.get();
                        JavaLanguageSupport jls = (JavaLanguageSupport) lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        try {
                                setCurrentLibraryInfos(jls.getJarManager());
                                
                        } catch (IOException ioe) {
                                throw new RuntimeException(ioe);
                        }
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setLineWrap(true);
                        lsf.register(scriptPanel);
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
                        scriptPanel.clearParsers();
                        centerPanel = new RTextScrollPane(scriptPanel);
                }
                return centerPanel;
        }
        
        private void setCurrentLibraryInfos(JarManager jls) throws IOException {
                // current JRE
                jls.addCurrentJreClassFileSource();
                
                String cp = System.getProperty("java.class.path");
                String ps = System.getProperty("path.separator");
                System.out.println("\"" + cp + "\"");
                
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
         * User click on execute script button
         */
        public void onExecute() {
                try {
                        // Set the current loaded map context
                        MapContext mc = MapElement.fetchMapContext();
                        if (mc != null) {
                                interpreter.set("mc", mc);
                        }
                        interpreter.eval(scriptPanel.getText());
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
