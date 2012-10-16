/*
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
package org.orbisgis.view.edition.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This is the dialog shown when the user close the application.
 * This modal dialog allow the user to save or discard changes before
 * closing the application.
 * @author Nicolas Fortin
 */
public class SaveDocuments extends JDialog {
        public enum CHOICE { CLOSE, CANCEL };
        private final static int BUTTON_VERTICAL_SPACE = 5;
        private static final Dimension minimumPanelDimension = new Dimension(320,200);
        private final static int PANEL_EMPTY_BORDER = 8; 
        private final static int BUTTON_EMPTY_BORDER = 10;
        
        // Var
        private CHOICE userCoice = CHOICE.CANCEL;
        private static final long serialVersionUID = 1L;
        private List<EditableElement> documents;
        private static final I18n I18N = I18nFactory.getI18n(SaveDocuments.class);
        private JLabel informationLabel = new JLabel();
        private DefaultListModel listModel = new DefaultListModel();
        private JList documentList = new JList(listModel);
        
        // Listeners
        private ActionListener saveListener = 
                EventHandler.create(ActionListener.class, this,"onSaveSelectedDocuments");
        private ActionListener saveAllListener = 
                EventHandler.create(ActionListener.class, this,"onSaveAllDocuments");
        private ActionListener saveNoneListener = 
                EventHandler.create(ActionListener.class, this,"onSaveNoneDocuments");
        private ActionListener cancelListener = 
                EventHandler.create(ActionListener.class, this,"onCancel");

        /**
         * Show this dialog and return only when the user :
         *  - close the dialog 
         *  - save all, some or none of the documents
         * @param owner
         * @param documents
         * @return Expected application behaviour, Close application or Cancel this process
         */
        public static CHOICE showModal(JFrame owner ,List<EditableElement> documents) {
                SaveDocuments saveDocuments = new SaveDocuments(owner,documents);
                saveDocuments.setModalityType(ModalityType.APPLICATION_MODAL);
                saveDocuments.create();
                saveDocuments.setLocationRelativeTo(owner);
                saveDocuments.setVisible(true);
                return saveDocuments.userCoice;
        } 
        
        /**
         * Constructor
         * @param owner Owner of the dialog
         * @param documents Modified document
         */
        private SaveDocuments(JFrame owner ,List<EditableElement> documents) {
                super(owner);
                setTitle(I18N.tr("Save documents"));
                this.documents = documents;
        }
        
