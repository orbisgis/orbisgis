/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
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
package org.orbisgis.sif;

import java.awt.Window;
import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * UIFactory is a factory to build SIF dialog.
 *
 * @author ebocher
 */
public class UIFactory {

        protected final static I18n i18n = I18nFactory.getI18n(UIFactory.class);
        private static HashMap<String, String> inputs = new HashMap<String, String>();
        private static URL defaultIconURL;
        private static ImageIcon defaultIcon;
        private static String okMessage;
        private static Window mainFrame = null;

        public static SIFDialog getSimpleDialog(UIPanel panel) {
                return getSimpleDialog(panel, mainFrame);
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
                dlg.setComponent(simplePanel, inputs);
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
                dlg.setComponent(simplePanels, inputs);
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
                AbstractOutsideFrame dlg;
                if (panels.length == 0) {
                        throw new IllegalArgumentException(
                                i18n.tr("sif.uIFactory.atLeastAPanelHasToBeSpecified")); //$NON-NLS-1$
                } else if (panels.length == 1) {
                        if (okCancel) {
                                dlg = getSimpleDialog(panels[0]);
                        } else {
                                dlg = getSimpleDialog(panels[0], mainFrame, false);
                        }
                } else {
                        dlg = getWizard(panels);
                }
                dlg.setModal(true);
                dlg.pack();
                dlg.setLocationRelativeTo(mainFrame);
                dlg.setVisible(true);
                dlg.setAlwaysOnTop(onTop);

                return dlg.isAccepted();
        }

        public static boolean showDialog(UIPanel panel, boolean okCancel, boolean onTop) {
                return showDialog(new UIPanel[]{panel}, okCancel, onTop);
        }

        public static boolean showDialog(UIPanel panel) {
                return showDialog(new UIPanel[]{panel}, true);
        }

        public static void showOkDialog(UIPanel panel) {
                showDialog(new UIPanel[]{panel}, false);
        }

        public static void setInputFor(String id, String inputName) {
                inputs.put(id, inputName);
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

        public static String getDefaultOkMessage() {
                return okMessage;
        }

        public static void setDefaultOkMessage(String msg) {
                okMessage = msg;
        }

        public static void setMainFrame(Window wnd) {
                mainFrame = wnd;
        }

        public static Window getMainFrame() {
                return mainFrame;
        }
}
