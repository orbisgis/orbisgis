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
import java.io.*;
import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import org.apache.commons.io.FileUtils;
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
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.view.beanshell.ext.BeanShellAction;
import org.orbisgis.view.components.Log4JOutputStream;
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
public final class BshConsolePanel extends JPanel {
        private static final I18n I18N = I18nFactory.getI18n(BshConsolePanel.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+BshConsolePanel.class);
        private static final int MESSAGE_CLEAR_INTERVAL = 10000; //ms Clear message interval
        private static final String MESSAGEBASE = "%d | %d | %s";
        private final Log4JOutputStream infoLogger = new Log4JOutputStream(LOGGER,Level.INFO);
        private final Log4JOutputStream errorLogger = new Log4JOutputStream(LOGGER,Level.ERROR);
        
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
        private static final String BSHINITFILE = "init.bsh";
        private DefaultAction executeAction;
        private DefaultAction clearAction;
        private DefaultAction saveAction;
        private DefaultAction findAction;
        
        /**
         * Creates a console for beanshell.
         * The interpreter is able to use scripts available in bsh folder
         * located in the application folder.
         */
        public BshConsolePanel() {
                try {
                        interpreter.setOut(new PrintStream(infoLogger));
                        interpreter.setErr(new PrintStream(errorLogger));
                        DataManager dm = Services.getService(DataManager.class);
                        interpreter.setClassLoader(dm.getDataSourceFactory().getClass().getClassLoader());
                        interpreter.set("dsf", dm.getDataSourceFactory());
                        interpreter.eval("setAccessibility(true)");
                        new LoadBSHScripts().execute();

                } catch (EvalError e) {
                        LOGGER.error(I18N.tr("Cannot initialize beanshell"), e);
                }
                setLayout(new BorderLayout());
                add(getCenterPanel(), BorderLayout.CENTER);
                add(statusMessage, BorderLayout.SOUTH);
        }        
        

        /**
         * This class is used to load all bsh scripts and register them without
         * blocking the OrbisGIS UI.The bsh scripts are stored in the resources
         * folder. The scripts are overrided after each run.
         */
        private class LoadBSHScripts extends SwingWorker<Object, Object> {
            @Override
            public String toString() {
                return "BshConsolePanel#LoadBSHScripts";
            }

            @Override
                protected Object doInBackground() {
                        CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                        try {
                                //Load all bsh files from the ressource system folder
                                //and register them
                                StringBuilder sb = new StringBuilder("importCommands(");
                                sb.append("\"/org/orbisgis/view/beanshell/system\"");
                                sb.append(")");
                                interpreter.eval(sb.toString());
                                //The user can specify an init file in its workspace folder
                                String userBSHFolder = ws.getWorkspaceFolder() + File.separator + BSHINITFILE;
                                //Set the workspace folder. It can be used in scripts to avoid scope variable.
                                interpreter.set("wsPath", ws.getWorkspaceFolder());
                                // Check if the init file exists and run it
                                // A user_demo.bsh is delivered with OrbisGIS sources for a demontration
                                File bshInitFile = new File(userBSHFolder);
                                if (bshInitFile.exists()) {
                                        interpreter.source(bshInitFile.getAbsolutePath());
                                } else {
                                        copyScripts(ws.getWorkspaceFolder(), "init.bsh");
                                        copyScripts(ws.getWorkspaceFolder(), "layerCount.bsh");
                                        interpreter.source(bshInitFile.getAbsolutePath());
                                }

                        } catch (Exception e) {
                                LOGGER.error(I18N.tr("Cannot initialize beanshell script folder"), e);
                        }
                        return null;
                }
        }


        /**
         * Copy the script file from the resource folder to the default OrbiGIS
         * beanshell folder
         * @param bshFolder
         * @param scriptName
         * @throws IOException 
         */
        public void copyScripts( String bshFolder, String scriptName) throws IOException {
                InputStream bshStream = BshConsolePanel.class.getResourceAsStream("/org/orbisgis/view/beanshell/user/"+scriptName);
                File script = new File(bshFolder + File.separator + scriptName);
                FileUtils.copyInputStreamToFile(bshStream, script);
        }

        /**
         * Clear the message shown
         */
        public void onClearMessage() {
                setStatusMessage("");
        }
        
        /**
         * Clear the content of the console
         */
        public void onClear() {
                if(scriptPanel.getDocument().getLength()!=0) {
                        int answer = JOptionPane.showConfirmDialog(this,
                                I18N.tr("Do you want to clear the contents of the console?"),
                                I18N.tr("Clear script"), JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                                scriptPanel.setText("");
                        }
                }
        }

        /**
         * Get the action manager.
         * @return ActionCommands instance.
         */
        public ActionCommands getActions() {
            return actions;
        }

