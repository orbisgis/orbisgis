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
package org.orbisgis.core.ui.components.findReplace;

import org.orbisgis.core.ui.components.findReplace.GBHelper;
import org.orbisgis.core.ui.components.findReplace.Gap;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchEngine;
import org.orbisgis.utils.I18N;

/**
 *
 * @author ebocher
 * FindReplaceDialog is based on the  Fred Swartz job's.
 * http://leepoint.net/notes-java/GUI/layouts/gridbag-example.html
 *
 */
public final class FindReplaceDialog extends JDialog implements ActionListener {

        private final RSyntaxTextArea rSyntaxTextArea;
        //================================================================ constants
        private static final int BORDER = 12;  // Window border in pixels.
        private static final int GAP = 5;   // Default gap btwn components.
        //=================================================================== fields
        JLabel findLbl;
        JLabel replaceLbl;
        JTextField findTF;
        JTextField replaceTF;
        JButton findBtn;
        JButton replaceBtn;
        JButton replAllBtn;
        JButton closeBtn;
        JCheckBox matchCaseCB;
        JCheckBox wholeWrdsCB;
        JCheckBox regexCB;
        JCheckBox markAllCB;
        JCheckBox upCB;
        JCheckBox downCB;

        public FindReplaceDialog(RSyntaxTextArea rSyntaxTextArea) {
                super();
                this.rSyntaxTextArea = rSyntaxTextArea;
                getContentPane().add(createContentPane());
                this.setLocationRelativeTo(rSyntaxTextArea);
                this.setTitle(I18N.getString("orbisgis.org.orbisgis.ui.findReplace"));
                pack();
                setResizable(false);
        }

        /**
         * Create the main panel
         * @return
         */
        private JPanel createContentPane() {


                //... Create an independent GridLayout panel of buttons.
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new GridLayout(5, 1, GAP, GAP));

                findBtn = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.find"));
                findBtn.setActionCommand("FindNext");
                findBtn.setMnemonic('f');
                findBtn.addActionListener(this);

                replaceBtn = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.replace"));
                replaceBtn.setActionCommand("Replace");
                replaceBtn.setMnemonic('r');
                replaceBtn.addActionListener(this);

                replAllBtn = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.replaceAll"));
                replAllBtn.setActionCommand("ReplaceAll");
                replAllBtn.setMnemonic('a');
                replAllBtn.addActionListener(this);

                closeBtn = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.close"));
                closeBtn.setActionCommand("Close");
                closeBtn.setMnemonic('c');
                closeBtn.addActionListener(this);
                closeBtn.setDefaultCapable(true);


                buttonPanel.add(findBtn);
                buttonPanel.add(replaceBtn);
                buttonPanel.add(replAllBtn);
                buttonPanel.add(closeBtn);

                //... Create an independent GridLayout panel of check boxes.
                JPanel checkBoxPanel = new JPanel();
                checkBoxPanel.setLayout(new GridLayout(3, 2));
                matchCaseCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.matchCase"));
                matchCaseCB.setMnemonic('m');

                wholeWrdsCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.wholeWords"));
                wholeWrdsCB.setMnemonic('w');

                regexCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.regularExpressions"));
                regexCB.setMnemonic('x');

                markAllCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.markAll"));
                markAllCB.setMnemonic('a');
                markAllCB.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (!markAllCB.isSelected()) {
                                        rSyntaxTextArea.clearMarkAllHighlights();
                                }
                        }
                });

                upCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.searchUp"), true);
                upCB.setMnemonic('u');
                upCB.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (!upCB.isSelected()) {
                                        downCB.setSelected(true);
                                } else {
                                        downCB.setSelected(false);
                                }
                        }
                });
                downCB = new JCheckBox(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.searchDown"));
                downCB.setMnemonic('d');
                downCB.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (!downCB.isSelected()) {
                                        upCB.setSelected(true);
                                } else {
                                        upCB.setSelected(false);
                                }
                        }
                });


                checkBoxPanel.add(matchCaseCB);
                checkBoxPanel.add(wholeWrdsCB);
                checkBoxPanel.add(regexCB);
                checkBoxPanel.add(markAllCB);
                checkBoxPanel.add(upCB);
                checkBoxPanel.add(downCB);

                //... Create GridBagLayout content pane; set border.
                JPanel content = new JPanel(new GridBagLayout());
                content.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER,
                        BORDER, BORDER));

                //GridBagLayout code begins here
                GBHelper pos = new GBHelper();  // Create GridBag helper object.

                //... First row
                findLbl = new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.findWhat"), JLabel.LEFT);
                content.add(findLbl, pos);
                content.add(new Gap(GAP), pos.nextCol());
                findTF = new JTextField(20);
                content.add(findTF, pos.nextCol().expandW());
                content.add(new Gap(GAP), pos.nextCol());
                content.add(buttonPanel, pos.nextCol().height(5).align(GridBagConstraints.NORTH));

                content.add(new Gap(GAP), pos.nextRow());  // Add a gap below

                //... Next row.
                replaceLbl = new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.findReplace.replaceWith"), JLabel.LEFT);
                content.add(replaceLbl, pos.nextRow());
                content.add(new Gap(GAP), pos.nextCol());
                replaceTF = new JTextField(20);
                content.add(replaceTF, pos.nextCol().expandW());

                content.add(new Gap(2 * GAP), pos.nextRow());  // Add a big gap below

                //... Last content row.
                content.add(checkBoxPanel, pos.nextRow().nextCol().nextCol());

                //... Add an area that can expand at the bottom.
                content.add(new Gap(), pos.nextRow().width().expandH());
                return content;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("FindNext".equals(command)) {
                        String text = findTF.getText();
                        if (text.length() == 0) {
                                return;
                        }
                        boolean markAll = markAllCB.isSelected();
                        boolean forward = downCB.isSelected();
                        boolean matchCase = matchCaseCB.isSelected();
                        boolean wholeWord = wholeWrdsCB.isSelected();
                        boolean regex = regexCB.isSelected();
                        if (markAll) {
                                rSyntaxTextArea.clearMarkAllHighlights();
                                rSyntaxTextArea.markAll(text, matchCase, wholeWord, regex);
                        }

                        boolean found = SearchEngine.find(rSyntaxTextArea, text, forward,
                                matchCase, wholeWord, regex);
                        if (!found) {
                                JOptionPane.showMessageDialog(this, I18N.getString("orbisgis.org.orbisgis.ui.findReplace.textNotFound"));
                        }
                } else if ("Close".equals(command)) {
                        setVisible(false);
                } else if ("Replace".equals(command)) {
                        String text = findTF.getText();
                        if (text.length() == 0) {
                                return;
                        }
                        String textReplace = replaceTF.getText();
                        if (textReplace.equals(text)) {
                                return;
                        } else {
                                boolean forward = downCB.isSelected();
                                boolean matchCase = matchCaseCB.isSelected();
                                boolean wholeWord = wholeWrdsCB.isSelected();
                                boolean regex = regexCB.isSelected();
                                boolean found = SearchEngine.replace(rSyntaxTextArea, text, textReplace, forward, matchCase, wholeWord, regex);
                                if (!found) {
                                        JOptionPane.showMessageDialog(this, I18N.getString("orbisgis.org.orbisgis.ui.findReplace.textNotFound"));
                                }
                        }

                } else if ("ReplaceAll".equals(command)) {
                        String text = findTF.getText();
                        if (text.length() == 0) {
                                return;
                        }
                        String textReplace = replaceTF.getText();
                        if (textReplace.equals(text)) {
                                return;
                        } else {
                                boolean matchCase = matchCaseCB.isSelected();
                                boolean wholeWord = wholeWrdsCB.isSelected();
                                boolean regex = regexCB.isSelected();
                                SearchEngine.replaceAll(rSyntaxTextArea, text, textReplace, matchCase, wholeWord, regex);
                        }
                }
        }
}
