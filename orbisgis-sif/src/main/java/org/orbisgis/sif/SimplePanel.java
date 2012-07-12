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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SimplePanel extends JPanel {

        protected final static I18n i18n = I18nFactory.getI18n(SimplePanel.class);
        private static final Logger logger = Logger.getLogger(SimplePanel.class);
        // a somehow difficult to stumble upon constant, to represent a canceled action
        public static final String CANCELED_ACTION = String.valueOf(
                Integer.MAX_VALUE);
        private MsgPanel msgPanel;
        private UIPanel uiPanel;
        private OutsideFrame outsideFrame;
        private Component firstFocus;

        /**
         * This is the default constructor
         */
        public SimplePanel(OutsideFrame frame, UIPanel panel) {
                this.uiPanel = panel;
                this.outsideFrame = frame;
                initialize(panel);
        }

        /**
         * This method initializes this
         *
         * @return void
         */
        private void initialize(UIPanel panel) {
                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BorderLayout());
                Component comp = panel.getComponent();
                fillFirstComponent(comp);
                centerPanel.add(comp, BorderLayout.CENTER);
                msgPanel = new MsgPanel(getIcon());
                msgPanel.setTitle(panel.getTitle());

                this.setLayout(new BorderLayout());
                this.add(msgPanel, BorderLayout.NORTH);

                this.add(centerPanel, BorderLayout.CENTER);
        }

        private boolean fillFirstComponent(Component comp) {
                if (comp instanceof Container) {
                        Container cont = (Container) comp;
                        for (int i = 0; i < cont.getComponentCount(); i++) {
                                if (fillFirstComponent(cont.getComponent(i))) {
                                        return true;
                                }
                        }

                        return false;
                } else {
                        firstFocus = comp;
                        this.addComponentListener(new ComponentAdapter() {

                                @Override
                                public void componentShown(ComponentEvent e) {
                                        firstFocus.requestFocus();
                                }
                        });

                        return true;
                }
        }

        public void initialize() {
                SIFMessage err;
                try {
                       err = uiPanel.initialize();
                } catch (Exception e) {
                        String msg = i18n.tr(
                                "Cannot initialize the dialog");
                        logger.error(msg, e);
                        err = new SIFMessage(msg + ": " + e.getMessage(), SIFMessage.ERROR);
                }
                if (err.getMessageType() != SIFMessage.OK) {
                        msgPanel.setError("Panel initialisation error");
                        outsideFrame.cannotContinue();
                }
        }

        public void validateInput() {
                SIFMessage err = uiPanel.validateInput();
                if (err.getMessageType() == SIFMessage.ERROR) {
                        msgPanel.setError(err.getMessage());
                        //On met le message ici
                        outsideFrame.cannotContinue();
                } else if (err.getMessageType() == SIFMessage.WARNING) {
                        msgPanel.setWarning(err.getMessage());
                        outsideFrame.canContinue();
                } else {
                        msgPanel.setText(uiPanel.getInfoText());
                        outsideFrame.canContinue();
                }
        }

        public ImageIcon getIcon() {
                URL iconURL = uiPanel.getIconURL();
                if (iconURL == null) {
                        iconURL = UIFactory.getDefaultIcon();
                }

                if (iconURL != null) {
                        return new ImageIcon(iconURL);
                } else {
                        return UIFactory.getDefaultImageIcon();
                }
        }

        public Image getIconImage() {
                ImageIcon ii = getIcon();
                if (ii == null) {
                        return null;
                } else {
                        return ii.getImage();
                }
        }

        public boolean postProcess() {
                SIFMessage ret = uiPanel.postProcess();
                if (ret.getMessageType() == SIFMessage.OK) {
                        return true;
                } else if (ret.getMessage().equals(CANCELED_ACTION)) {
                        // no message, just cancel the action!
                        return false;
                } else {
                        JOptionPane.showMessageDialog(UIFactory.getMainFrame(), ret);
                        return false;
                }
        }

        public UIPanel getUIPanel() {
                return uiPanel;
        }

        public OutsideFrame getOutsideFrame() {
                return outsideFrame;
        }
        
        
        
        
}
