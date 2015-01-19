/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.sif;

import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * UIFactory is a factory to build SIF dialog.
 * @author Erwan Bocher
 */
public class UIFactory {

        private static final I18n I18N = I18nFactory.getI18n(UIFactory.class);
        private static URL defaultIconURL;
        private static ImageIcon defaultIcon;
        private static Window mainFrame = null;
        private static Properties fileDialogPersistence = new Properties();
        private static final String OPEN_DIALOG_PROPERTIES_FILENAME = "opendialog.ini";

        private UIFactory(){}

        public static SIFDialog getSimpleDialog(UIPanel panel) {
                return getSimpleDialog(panel, mainFrame);
        }

        /**
         * Sif load persistence information, like current folder in dialogs
         * @param sifWorkspaceFolder Folder where to load SIF persistence data
         */
        public static void loadState(File sifWorkspaceFolder) throws IOException {
                File iniFile = new File(sifWorkspaceFolder,OPEN_DIALOG_PROPERTIES_FILENAME);
                if(iniFile.exists()) {
                        FileReader iniReader=null;
                        try {
                               iniReader = new FileReader(iniFile);
                               fileDialogPersistence.load(iniReader);
                        } finally {
                                if(iniReader!=null) {
                                        iniReader.close();
                                }
                        }
                }
        }
        /**
         * Sif load persistence information, like current folder in dialogs
         * @param sifWorkspaceFolder Folder where to save SIF persistence data
         */
        public static void saveState(File sifWorkspaceFolder) throws IOException {
                File iniFile = new File(sifWorkspaceFolder,OPEN_DIALOG_PROPERTIES_FILENAME);
                if(!sifWorkspaceFolder.exists()) {
                        sifWorkspaceFolder.mkdir();
                }
                FileWriter iniWriter=null;
                try {
                        iniWriter = new FileWriter(iniFile);
                        fileDialogPersistence.store(iniWriter, "File Dialogs properties");
                } finally {
                        if(iniWriter!=null) {
                                iniWriter.close();
                        }
                }
        }

        /**
         * @return Stored properties of the dialogs
         */
        public static Properties getFileDialogPersistence() {
                return fileDialogPersistence;
        }
        
        public static SIFDialog getSimpleDialog(UIPanel panel, Window owner) {
                return getSimpleDialog(panel, owner, true);
        }

        public static SIFDialog getSimpleDialog(UIPanel panel, boolean b) {
                return getSimpleDialog(panel, mainFrame, true);
        }

        public static SIFDialog getSimpleDialog(UIPanel panel, Window owner,
                boolean okCancel) {
                SIFDialog dlg = new SIFDialog(owner, okCancel);
                SimplePanel simplePanel = new SimplePanel(dlg, panel);
                dlg.setTitle(panel.getTitle());
                dlg.setComponent(simplePanel);
                return dlg;
        }

        public static SIFWizard getWizard(UIPanel[] panels) {
                return getWizard(panels, mainFrame);
        }

        private static SIFWizard getWizard(UIPanel[] panels, Window owner) {
                SIFWizard dlg = new SIFWizard(owner);
                SimplePanel[] simplePanels = new SimplePanel[panels.length];
                for (int i = 0; i < simplePanels.length; i++) {
                        simplePanels[i] = new SimplePanel(dlg, panels[i]);
                }
                dlg.setComponent(simplePanels);
                return dlg;
        }

        public static boolean showDialog(UIPanel[] panels) {
                return showDialog(panels, true);
        }

        public static boolean showDialog(UIPanel[] panels, boolean okCancel) {
                return showDialog(panels, okCancel, false);
        }

        /**
         * Create a dialog and specify if the dialog shows the ok cancel buttons
         * and if its on top of all swing GUI.
         *
         * @param panels
         * @param okCancel
         * @param onTop
         * @return
         */
        public static boolean showDialog(UIPanel[] panels, boolean okCancel, boolean onTop) {
                if (mainFrame == null) {
                        //No way mainframe must be used,
                        //resources can not be freed otherwise
                        throw new RuntimeException("Main Frame is not set");
                }
                return showDialog(panels, okCancel, onTop, mainFrame);
        }

