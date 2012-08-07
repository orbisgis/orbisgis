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
package org.orbisgis.view.components.findReplace;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 * FindReplaceDialog is based on the Fred Swartz job's.
 * http://leepoint.net/notes-java/GUI/layouts/gridbag-example.html
 *
 */
public final class FindReplaceDialog extends JDialog {
        private static final long serialVersionUID = 1L;
        protected static final I18n I18N = I18nFactory.getI18n(FindReplaceDialog.class);
        private static final Logger LOGGER = Logger.getLogger(FindReplaceDialog.class);
        private AtomicBoolean initialised = new AtomicBoolean(false);
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

        @Override
        public void addNotify() {
                super.addNotify();
                initialize();
        }
        
        private void initialize() {
                if(!initialised.getAndSet(true)) {
                        getContentPane().add(createContentPane());
                        setLocationRelativeTo(rSyntaxTextArea);
                        setTitle(I18N.tr("Search replace"));
                        pack();
                        setResizable(false);
                }
        }
        
        private FindReplaceDialog() {
                rSyntaxTextArea = null;
        }

        /**
         * This constructor doesn't define an owner.
         * L&F and dispose functions will not be called automatically
         * 
         * @param rSyntaxTextArea 
         * @deprecated Use a constructor with a frame owner
         */
        public FindReplaceDialog(RSyntaxTextArea rSyntaxTextArea) {
                super();
                this.rSyntaxTextArea = rSyntaxTextArea;
        }

        public FindReplaceDialog(RSyntaxTextArea rSyntaxTextArea, Window window) {
                super(window);
                this.rSyntaxTextArea = rSyntaxTextArea;
        }

        public FindReplaceDialog(RSyntaxTextArea rSyntaxTextArea, Dialog dialog) {
                super(dialog);
                this.rSyntaxTextArea = rSyntaxTextArea;
        }

        public FindReplaceDialog(RSyntaxTextArea rSyntaxTextArea, Frame frame) {
                super(frame);
                this.rSyntaxTextArea = rSyntaxTextArea;
        }

        
        /**
         * Click on the find next button
         */
        public void onFindNext() {
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

                SearchContext c = new SearchContext();
                c.setMatchCase(matchCase);
                c.setSearchFor(text);
                c.setWholeWord(wholeWord);
                c.setRegularExpression(regex);
                c.setSearchForward(forward);
                boolean found = SearchEngine.find(rSyntaxTextArea, c);
                if (!found) {
                        LOGGER.info(I18N.tr("Text not found !"));
                }
        }

        /**
         * Click on the replace button
         */
        public void onReplace() {
                String text = findTF.getText();
                if (text.length() == 0) {
                        return;
                }
                String textReplace = replaceTF.getText();
                if (!textReplace.equals(text)) {
                        boolean forward = downCB.isSelected();
                        boolean matchCase = matchCaseCB.isSelected();
                        boolean wholeWord = wholeWrdsCB.isSelected();
                        boolean regex = regexCB.isSelected();
                        SearchContext c = new SearchContext();
                        c.setMatchCase(matchCase);
                        c.setSearchFor(text);
                        c.setReplaceWith(textReplace);
                        c.setWholeWord(wholeWord);
                        c.setRegularExpression(regex);
                        c.setSearchForward(forward);
                        boolean found = SearchEngine.find(rSyntaxTextArea, c);
                        if (!found) {
                                LOGGER.info(I18N.tr("Text not found !"));
                        }
                }                
        }
        
        /**
         * Click on the replace all button
         */
        public void onReplaceAll() {                
                String text = findTF.getText();
                if (text.length() == 0) {
                        return;
                }
                String textReplace = replaceTF.getText();
                if (!textReplace.equals(text)) {
                        boolean matchCase = matchCaseCB.isSelected();
                        boolean wholeWord = wholeWrdsCB.isSelected();
                        boolean regex = regexCB.isSelected();
                        SearchContext c = new SearchContext();
                        c.setMatchCase(matchCase);
                        c.setSearchFor(text);
                        c.setWholeWord(wholeWord);
                        c.setRegularExpression(regex);
                        c.setReplaceWith(textReplace);
                        SearchEngine.find(rSyntaxTextArea, c);
                }
        }
        
