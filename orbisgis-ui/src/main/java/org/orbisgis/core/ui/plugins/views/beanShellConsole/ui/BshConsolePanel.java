/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.ui.components.jtextComponent.SearchWord;
import org.orbisgis.core.ui.components.jtextComponent.WordHighlightPainter;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshActionsListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleAction;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleListener;
import org.orbisgis.utils.I18N;

public class BshConsolePanel extends JPanel {

    private JButton btExecute = null;


    private JButton btClear = null;


    private JButton btOpen = null;


    private JButton btSave = null;


    private BshActionsListener actionAndKeyListener;


    private JPanel centerPanel;


    private BshScriptPanel scriptPanel;


    private JButtonTextField searchTextField;


    private JToolBar toolBar;


    private JLabel statusMessage;


    private SearchWord searchWord;


    private Timer timer;

    // An instance of the private subclass of the default highlight painter

    Highlighter.HighlightPainter myHighlightPainter = (HighlightPainter) new WordHighlightPainter(
            new Color(205, 235, 255));


    private JPanel pnlTextFilter;


    /**
     * Creates a console for sql.
     */
    public BshConsolePanel(BshConsoleListener listener) {
        actionAndKeyListener = new BshActionsListener(listener, this);

        setLayout(new BorderLayout());
        add(getCenterPanel(listener), BorderLayout.CENTER);
        if (listener.showControlButtons()) {
            add(getButtonToolBar(), BorderLayout.NORTH);
        }
        setButtonsStatus();
        add(getStatusToolBar(), BorderLayout.SOUTH);

        searchWord = new SearchWord(scriptPanel.getTextComponent());

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


    private JPanel getCenterPanel(BshConsoleListener listener) {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            scriptPanel = new BshScriptPanel(actionAndKeyListener, listener);
            centerPanel.add(scriptPanel, BorderLayout.CENTER);
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
            Highlighter hilite = scriptPanel.getTextComponent().getHighlighter();
            Document doc = scriptPanel.getTextComponent().getDocument();
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
            btExecute = new BshConsoleButton(BshConsoleAction.EXECUTE,
                                             actionAndKeyListener);
            btExecute.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Execute"));
        }
        return btExecute;
    }


    private JButton getBtClear() {
        if (null == btClear) {
            btClear = new BshConsoleButton(BshConsoleAction.CLEAR,
                                           actionAndKeyListener);
            btClear.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Clear"));
        }
        return btClear;
    }


    private JButton getBtOpen() {
        if (null == btOpen) {
            btOpen = new BshConsoleButton(BshConsoleAction.OPEN,
                                          actionAndKeyListener);
            btOpen.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Open"));
        }
        return btOpen;
    }


    private JButton getBtSave() {
        if (null == btSave) {
            btSave = new BshConsoleButton(BshConsoleAction.SAVE,
                                          actionAndKeyListener);
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


    public void setText(String text) {
        scriptPanel.setText(text);
        setButtonsStatus();
    }


    public void insertString(String string) throws BadLocationException {
        scriptPanel.insertString(string);
    }


    public JTextComponent getTextComponent() {
        return scriptPanel.getTextComponent();
    }


}