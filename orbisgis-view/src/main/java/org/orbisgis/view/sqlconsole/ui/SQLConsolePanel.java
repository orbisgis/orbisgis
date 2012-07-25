/*
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
package org.orbisgis.view.sqlconsole.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.findReplace.FindReplaceDialog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.sqlconsole.actions.ExecuteScriptProcess;
import org.orbisgis.view.sqlconsole.codereformat.CodeReformator;
import org.orbisgis.view.sqlconsole.codereformat.CommentSpec;
import org.orbisgis.view.sqlconsole.language.SQLLanguageSupport;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * SQL Panel that contain a RSyntaxTextArea
 */
public class SQLConsolePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsolePanel.class);
        private final static Logger LOGGER = Logger.getLogger("gui." + SQLConsolePanel.class);
        
        
        // Components
        private JToolBar infoToolBar;
        private JToolBar commandToolBar;
        
        //Keep buttons reference to enable/disable them
        private Map<SQLConsoleAction,ArrayList<AbstractButton>> actionButtons = new HashMap<SQLConsoleAction,ArrayList<AbstractButton>>();
        
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private JLabel statusMessage;
        private Timer timer;
        private int lastSQLStatementToReformatStart;
        private int lastSQLStatementToReformatEnd;
        private static final String MESSAGEBASE = "%d | %d | %s";
        private int line = 0;
        private int character = 0;
        private String message = "";
        private SQLLanguageSupport lang;
        static CommentSpec[] COMMENT_SPECS = new CommentSpec[]{
                new CommentSpec("/*", "*/"), new CommentSpec("--", "\n")};
        private FindReplaceDialog findReplaceDialog;
        private MapContext mapContext;
        
        //Actions
        private List<SQLConsoleAction> actions = new ArrayList<SQLConsoleAction>();
                
        /**
         * Creates a console for sql.
         */
        public SQLConsolePanel() {
                super(new BorderLayout());
                initActions();
                JPanel split = new JPanel();
                split.setLayout(new BorderLayout());
                split.add(new SQLFunctionsPanel(), BorderLayout.EAST);
                split.add(getCenterPanel(), BorderLayout.CENTER);
                add(split, BorderLayout.CENTER);
                add(getStatusToolBar(), BorderLayout.SOUTH);
        }
        
        /**
         * Register action button, to enable/disable them later
         * @param button A button with a registered action instance of SQLConsoleAction
         */
        private void registerActionButton(AbstractButton button) {
                SQLConsoleAction action = (SQLConsoleAction) button.getAction();
                if(!actionButtons.containsKey(action)) {
                        actionButtons.put(action, new ArrayList<AbstractButton>());
                }
                actionButtons.get(action).add(button);
        }

        /**
         * Create actions instances
         */
        private void initActions() {
                //Execute Action
                actions.add(new SQLConsoleAction(
                        I18N.tr("Execute"),
                        I18N.tr("Run SQL statements"),
                        OrbisGISIcon.getIcon("execute"),
                        EventHandler.create(ActionListener.class,this,"onExecute"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK)
                        ));
                //Clear action
                actions.add(new SQLConsoleAction(
                        I18N.tr("Clear"),
                        I18N.tr("Erase the content of the editor"),
                        OrbisGISIcon.getIcon("erase"),
                        EventHandler.create(ActionListener.class,this,"onClear"),
                        null
                       ));
                //Open action
                actions.add(new SQLConsoleAction(
                        I18N.tr("Open"),
                        I18N.tr("Load a file in this editor"),
                        OrbisGISIcon.getIcon("open"),
                        EventHandler.create(ActionListener.class,this,"onOpenFile"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)
                       ));
        }
        /**
         * Return a set of button to control the sql panel features
         * 
         * @param setButtonText If true, a text is set on the buttons
         * @return Instance of JToolBar
         */
        public JToolBar getEditorToolBar(boolean setButtonText) {
                if(commandToolBar==null) {
                        commandToolBar = new JToolBar();
                        //Add all registered actions
                        for(SQLConsoleAction action : actions) {
                                JButton newButton = new JButton(action);
                                registerActionButton(newButton);
                                commandToolBar.add(newButton);
                        }
                        
                        //Final separator
                        commandToolBar.add(new JSeparator());
                }
                return commandToolBar;
        }
        
        
        
        /**
         * The map context is used to show the selected geometries
         * @param mapContext 
         */
        public void setMapContext(MapContext mapContext) {
                this.mapContext = mapContext;
        }
        private void feedPopupMenu(JPopupMenu areaMenu) {
                int customMenuCounter=0;
                for(SQLConsoleAction action : actions) {
                        JMenuItem actionItem = new JMenuItem(action);
                        registerActionButton(actionItem);
                        areaMenu.insert(actionItem, customMenuCounter++);                        
                }
                
                //Separator at the end
                areaMenu.insert(new JSeparator(),customMenuCounter++);                
        }
        
        /**
         * Text key shortcuts, Accelerators
         */
        private void setAccelerators(JComponent component) {
                InputMap im = component.getInputMap(WHEN_FOCUSED);                
                ActionMap actionMap = component.getActionMap();
                for(SQLConsoleAction action : actions) {
                        if(action.getKeyStroke()!=null) {
                                actionMap.put(im.get(action.getKeyStroke()), action);
                        }
                }
        }
        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_SQL);
                        //scriptPanel.getDocument().addDocumentListener(actionAndKeyListener);
                        scriptPanel.setLineWrap(true);
                        scriptPanel.setClearWhitespaceLinesEnabled(true);
                        scriptPanel.setMarkOccurrences(false);
                        setAccelerators(scriptPanel);
                        lang = new SQLLanguageSupport();
                        lang.install(scriptPanel);

                        CodeReformator codeReformator = new CodeReformator(";",
                                COMMENT_SPECS);
                        scriptPanel.addCaretListener(EventHandler.create(CaretListener.class,this,"onScriptPanelCaretUpdate"));
                        //Add custom actions
                        feedPopupMenu(scriptPanel.getPopupMenu());
                        centerPanel = new RTextScrollPane(scriptPanel);
                }
                return centerPanel;
        }
        /**
         * Run the Sql commands stored in the editor
         */
        public void onExecute() {                
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new ExecuteScriptProcess(getText(), this,mapContext));
        }
        /**
         * Open a dialog that let the user to select a file
         * and add or replace the content of the sql editor.
         */
        public void onOpenFile() {
                BufferedReader in=null;
                try {
                        final OpenFilePanel inFilePanel = new OpenFilePanel(
                                "sqlConsoleInFile",
                                I18N.tr("Open script"));
                        inFilePanel.addFilter("sql", I18N.tr("SQL script (*.sql)"));
                        String script;
                        final String eol = System.getProperty("line.separator");
                        if (UIFactory.showDialog(inFilePanel)) {
                                File selectedFile = inFilePanel.getSelectedFile();
                                in = new BufferedReader(
                                        new FileReader(selectedFile));
                                
                                int answer = JOptionPane.NO_OPTION;
                                if (scriptPanel.getDocument().getLength() > 0) {
                                        answer = JOptionPane.showConfirmDialog(
                                                this,
                                                I18N.tr("Do you want to clear all before loading the file ?"),
                                                I18N.tr("Open file"),
                                                JOptionPane.YES_NO_CANCEL_OPTION);
                                }

                                if (answer == JOptionPane.YES_OPTION) {
                                        scriptPanel.setText("");
                                }
                                
                                if(answer != JOptionPane.CANCEL_OPTION) {
                                        while(in.ready()) {
                                                scriptPanel.append(in.readLine());
                                        }
                                }
                        }
                } catch (IOException e1) {
                        LOGGER.error(I18N.tr("IO error."), e1);
                } finally {
                        if(in!=null) {
                                try {
                                        in.close();
                                } catch (IOException ex) {
                                        LOGGER.error(I18N.tr("Fail to close the file"), ex);
                                }
                        }
                }
        }
        /**
         * Prompt the user to accept the document cleaning.
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
         * Update the row:column label
         */
        public void onScriptPanelCaretUpdate() {
                line = scriptPanel.getCaretLineNumber() + 1;
                character = scriptPanel.getCaretOffsetFromLineStart();
                setStatusMessage(message);
        }
        private JToolBar getStatusToolBar() {

                if (infoToolBar == null) {
                        infoToolBar = new JToolBar();
                        statusMessage = new JLabel();
                        infoToolBar.add(statusMessage);
                        infoToolBar.setFloatable(false);

                        timer = new Timer(5000, new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        setStatusMessage("");
                                }
                        });
                        timer.setRepeats(false);
                }

                return infoToolBar;
        }

        public final void setStatusMessage(String message) {
                this.message = message;
                if (!message.isEmpty()) {
                        timer.restart();
                }
                statusMessage.setText(String.format(MESSAGEBASE, line, character, message));
        }

        public void setCharacter(int character) {
                this.character = character;
        }

        public void setLine(int line) {
                this.line = line;
        }

        public String getText() {
                return scriptPanel.getText();
        }

        public RSyntaxTextArea getScriptPanel() {
                return scriptPanel;
        }

        public String getCurrentSQLStatement() {
                String sql = scriptPanel.getSelectedText();
                lastSQLStatementToReformatEnd = scriptPanel.getSelectionEnd();
                lastSQLStatementToReformatStart = scriptPanel.getSelectionStart();
                if (sql == null || sql.trim().length() == 0) {
                        sql = getText();
                        lastSQLStatementToReformatEnd = -2;
                        // int[] bounds = getBoundsOfCurrentSQLStatement();
                        //
                        // if (bounds[0] >= bounds[1]) {
                        // sql = "";
                        // } else {
                        // sql = sql.substring(bounds[0], bounds[1]).trim();
                        // }
                }
                return sql != null ? sql : "";
        }

        public void replaceCurrentSQLStatement(String st) {

                if (lastSQLStatementToReformatStart >= lastSQLStatementToReformatEnd) {
                        scriptPanel.replaceRange(st, 0, scriptPanel.getDocument().getLength());
                } else {
                        scriptPanel.replaceRange(st, lastSQLStatementToReformatStart,
                                lastSQLStatementToReformatEnd);
                }
        }

        public int[] getBoundsOfCurrentSQLStatement() {
                int[] bounds = new int[2];
                bounds[0] = scriptPanel.getSelectionStart();
                bounds[1] = scriptPanel.getSelectionEnd();

                if (bounds[0] == bounds[1]) {
                        bounds = getSqlBoundsBySeparatorRule(scriptPanel.getCaretPosition());
                }

                return bounds;
        }

        private int[] getSqlBoundsBySeparatorRule(int iCaretPos) {
                int[] bounds = new int[2];

                String sql = getText();

                bounds[0] = lastIndexOfStateSep(sql, iCaretPos);
                bounds[1] = indexOfStateSep(sql, iCaretPos);

                return bounds;

        }

        private static int indexOfStateSep(String sql, int pos) {
                int ix = pos;

                int newLinteCount = 0;
                for (;;) {
                        if (sql.length() == ix) {
                                return sql.length();
                        }

                        if (false == Character.isWhitespace(sql.charAt(ix))) {
                                newLinteCount = 0;
                        }

                        if ('\n' == sql.charAt(ix)) {
                                ++newLinteCount;
                                if (2 == newLinteCount) {
                                        return ix - 1;
                                }
                        }

                        ++ix;
                }
        }

        private static int lastIndexOfStateSep(String sql, int pos) {
                int ix = pos;

                int newLinteCount = 0;
                for (;;) {

                        if (ix == sql.length()) {
                                if (ix == 0) {
                                        return ix;
                                } else {
                                        ix--;
                                }
                        }

                        if (false == Character.isWhitespace(sql.charAt(ix))) {
                                newLinteCount = 0;
                        }

                        if ('\n' == sql.charAt(ix)) {
                                ++newLinteCount;
                                if (2 == newLinteCount) {
                                        return ix + newLinteCount;
                                }
                        }

                        if (0 == ix) {
                                return 0 + newLinteCount;
                        }

                        --ix;
                }
        }

        public void insertString(String string) throws BadLocationException {
                scriptPanel.getDocument().insertString(
                        scriptPanel.getDocument().getLength(), string, null);
        }

        public void freeResources() {
                if (lang != null) {
                        lang.uninstall(scriptPanel);
                }
                if(timer!=null) {
                        timer.stop();
                }
        }

        /**
         * Open one instance of the find replace dialog
         */
        public void openFindReplaceDialog() {
                if (findReplaceDialog == null) {
                        findReplaceDialog = new FindReplaceDialog(getScriptPanel());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
        
        /**
         * Run the SQL command in the editor
         */
        private class RunAction extends AbstractAction {
                private static final long serialVersionUID = 1L;

                public RunAction() {
                        
                }

                @Override
                public void actionPerformed(ActionEvent ae) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }
                
        }
}