        /**
         * Click on the close button
         */
        public void onClose() {
                setVisible(false);
        }
        
        /**
         * Click on the regex check box
         */
        public void onRegularExpression() {
                if (regexCB.isSelected()) {
                        wholeWrdsCB.setSelected(false);
                        matchCaseCB.setSelected(false);
                        wholeWrdsCB.setEnabled(false);
                        matchCaseCB.setEnabled(false);
                } else {
                        wholeWrdsCB.setEnabled(true);
                        matchCaseCB.setEnabled(true);
                }

        }
        
        /**
         * Click on mark all check box
         */
        public void onMarkAll() {
                if (!markAllCB.isSelected()) {
                        rSyntaxTextArea.clearMarkAllHighlights();
                }
        }
        
        /**
         * Create the main panel
         *
         * @return
         */
        private JPanel createContentPane() {
                //... Create an independent GridLayout panel of buttons.
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new GridLayout(5, 1, GAP, GAP));

                findBtn = new JButton(I18N.tr("&Find"));
                MenuCommonFunctions.setMnemonic(findBtn);
                findBtn.addActionListener(EventHandler.create(ActionListener.class,this,"onFindNext"));

                replaceBtn = new JButton(I18N.tr("&Replace"));
                MenuCommonFunctions.setMnemonic(replaceBtn);
                replaceBtn.addActionListener(EventHandler.create(ActionListener.class,this,"onReplace"));

                replAllBtn = new JButton(I18N.tr("Replace &All"));
                MenuCommonFunctions.setMnemonic(replAllBtn);
                replAllBtn.addActionListener(EventHandler.create(ActionListener.class,this,"onReplaceAll"));

                closeBtn = new JButton(I18N.tr("&Close"));
                MenuCommonFunctions.setMnemonic(closeBtn);
                closeBtn.addActionListener(EventHandler.create(ActionListener.class,this,"onClose"));
                closeBtn.setDefaultCapable(true);


                buttonPanel.add(findBtn);
                buttonPanel.add(replaceBtn);
                buttonPanel.add(replAllBtn);
                buttonPanel.add(closeBtn);

                //... Create an independent GridLayout panel of check boxes.
                JPanel checkBoxPanel = new JPanel();
                checkBoxPanel.setLayout(new GridLayout(3, 2));
                matchCaseCB = new JCheckBox(I18N.tr("&Match Case"));
                MenuCommonFunctions.setMnemonic(matchCaseCB);

                wholeWrdsCB = new JCheckBox(I18N.tr("&Whole Words"));
                MenuCommonFunctions.setMnemonic(wholeWrdsCB);

                regexCB = new JCheckBox(I18N.tr("Regular E&xpressions"));
                MenuCommonFunctions.setMnemonic(regexCB);
                regexCB.addActionListener(EventHandler.create(ActionListener.class,this,"onRegularExpression"));

                markAllCB = new JCheckBox(I18N.tr("M&ark all"));
                MenuCommonFunctions.setMnemonic(markAllCB);
                markAllCB.addActionListener(EventHandler.create(ActionListener.class,this,"onMarkAll"));

                ButtonGroup searchBG = new ButtonGroup();
                upCB = new JCheckBox(I18N.tr("Search &up"), true);
                MenuCommonFunctions.setMnemonic(upCB);
                searchBG.add(upCB);
                
                downCB = new JCheckBox(I18N.tr("Search &down"));
                MenuCommonFunctions.setMnemonic(downCB);
                searchBG.add(downCB);

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
                findLbl = new JLabel(I18N.tr("Find what:"), JLabel.LEFT);
                content.add(findLbl, pos);
                content.add(new Gap(GAP), pos.nextCol());
                findTF = new JTextField(20);
                content.add(findTF, pos.nextCol().expandW());
                content.add(new Gap(GAP), pos.nextCol());
                content.add(buttonPanel, pos.nextCol().height(5).align(GridBagConstraints.NORTH));

                content.add(new Gap(GAP), pos.nextRow());  // Add a gap below

                //... Next row.
                replaceLbl = new JLabel(I18N.tr("Replace with:"), JLabel.LEFT);
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
}
