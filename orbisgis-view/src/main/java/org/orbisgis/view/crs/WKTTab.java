/**
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
package org.orbisgis.view.crs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class WKTTab extends JPanel implements ActionListener {

        private static final Logger LOGGER = Logger.getLogger(WKTTab.class);
        private static final I18n I18N = I18nFactory.getI18n(WKTTab.class);
        private JTextArea jTextArea;
        public static final String NO_SRS_INFO = "MapContext contains no CRS text information";
        public String crsHistory = "crsHistory.properties";
        private JPanel crsHistoryPanel;
        public LinkedList<CRSValue> crsHistoryMap = new LinkedList<CRSValue>();
        int MAX_CRS = 3;
        private final JTable crsTable;

        public WKTTab(JTable crsTable) {
                this.crsTable = crsTable;
                init();
        }

        private void init() {
                this.setLayout(new BorderLayout());
                jTextArea = new JTextArea();
                jTextArea.setText(NO_SRS_INFO);
                jTextArea.setLineWrap(true);
                JScrollPane scrollDriver = new JScrollPane(jTextArea);
                scrollDriver.setPreferredSize(new Dimension(300, 70));
                this.add(scrollDriver, BorderLayout.CENTER);
               /* if (readPropertiesFile()) {
                        this.add(crsHistoryPanel, BorderLayout.SOUTH);
                }*/

        }

        public void setWKT(String driverWKT) {
                if (driverWKT == null) {
                        driverWKT = NO_SRS_INFO;
                }
                jTextArea.setText(driverWKT);
        }

        public JPanel createCRSHistory() {
                if (crsHistoryPanel == null) {
                        crsHistoryPanel = new JPanel(new BorderLayout());
                        JButton crsButton1 = new JButton(crsHistoryMap.get(2).toString());
                        crsButton1.setActionCommand("FIRST_CRS");
                        crsButton1.addActionListener(this);
                        JButton crsButton2 = new JButton(crsHistoryMap.get(1).toString());
                        JButton crsButton3 = new JButton(crsHistoryMap.get(0).toString());
                        crsHistoryPanel.add(crsButton1, BorderLayout.NORTH);
                        crsHistoryPanel.add(crsButton2, BorderLayout.CENTER);
                        crsHistoryPanel.add(crsButton3, BorderLayout.SOUTH);
                }

                return crsHistoryPanel;
        }

        /*public boolean readPropertiesFile() {
                Workspace workspace = Services.getService(Workspace.class);
                File file = workspace.getFile(crsHistory);
                if (file.exists()) {
                        Properties properties = new Properties();
                        try {
                                properties.load(new FileInputStream(file));
                                Iterator it = properties.keySet().iterator();
                                while (it.hasNext()) {
                                        String key = (String) it.next();
                                        String valeur = properties.getProperty(key);
                                        crsHistoryMap.add(new CRSValue(key, valeur));
                                }
                                createCRSHistory();
                                return true;
                        } catch (IOException e) {
                                return false;
                        }
                }
                return false;
        }

        void saveCRSHistory(String[] crsKeyValue) {
                Workspace workspace = Services.getService(Workspace.class);
                File file = workspace.getFile(crsHistory);
                if (crsHistoryMap.size() < MAX_CRS) {
                        crsHistoryMap.addFirst(new CRSValue(crsKeyValue[0], crsKeyValue[1]));
                } else {
                        crsHistoryMap.removeLast();
                        crsHistoryMap.addFirst(new CRSValue(crsKeyValue[0], crsKeyValue[1]));
                }

                Properties properties = new Properties();
                for (CRSValue crsv : crsHistoryMap) {
                        properties.put(crsv.getRegister(), crsv.getCrsName());
                }

                try {
                        properties.store(new FileOutputStream(file), "");
                } catch (IOException ex) {
                        LOGGER.error(I18N.tr(
                                "Cannot save CRS history"), ex);
                }
        }*/

        @Override
        public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("FIRST_CRS".equals(command)) {
                        //Return the selected row and display it
                } else if ("SECOND_CRS".equals(command)) {
                } else if ("LAST_CRS".equals(command)) {
                }
        }

        private static class CRSValue {

                private final String register;
                private final String crsName;

                public CRSValue(String register, String crsName) {
                        this.register = register;
                        this.crsName = crsName;
                }

                public String getCrsName() {
                        return crsName;
                }

                public String getRegister() {
                        return register;
                }

                @Override
                public String toString() {
                        return crsName + " (" + register + ")";
                }
        }
}
