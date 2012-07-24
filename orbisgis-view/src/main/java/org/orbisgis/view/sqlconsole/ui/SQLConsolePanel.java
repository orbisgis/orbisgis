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
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
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
        
        private JToolBar infoToolBar;
        private JToolBar commandToolBar;
        
        private List<AbstractButton> btExecute = new ArrayList<AbstractButton>();
        private JButton btClear = null;
        private JButton btOpen = null;
        private JButton btSave = null;
        private JButton btFindReplace = null;
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
        
        //Listeners
        private ActionListener executeListener = EventHandler.create(ActionListener.class,this,"onExecute");
        
        /**
         * Creates a console for sql.
         */
        public SQLConsolePanel() {
                super(new BorderLayout());
                JPanel split = new JPanel();
                split.setLayout(new BorderLayout());
                split.add(new SQLFunctionsPanel(), BorderLayout.EAST);
                split.add(getCenterPanel(), BorderLayout.CENTER);
                add(split, BorderLayout.CENTER);
                add(getStatusToolBar(), BorderLayout.SOUTH);
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
                        String executeText="";
                        if(setButtonText) {
                                executeText = I18N.tr("Execute");
                        }
                        JButton execute = new JButton(executeText,OrbisGISIcon.getIcon("execute"));
                        execute.setToolTipText(I18N.tr("Run SQL statements"));
                        execute.addActionListener(executeListener);
                        commandToolBar.add(execute);
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

        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_SQL);
                        //scriptPanel.getDocument().addDocumentListener(actionAndKeyListener);
                        scriptPanel.setLineWrap(true);
                        scriptPanel.setClearWhitespaceLinesEnabled(true);
                        scriptPanel.setMarkOccurrences(false);
                        lang = new SQLLanguageSupport();
                        lang.install(scriptPanel);

                        CodeReformator codeReformator = new CodeReformator(";",
                                COMMENT_SPECS);
                        scriptPanel.addCaretListener(EventHandler.create(CaretListener.class,this,"onScriptPanelCaretUpdate"));
                        //Add custom actions
                        JPopupMenu areaMenu = scriptPanel.getPopupMenu();
                        //Add Run menu
                        JMenuItem runSql = new JMenuItem(I18N.tr("Execute"),OrbisGISIcon.getIcon("execute"));
                        runSql.addActionListener(executeListener);
                        btExecute.add(runSql);
                        areaMenu.insert(runSql, 0);
                        areaMenu.insert(new JSeparator(),1);
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
         * Open one instanceof the find replace dialog
         */
        public void openFindReplaceDialog() {
                if (findReplaceDialog == null) {
                        findReplaceDialog = new FindReplaceDialog(getScriptPanel());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
}
