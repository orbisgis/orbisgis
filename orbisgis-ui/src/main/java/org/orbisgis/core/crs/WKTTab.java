package org.orbisgis.core.crs;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.Workspace;

public class WKTTab extends JPanel implements ActionListener {

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
                if (readPropertiesFile()) {
                        this.add(crsHistoryPanel, BorderLayout.SOUTH);
                }

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

        public boolean readPropertiesFile() {
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
                        Services.getErrorManager().error(
                                "Cannot save CRS history", ex);
                }
        }

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
