/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.wms;

import com.vividsolutions.wms.MapLayer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.components.button.JButtonTextField;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class SRSPanel extends JPanel implements UIPanel {

        private static final I18n I18N = I18nFactory.getI18n(SRSPanel.class);
        private JList lstSRS;
        private JButtonTextField txtFilter;
        private SRSListModel SRSlistModel;
        private JPanel searchPanel;
        private Component scrollPane;

        /**
         * The SRSPanel lists all available SRS.
         *
         */
        public SRSPanel() {
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getTitle() {
                return I18N.tr("Select a SRS");
        }

        /**
         * Create the SRSPanel with a populated list of supported SRS.
         */
        private void initialize() {
                this.setLayout(new BorderLayout());
                if (null == scrollPane) {
                        scrollPane = new JScrollPane(lstSRS);
                }
                this.add(scrollPane, BorderLayout.CENTER);
                this.add(getSearchSRSPanel(), BorderLayout.NORTH);
        }

        /**
         * Create the JList that stores all supported SRS.
         *
         * @return
         */
        public void createSRSList(MapLayer wMSClient) {
                if (null == lstSRS) {
                        lstSRS = new JList();
                        Collection allSrs = wMSClient.getFullSRSList();
                        String[] srsNames = new String[allSrs.size()];
                        allSrs.toArray(srsNames);
                        SRSlistModel = new SRSListModel(srsNames);
                        lstSRS.setModel(SRSlistModel);
                        lstSRS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        initialize();
                }
        }

        /**
         * Create the search panel that permits to filter a SRS.
         *
         * @return
         */
        public JPanel getSearchSRSPanel() {
                if (null == searchPanel) {
                        searchPanel = new JPanel();
                        JLabel label = new JLabel(I18N.tr("Search a SRS :"));

                        txtFilter = new JButtonTextField();
                        txtFilter.getDocument().addDocumentListener(new DocumentListener() {

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                        doFilter();
                                }

                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                        doFilter();
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                        doFilter();
                                }
                        });
                        searchPanel.add(label);
                        searchPanel.add(txtFilter);
                }
                return searchPanel;

        }

        /**
         * Apply the filter when the user set a text to the search panel.
         */
        private void doFilter() {
                SRSlistModel.filter(txtFilter.getText());
        }

        @Override
        public String validateInput() {
                if (lstSRS.getSelectedIndex() == -1) {
                        return I18N.tr("Please select a SRS.");
                }
                return null;
        }

        /**
         * Get the selected SRS available in the list of SRS.
         *
         * @return
         */
        public String getSRS() {
                return lstSRS.getSelectedValue().toString();
        }
}