        public static boolean showDialog(UIPanel[] panels, boolean okCancel, boolean onTop, Window parent) {
                AbstractOutsideFrame dlg;
                if (panels.length == 0) {
                        throw new IllegalArgumentException(
                                I18N.tr("sif.uIFactory.atLeastAPanelHasToBeSpecified")); //$NON-NLS-1$
                } else if (panels.length == 1) {
                        if (okCancel) {
                                dlg = getSimpleDialog(panels[0]);
                        } else {
                                dlg = getSimpleDialog(panels[0], parent, false);
                        }
                } else {
                        dlg = getWizard(panels);
                }
                dlg.setModal(true);
                dlg.pack();
                dlg.setLocationRelativeTo(parent);
                dlg.setAlwaysOnTop(onTop);
                // Show the dialog, block until the user click on a button
                dlg.setVisible(true);
                // Save the state
                if(dlg.isAccepted()) {
                        for(UIPanel panel : panels) {
                                if(panel instanceof UIPersistence) {
                                        ((UIPersistence)panel).saveState();
                                }
                        }
                }
                return dlg.isAccepted();
        }

        /**
         * Builds an apply dialog. It places the given UIPanel in a dialog that has
         * three buttons : OK, Apply and Cancel. The action associated to the Apply
         * button is configured thanks to the provided ActionListener.
         * @param panel The main panel of the dialog
         * @param applyListener The action associated to the Apply button
         * @return true if the dialog has been accepted, false if it has been cancelled or if it is not modal.
         */
        public static boolean showApplyDialog(UIPanel panel, ActionListener applyListener,boolean modal){
            ApplyDialog dlg = new ApplyDialog(mainFrame, applyListener);
            SimplePanel sp = new SimplePanel(dlg, panel);
            dlg.setComponent(sp);
            dlg.setModal(modal);
            dlg.pack();
            dlg.setLocationRelativeTo(mainFrame);
            dlg.setAlwaysOnTop(true);
            dlg.setTitle(panel.getTitle());
            dlg.setVisible(true);
            return dlg.isAccepted();
        }

        /** 
         * Show the given wizard as a modal, on top and visible frame.
         * @param wiz The wizard to be displayed.
         * @return true if the wizard has been accepted, false otherwise.
         */
        public static boolean showWizard(SIFWizard wiz){
            wiz.setModal(true);
            wiz.pack();
            wiz.setLocationRelativeTo(mainFrame);
            wiz.setAlwaysOnTop(true);
            wiz.setVisible(true);
            return wiz.isAccepted();
        }

        /**
         * Create a dialog and specify if the dialog shows the ok cancel buttons
         * and if its on top of all swing GUI.
         *
         * @param panel
         * @param okCancel
         * @param onTop
         * @return
         */
        public static boolean showDialog(UIPanel panel, boolean okCancel, boolean onTop) {
                return showDialog(new UIPanel[]{panel}, okCancel, onTop);
        }

        /**
         * Create a dialog and specify if the dialog shows the ok cancel buttons
         * and on top of all swing GUI.
         *
         * @param panel
         * @return
         */
        public static boolean showDialog(UIPanel panel) {
                return showDialog(new UIPanel[]{panel}, true);
        }

        /**
         * Create a dialog with ok button on top of all swing GUI.
         *
         * @param panel
         * @return
         */
        public static void showOkDialog(UIPanel panel) {
                showDialog(new UIPanel[]{panel}, false);
        }

       
        public static URL getDefaultIcon() {
                return defaultIconURL;
        }

        public static ImageIcon getDefaultImageIcon() {
                return defaultIcon;
        }

        public static void setDefaultIcon(URL iconURL) {
                UIFactory.defaultIconURL = iconURL;
        }

        public static void setDefaultImageIcon(ImageIcon icon) {
                UIFactory.defaultIcon = icon;
        }

        public static void setMainFrame(Window wnd) {
                mainFrame = wnd;
        }

        /**
         * @deprecated Use OSGi declarative services reference org.orbisgis.mainframe.api.MainWindow class or call
         * {@link javax.swing.SwingUtilities#getWindowAncestor(java.awt.Component)}
         * @return Main Frame instance
         */
        public static Window getMainFrame() {
                return mainFrame;
        }

        public static I18n getI18n() {
                return I18N;
        }        
        
}
