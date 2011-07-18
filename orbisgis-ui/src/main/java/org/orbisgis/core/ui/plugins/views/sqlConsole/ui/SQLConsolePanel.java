/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher 
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
package org.orbisgis.core.ui.plugins.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.ui.components.jtextComponent.SearchWord;
import org.orbisgis.core.ui.components.jtextComponent.WordHighlightPainter;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.editorViews.toc.TransferableLayer;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.plugins.views.geocatalog.TransferableSource;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ActionsListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ConsoleAction;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.SQLConsoleKeyListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.codereformat.CodeReformator;
import org.orbisgis.core.ui.plugins.views.sqlConsole.codereformat.CommentSpec;
import org.orbisgis.core.ui.plugins.views.sqlConsole.syntax.SQLCompletionProvider;
import org.orbisgis.utils.I18N;

public class SQLConsolePanel extends JPanel implements DropTargetListener {

        private JButton btExecute = null;
        private JButton btClear = null;
        private JButton btOpen = null;
        private JButton btSave = null;
        private ActionsListener actionAndKeyListener;
        private ConsoleListener listener;
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private JButtonTextField searchTextField;
        private JToolBar toolBar;
        private JLabel statusMessage;
        private SearchWord searchWord;
        private Timer timer;
        private int lastSQLStatementToReformatStart;
        private int lastSQLStatementToReformatEnd;
        private SQLCompletionProvider cpl;
        // An instance of the private subclass of the default highlight painter
        Highlighter.HighlightPainter myHighlightPainter = (HighlightPainter) new WordHighlightPainter(
                new Color(205, 235, 255));
        private JPanel pnlTextFilter;
        static CommentSpec[] COMMENT_SPECS = new CommentSpec[]{
                new CommentSpec("/*", "*/"), new CommentSpec("--", "\n")};

        /**
         * Creates a console for sql.
         */
        public SQLConsolePanel(ConsoleListener listener) {
                this.listener = listener;
                actionAndKeyListener = new ActionsListener(listener, this);
                setLayout(new BorderLayout());
                add(getCenterPanel(listener), BorderLayout.CENTER);
                if (listener.showControlButtons()) {
                        add(getButtonToolBar(), BorderLayout.NORTH);
                }
                setButtonsStatus();
                add(getStatusToolBar(), BorderLayout.SOUTH);
                add(getStatusToolBar(), BorderLayout.SOUTH);
                //add(new SQLFunctionsPanel(this), BorderLayout.EAST);

                JPanel split = new JPanel();
                split.setLayout(new BorderLayout());
                split.add(new SQLFunctionsPanel(this), BorderLayout.EAST);
                split.add(getCenterPanel(listener), BorderLayout.CENTER);
                add(split, BorderLayout.CENTER);
                searchWord = new SearchWord(scriptPanel);

        }



        // getters
        private JToolBar getButtonToolBar() {
                final JToolBar northPanel = new JToolBar();
                northPanel.add(getBtExecute());
                northPanel.add(getBtClear());
                northPanel.add(getBtOpen());
                northPanel.add(getBtSave());
                northPanel.add(new JLabel("  "
                        + I18N.getString("orbisgis.org.orbisgis.FindText") + " "));
                northPanel.add(getJTextFieldPanel());
                setBtExecute();
                setBtClear();
                setBtSave();
                northPanel.setFloatable(false);
                northPanel.setBorderPainted(false);
                northPanel.setOpaque(false);

                return northPanel;
        }

