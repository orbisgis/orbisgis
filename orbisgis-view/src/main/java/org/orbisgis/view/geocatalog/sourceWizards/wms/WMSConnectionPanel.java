/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.wms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gvsig.remoteClient.wms.WMSClient;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * 
 * @author Erwan Bocher
 */
public class WMSConnectionPanel extends JPanel implements UIPanel {

        private static final I18n I18N = I18nFactory.getI18n(LayerConfigurationPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private static final String WMSServerFile = "wmsServerList.txt";
        private JComboBox cmbURLServer;
        private JLabel lblVersion;
        private JLabel lblTitle;
        private JTextArea txtDescription;
        private WMSClient client;
        private ArrayList<String> serverswms;
        private LayerConfigurationPanel configPanel;

        /**
         * The WMSConnectionPanel is the first panel used to specify the URL of the WMS server.
         * It shows some informations on the server.
         * @param configPanel 
         */
        public WMSConnectionPanel(LayerConfigurationPanel configPanel) {
                GridBagLayout gl = new GridBagLayout();
                this.setLayout(gl);
                GridBagConstraints c = new GridBagConstraints();
                this.configPanel = configPanel;

                // Connection panel
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 1;
                c.weighty = 0;
                JPanel pnlURL = new JPanel(new BorderLayout());
                pnlURL.setBorder(BorderFactory.createTitledBorder(I18N.tr("WMS server URL")));

                serverswms = loadWMSServers();
                cmbURLServer = new JComboBox(serverswms.toArray(new String[serverswms.size()]));
                cmbURLServer.setEditable(true);
                cmbURLServer.setMaximumSize(new Dimension(100, 20));

                pnlURL.add(cmbURLServer, BorderLayout.NORTH);
                JToolBar wmsBtnManager = new JToolBar();
                wmsBtnManager.setFloatable(false);
                wmsBtnManager.setOpaque(false);

                JButton btnConnect = new JButton(OrbisGISIcon.getIcon("server_connect"));
                btnConnect.setToolTipText(I18N.tr("Connect to the server."));
                btnConnect.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                connect();
                        }
                });
                btnConnect.setBorderPainted(false);

                JButton btnDelete = new JButton(OrbisGISIcon.getIcon("remove"));
                btnDelete.setToolTipText(I18N.tr("Delete the server connection."));
                btnDelete.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                String item = cmbURLServer.getSelectedItem().toString();
                                if (serverswms.contains(item)) {
                                        serverswms.remove(item);
                                        saveWMSServerFile();
                                }
                                cmbURLServer.removeItem(item);
                        }
                });
                btnDelete.setBorderPainted(false);

                JButton btnUpdate = new JButton(OrbisGISIcon.getIcon("arrow_refresh"));
                btnUpdate.setToolTipText(I18N.tr("Reload the server connection."));
                btnUpdate.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                                try {
                                        ArrayList<String> updateServersList = readWMSServerFile(WMSConnectionPanel.class.getResourceAsStream(WMSServerFile));

                                        for (String updatewms : updateServersList) {
                                                if (!serverswms.contains(updatewms)) {
                                                        serverswms.add(updatewms);
                                                        cmbURLServer.addItem(updatewms);
                                                }
                                        }
                                        saveWMSServerFile();
                                } catch (IOException e1) {
                                        LOGGER.error(I18N.tr("Cannot update and save the URL list"), e1);
                                }

                        }
                });
                btnUpdate.setBorderPainted(false);

                wmsBtnManager.add(btnConnect);
                wmsBtnManager.add(btnDelete);
                wmsBtnManager.add(btnUpdate);
                pnlURL.add(wmsBtnManager, BorderLayout.SOUTH);
                this.add(pnlURL, c);

                // Info panel
                c.fill = GridBagConstraints.BOTH;
                c.gridy = 1;
                c.weightx = 1;
                c.weighty = 1;
                JPanel pnlInfo = new JPanel();
                pnlInfo.setLayout(new BorderLayout());
                pnlInfo.setBorder(BorderFactory.createTitledBorder(I18N.tr("Information")));
                JPanel pnlNorth = new JPanel();
                pnlNorth.setLayout(new CRFlowLayout());
                lblVersion = new JLabel(I18N.tr("Version :"));
                lblTitle = new JLabel(I18N.tr("Nom :"));
                pnlNorth.add(lblVersion);
                pnlNorth.add(new CarriageReturn());
                pnlNorth.add(lblTitle);
                pnlInfo.add(pnlNorth, BorderLayout.NORTH);
                txtDescription = new JTextArea("\n\n\n\n\n\n");
                txtDescription.setEditable(false);
                txtDescription.setLineWrap(true);
                JScrollPane comp = new JScrollPane(txtDescription);
                pnlInfo.add(comp, BorderLayout.CENTER);
                this.add(pnlInfo, c);
        }

        /**
         * Load a list of servers stored in file in the current workspace if the
         * file doesn't exist a list of default URL is loaded.
         *
         * @return
         */
        private ArrayList<String> loadWMSServers() {
                CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                File file = new File(ws.getWorkspaceFolder() + File.separator + WMSServerFile);
                try {
                        if (file.exists()) {
                                return readWMSServerFile(new FileInputStream(file));
                        } else {
                                return readWMSServerFile(WMSConnectionPanel.class.getResourceAsStream(WMSServerFile));
                        }
                } catch (IOException e) {
                         LOGGER.error(I18N.tr("Cannot load the list of WMS url"), e);
                }

                return null;
        }

        /**
         * Read the wms servers file list to populate the combobox
         *
         * @param layoutStream
         * @return
         * @throws IOException
         */
        private ArrayList<String> readWMSServerFile(InputStream layoutStream)
                throws IOException {
                ArrayList<String> serversList = new ArrayList<String>();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(layoutStream));

                String str;
                while ((str = bufferedReader.readLine()) != null) {
                        if (str.length() > 0) {
                                if (!serversList.contains(str)) {
                                        serversList.add(str);
                                }
                        }
                }
                bufferedReader.close();
                layoutStream.close();

                return serversList;
        }

        /**
         * A method to save the list of WMS url in a file located in the current
         * OrbisGIS workspace.
         */
        public void saveWMSServerFile() {
                try {
                        CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                        File file = new File(ws.getWorkspaceFolder() + File.separator + WMSServerFile);
                        PrintWriter pw = new PrintWriter(file);
                        for (String server : serverswms) {
                                pw.println(server);
                        }
                        pw.close();
                } catch (FileNotFoundException e) {
                        LOGGER.error(I18N.tr("Cannot save the list of WMS url"));
                }
        }

        /**
         * When the user click on the connect button a background job is started
         * to display some informations about the WMS server.
         */
        private void connect() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new BackgroundJob() {

                        @Override
                        public void run(ProgressMonitor pm) {
                                String originalWmsURL = cmbURLServer.getSelectedItem().toString();
                                String wmsURL = originalWmsURL.trim();
                                try {
                                        if (client == null) {
                                                client = getWMSClient(wmsURL);
                                        } else {
                                                if (!client.getHost().equals(wmsURL)) {
                                                        client = getWMSClient(wmsURL);
                                                }
                                        }
                                        configPanel.setClient(client);                                        
                                        client.getCapabilities(null, false, null);
                                        //This action populates the UI with all informations about the server.
                                        configPanel.initialize();
                                        SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                        lblVersion.setText(I18N.tr("Version :")
                                                                + changeNullForEmptyString(client.getVersion()));
                                                        lblTitle.setText(I18N.tr("Nom :")
                                                                + changeNullForEmptyString(client.getServiceName()));
                                                        txtDescription.setText(client.getServiceInformation().abstr);
                                                        txtDescription.setCaretPosition(0);

                                                }
                                        });

                                        if (!serverswms.contains(originalWmsURL)) {
                                                serverswms.add(originalWmsURL);
                                                saveWMSServerFile();
                                        }

                                } catch (ConnectException e) {
                                        LOGGER.error(I18N.tr("orbisgis.errorMessages.wms.CannotConnect"
                                                + " " + wmsURL), e);
                                } catch (IOException e) {
                                        LOGGER.error(
                                                I18N.tr("orbisgis.errorMessages.wms.CannotGetCapabilities"
                                                + " " + wmsURL), e);
                                }
                        }

                        @Override
                        public String getTaskName() {
                                return I18N.tr("Connecting to the server...");
                        }
                });
        }

        /**
         * Replace an empty string by a null.
         * @param property
         * @return 
         */
        private String changeNullForEmptyString(String property) {
                if (property == null) {
                        return "";
                } else {
                        return property;
                }
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getTitle() {
                return I18N.tr("WMS server connection");
        }

        @Override
        public String validateInput() {
                if (client == null) {
                        return I18N.tr("Please connect to the WMS server");
                }

                return null;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        /**
         * Return the current WMSClient.
         * @return 
         */
        public WMSClient getWMSClient() {
                return client;
        }

        /**
         * Return a WMSClient corresponding to a URL.
         *
         * @param host
         * @return
         * @throws ConnectException
         * @throws IOException
         */
        public WMSClient getWMSClient(String host) throws ConnectException,
                IOException {
                if (client == null) {
                        client = new WMSClient(host);
                        client.getCapabilities(null, true, null);
                        return client;
                }
                return client;
        }
        
        /**
         * Return a supported image format if not return null.
         * @param formats
         * @return 
         */
        public String getFirstImageFormat(Vector<?> formats) {
		String[] preferredFormats = new String[] { "image/png", "image/jpeg",
				"image/gif", "image/tiff" };
		for (int i = 0; i < preferredFormats.length; i++) {
			if (formats.contains(preferredFormats[i])) {
				return preferredFormats[i];
			}
		}

		for (Object object : formats) {
			String format = object.toString();
			if (format.startsWith("image/")) {
				return format;
			}
		}

		return null;
	}
}