        /**
         * Create actions instances
         * 
         * Each action is put in the Popup menu and the tool bar
         * Their shortcuts are registered also in the editor
         */
        private void initActions() {
                //Execute action
                executeAction = new DefaultAction(BeanShellAction.A_EXECUTE, I18N.tr("Execute"),
                        I18N.tr("Execute the java script"),
                        OrbisGISIcon.getIcon("execute"),
                        EventHandler.create(ActionListener.class, this, "onExecute"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
                actions.addAction(executeAction);

                //Clear action
                clearAction = new DefaultAction(BeanShellAction.A_CLEAR,
                        I18N.tr("Clear"),
                        I18N.tr("Erase the content of the editor"),
                        OrbisGISIcon.getIcon("erase"),
                        EventHandler.create(ActionListener.class, this, "onClear"),
                        null);
                actions.addAction(clearAction);

                //Open action
                actions.addAction(new DefaultAction(BeanShellAction.A_OPEN,
                        I18N.tr("Open"),
                        I18N.tr("Load a file in this editor"),
                        OrbisGISIcon.getIcon("open"),
                        EventHandler.create(ActionListener.class, this, "onOpenFile"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)));
                //Save
                saveAction = new DefaultAction(BeanShellAction.A_SAVE,
                        I18N.tr("Save"),
                        I18N.tr("Save the editor content into a file"),
                        OrbisGISIcon.getIcon("save"),
                        EventHandler.create(ActionListener.class, this, "onSaveFile"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
                actions.addAction(saveAction);
                //Find action
                findAction = new DefaultAction(BeanShellAction.A_SEARCH,
                        I18N.tr("Search.."),
                        I18N.tr("Search text in the document"),
                        OrbisGISIcon.getIcon("find"),
                        EventHandler.create(ActionListener.class, this, "openFindReplaceDialog"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)).addStroke(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
                actions.addAction(findAction);
                                
        }

        /**
         * Open a dialog that let the user select a file and save the content
         * of the sql editor into this file.
         */
        public void onSaveFile() {
                final SaveFilePanel outfilePanel = new SaveFilePanel(
                        "bshConsoleOutFile", I18N.tr("Save script"));
                outfilePanel.addFilter("bsh", I18N.tr("BeanShell Script (*.bsh)"));
                outfilePanel.setConfirmOverwrite(true);
                outfilePanel.loadState();
                if (UIFactory.showDialog(outfilePanel)) {
                        try {
                        FileUtils.write(outfilePanel.getSelectedFile(), scriptPanel.getText());
                        } catch (IOException e1) {
                                LOGGER.error(I18N.tr("IO error."), e1);
                                return;
                        }
                        setStatusMessage(I18N.tr("The file has been saved."));
                } else {
                        setStatusMessage("");
                }
        }
        /**
         * Open a dialog that let the user select a file
         * and add or replace the content of the sql editor.
         */
        public void onOpenFile() {
                final OpenFilePanel inFilePanel = new OpenFilePanel("bshConsoleInFile",
                        I18N.tr("Open script"));
                inFilePanel.addFilter("bsh", I18N.tr("BeanShell Script (*.bsh)"));
                inFilePanel.loadState();
                if (UIFactory.showDialog(inFilePanel)) {
                        int answer = JOptionPane.NO_OPTION;
                        if (scriptPanel.getDocument().getLength() > 0) {
                                answer = JOptionPane.showConfirmDialog(
                                        this,
                                        I18N.tr("Do you want to clear all before loading the file ?"),
                                        I18N.tr("Open file"),
                                        JOptionPane.YES_NO_CANCEL_OPTION);
                        }

                        String text;
                        try {
                                text = FileUtils.readFileToString(inFilePanel.getSelectedFile());
                        } catch (IOException e1) {
                                LOGGER.error(I18N.tr("IO error."), e1);
                                return;
                        }
                        
                        if (answer == JOptionPane.YES_OPTION) {
                                scriptPanel.setText(text);
                        } else if (answer == JOptionPane.NO_OPTION) {
                                scriptPanel.append(text);
                        }
                }
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
                        scriptPanel.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this, "onUserSelectionChange"));
                        scriptPanel.clearParsers();
                        actions.setAccelerators(scriptPanel);
                        // Actions will be set on the scriptPanel PopupMenu
                        scriptPanel.getPopupMenu().addSeparator();
                        actions.registerContainer(scriptPanel.getPopupMenu());
                        centerPanel = new RTextScrollPane(scriptPanel);                        
                        onUserSelectionChange();
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
                        interpreter.eval(text);
                        infoLogger.flush();
                        errorLogger.flush();
                        
                } catch (IOException e) {
                        setStatusMessage(e.getLocalizedMessage());
                        LOGGER.error(
                                e.getLocalizedMessage(),
                                e);
                } catch (IllegalArgumentException e) {
                        setStatusMessage(e.getLocalizedMessage());
                        LOGGER.error(
                                I18N.tr("Cannot execute the script"),
                                e);
                } catch (EvalError e) {
                        setStatusMessage(e.getLocalizedMessage());
                        LOGGER.error(
                                I18N.tr("The script is not valid"),
                                e);
                }
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
                        findReplaceDialog = new FindReplaceDialog(scriptPanel,UIFactory.getMainFrame());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
        
        /**
         * Change the status of the button when the console is empty or not.
         */
        public void onUserSelectionChange(){
                String text = scriptPanel.getText().trim();
                if (text.isEmpty()) {
                        executeAction.setEnabled(false);
                        clearAction.setEnabled(false);
                        saveAction.setEnabled(false);
                        findAction.setEnabled(false);
                }
                else{
                        executeAction.setEnabled(true);
                        clearAction.setEnabled(true);
                        saveAction.setEnabled(true);
                        findAction.setEnabled(true);
                }
        }
        
        
}