        private RTextScrollPane getCenterPanel(ConsoleListener listener) {
                if (centerPanel == null) {
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_SQL);
                        scriptPanel.getDocument().addDocumentListener(actionAndKeyListener);
                        scriptPanel.setDropTarget(new DropTarget(centerPanel, this));
                        scriptPanel.setLineWrap(true);
                        cpl = new SQLCompletionProvider(scriptPanel);
                        cpl.install();

                        CodeReformator codeReformator = new CodeReformator(";",
                                COMMENT_SPECS);

                        scriptPanel.addKeyListener(new SQLConsoleKeyListener(this,
                                codeReformator, actionAndKeyListener));

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

        private JPanel getJTextFieldPanel() {
                if (null == pnlTextFilter) {
                        pnlTextFilter = new JPanel();
                        CRFlowLayout layout = new CRFlowLayout();
                        layout.setAlignment(CRFlowLayout.LEFT);
                        pnlTextFilter.setLayout(layout);
                        searchTextField = new JButtonTextField();
                        searchTextField.getDocument().addDocumentListener(
                                new DocumentListener() {

                                        public void removeUpdate(DocumentEvent e) {
                                                search();
                                        }

                                        public void insertUpdate(DocumentEvent e) {
                                                search();
                                        }

                                        public void changedUpdate(DocumentEvent e) {
                                                search();
                                        }
                                });
                        pnlTextFilter.add(searchTextField);
                }
                return pnlTextFilter;
        }

        public void search() {
                searchWord.removeHighlights();
                String pattern = searchTextField.getText();
                if (pattern.length() <= 0) {
                        setStatusMessage("");
                        return;
                }

                try {
                        Highlighter hilite = scriptPanel.getHighlighter();
                        Document doc = scriptPanel.getDocument();
                        String text = doc.getText(0, doc.getLength());
                        int pos = 0;

                        int patternFound = 0;

                        // Search for pattern
                        while ((pos = text.indexOf(pattern, pos)) >= 0) {
                                // Create highlighter using private painter and apply around
                                // pattern
                                hilite.addHighlight(pos, pos + pattern.length(),
                                        myHighlightPainter);
                                pos += pattern.length();
                                patternFound += 1;
                        }

                        if (patternFound > 0) {
                                setStatusMessage(pattern + " "
                                        + I18N.getString("orbisgis.org.orbisgis.found") + " "
                                        + patternFound + " "
                                        + I18N.getString("orbisgis.org.orbisgis.Times"));
                        } else {
                                setStatusMessage(pattern + " "
                                        + I18N.getString("orbisgis.org.orbisgis.notFound"));
                        }
                } catch (BadLocationException e) {
                }

        }

        private JButton getBtExecute() {
                if (null == btExecute) {
                        btExecute = new ConsoleButton(ConsoleAction.EXECUTE,
                                actionAndKeyListener);
                        btExecute.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Execute"));
                }
                return btExecute;
        }

        private JButton getBtClear() {
                if (null == btClear) {
                        btClear = new ConsoleButton(ConsoleAction.CLEAR,
                                actionAndKeyListener);
                        btClear.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Clear"));
                }
                return btClear;
        }

        private JButton getBtOpen() {
                if (null == btOpen) {
                        btOpen = new ConsoleButton(ConsoleAction.OPEN, actionAndKeyListener);
                        btOpen.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Open"));
                }
                return btOpen;
        }

        private JButton getBtSave() {
                if (null == btSave) {
                        btSave = new ConsoleButton(ConsoleAction.SAVE, actionAndKeyListener);
                        btSave.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Save"));
                }
                return btSave;
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

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
                final Transferable t = dtde.getTransferable();

                String query = listener.doDrop(t);
                if (query == null) {
                        try {
                                if ((t.isDataFlavorSupported(TransferableSource.getResourceFlavor()))
                                        || (t.isDataFlavorSupported(TransferableLayer.getLayerFlavor()))) {
                                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                        String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                                        dtde.getDropTargetContext().dropComplete(true);
                                        query = s;
                                } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                        String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                                        dtde.getDropTargetContext().dropComplete(true);
                                        query = s;
                                }
                        } catch (IOException e) {
                                dtde.rejectDrop();
                        } catch (UnsupportedFlavorException e) {
                                dtde.rejectDrop();
                        }
                }

                if (query != null) {
                        // Cursor position
                        int position = scriptPanel.viewToModel(dtde.getLocation());
                        try {
                                scriptPanel.getDocument().insertString(position, query, null);
                        } catch (BadLocationException e) {
                                ErrorMessages.error(
                                        I18N.getString("orbisgis.org.orbisgis.textArea.BadLocationException"),
                                        e);
                        }
                } else {
                        dtde.rejectDrop();
                }

                setButtonsStatus();
        }

        public void freeResources() {
                if (cpl != null) {
                        cpl.freeExternalResources();
                }
        }
}