        private void create() {
                JPanel panel = new JPanel(new BorderLayout());
                setContentPane(panel);
                informationLabel.setPreferredSize(new Dimension(1, 1));   
                panel.add(informationLabel,BorderLayout.NORTH);
                // Add document list at center
                add(documentList, BorderLayout.CENTER);
                // Buttons at right
                JPanel buttonPanel = new JPanel();
                add(buttonPanel,BorderLayout.EAST);
                BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
                buttonPanel.setLayout(boxLayout);
                // Save select button
                // This button save the selected documents
                JButton saveSelectedDocuments = new JButton(I18N.tr("&Save"));
                MenuCommonFunctions.setMnemonic(saveSelectedDocuments);
                saveSelectedDocuments.setToolTipText(I18N.tr("Save the selected documents,"
                        + " and close this dialog if there is no more modifed documents"));
                saveSelectedDocuments.addActionListener(saveListener);                
                buttonPanel.add(saveSelectedDocuments);
                buttonPanel.add(Box.createRigidArea(new Dimension(0, BUTTON_VERTICAL_SPACE)));
                // Save All button
                JButton saveAllDocuments = new JButton(I18N.tr("Save &All"));
                MenuCommonFunctions.setMnemonic(saveAllDocuments);
                saveAllDocuments.addActionListener(saveAllListener);
                saveAllDocuments.setToolTipText(I18N.tr("Save all documents and close this dialog"));
                buttonPanel.add(saveAllDocuments);
                buttonPanel.add(Box.createRigidArea(new Dimension(0, BUTTON_VERTICAL_SPACE)));
                // Save All button
                JButton saveNoneDocuments = new JButton(I18N.tr("Save &None"));
                MenuCommonFunctions.setMnemonic(saveNoneDocuments);
                saveNoneDocuments.addActionListener(saveNoneListener);
                saveNoneDocuments.setToolTipText(I18N.tr("Discard all changes and close this dialog"));                
                buttonPanel.add(saveNoneDocuments);
                buttonPanel.add(Box.createVerticalGlue());
                // Cancel button
                JButton cancel = new JButton(I18N.tr("&Cancel"));
                cancel.setToolTipText(I18N.tr("Do nothing and lose nothing"));
                MenuCommonFunctions.setMnemonic(cancel);
                informationLabel.setForeground(Color.red.darker());
                cancel.addActionListener(cancelListener);
                buttonPanel.add(cancel);  
                updateDocuments();      
                setSameButtonWidth(buttonPanel); 
                setMinimumSize(minimumPanelDimension);
                panel.setBorder(BorderFactory.createEmptyBorder(PANEL_EMPTY_BORDER,
                        PANEL_EMPTY_BORDER, PANEL_EMPTY_BORDER, PANEL_EMPTY_BORDER));
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,
                        BUTTON_EMPTY_BORDER, 0, 0));
                pack();
        }
        
        
        private void setSameButtonWidth(JPanel buttonContainer) {
                //Set this size to all buttons
                for(Component component : buttonContainer.getComponents()) {
                        if(component instanceof JButton) {
                               component.setMaximumSize(new Dimension(Short.MAX_VALUE,
                                  component.getMaximumSize().height));
                        }
                } 
        }
        
        
        /**
         *  Save the selected documents
         *  If there is remaining items, it select the first one
         *  and wait for new user action when there is no more items
         *  the application is closed
         */
        public void onSaveSelectedDocuments() {
                List<EditableElement> documentsToSave = new ArrayList<EditableElement>();
                for(Object docObj : documentList.getSelectedValues()) {
                        if(docObj instanceof EditableElement) {
                               documentsToSave.add((EditableElement)docObj);
                        }
                }
                if(saveDocuments(documentsToSave) && listModel.isEmpty()) {
                        userCoice = CHOICE.CLOSE;
                        setVisible(false);
                        return;
                }
                informationLabel.setText("");
                informationLabel.setIcon(null);
        }
        
        /**
         * 
         * @param documentsToSave
         * @return True if all provided documents has been successfully saved
         */
        private boolean saveDocuments(List<EditableElement> documentsToSave) {
                for(EditableElement document : documentsToSave) {
                        if(document.isModified()) {
                                try {
                                        document.save();
                                } catch (UnsupportedOperationException ex) {
                                        informationLabel.setText(getLocalisedDocumentErrorMessage(document,ex));
                                        informationLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                                        updateDocuments();
                                        return false;
                                } catch (EditableElementException ex) {
                                        informationLabel.setText(getLocalisedDocumentErrorMessage(document,ex));
                                        informationLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                                        updateDocuments();
                                        return false;
                                }
                        }
                }
                updateDocuments();
                return true;
        }
        
        /**
         * The user want to discard all modifications
         */
        public void onSaveNoneDocuments() {
                userCoice = CHOICE.CLOSE;
                setVisible(false);
        }
        /**
         * Remove saved documents
         */
        private void updateDocuments() {
                listModel.clear();
                documentList.clearSelection();
                for(EditableElement document : documents) {
                        if(document.isModified()) {
                                listModel.add(listModel.size(),document);
                        }                        
                }
                if(!listModel.isEmpty()) {
                        documentList.setSelectedIndex(0);
                }
        }
        
        private static String getLocalisedDocumentErrorMessage(EditableElement document,Throwable ex) {
                return "<html>"+I18N.tr("The document {0} cannot be saved due to the following error {1}",document,ex.getLocalizedMessage())+"</html>";
        }
        
        /**
         * The user cancel the closing processing of the software
         */
        public void onCancel() {
                userCoice = CHOICE.CANCEL;
                setVisible(false);
        }
        
        /**
         * Save all documents and close the application
         */
        public void onSaveAllDocuments() {
                if(saveDocuments(documents)) {
                        userCoice = CHOICE.CLOSE;
                        setVisible(false);                        
                }
        }
        
        
}
