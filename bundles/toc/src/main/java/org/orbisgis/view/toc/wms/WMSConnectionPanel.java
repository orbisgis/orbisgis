/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.toc.wms;

import com.vividsolutions.wms.MapLayer;
import com.vividsolutions.wms.WMService;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.icons.TocIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class WMSConnectionPanel extends JPanel implements UIPanel {
    public static final int timeout = 5000;
    private static final I18n I18N = I18nFactory.getI18n(LayerConfigurationPanel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(WMSConnectionPanel.class);
    private static final String WMS_SERVER_FILE = "wmsServerList.txt";
    private WideComboBox<String> cmbURLServer;
    private JLabel lblVersion;
    private JLabel lblTitle;
    private JTextArea txtDescription;
    private MapLayer client;
    private WMService service;
    private ArrayList<String> serversWms;
    private LayerConfigurationPanel configPanel;
    private ExecutorService executorService;
    private File wmsFileList;

    /**
     * The WMSConnectionPanel is the first panel used to specify the URL of the
     * WMS server. It shows some informations on the server.
     *
     * @param configPanel
     */
    public WMSConnectionPanel(LayerConfigurationPanel configPanel, ExecutorService executorService, CoreWorkspace coreWorkspace) {
        this.configPanel = configPanel;
        this.executorService = executorService;
        wmsFileList = new File(coreWorkspace.getApplicationFolder() + File.separator + WMS_SERVER_FILE);
        JPanel pnlURL = new JPanel(new MigLayout());
       
        serversWms = loadWMSServers();
        cmbURLServer = new WideComboBox<>(serversWms.toArray(new String[serversWms.size()]));
        cmbURLServer.setEditable(true);
        pnlURL.add(cmbURLServer, "span 2");
        
        CustomButton btnConnect = new CustomButton(TocIcon.getIcon("server_connect"));
        btnConnect.setToolTipText(I18N.tr("Connect to the server."));
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });
        
        CustomButton btnDelete = new CustomButton(TocIcon.getIcon("remove"));
        btnDelete.setToolTipText(I18N.tr("Delete the server connection."));
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = cmbURLServer.getSelectedItem();
                if (serversWms.contains(item)) {
                    serversWms.remove(item);
                    saveWMSServerFile();
                }
                cmbURLServer.removeItem(item);
            }
        });

        CustomButton btnUpdate = new CustomButton(TocIcon.getIcon("refresh"));
        btnUpdate.setToolTipText(I18N.tr("Reload the server connection."));
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    ArrayList<String> updateServersList = readWMSServerFile(WMSConnectionPanel.class.getResourceAsStream(WMS_SERVER_FILE));

                    for (String updatewms : updateServersList) {
                        if (!serversWms.contains(updatewms)) {
                            serversWms.add(updatewms);
                            cmbURLServer.addItem(updatewms);
                        }
                    }
                    saveWMSServerFile();
                } catch (IOException e1) {
                    LOGGER.error(I18N.tr("Cannot update and save the URL list"), e1);
                }

            }
        });

        pnlURL.add(btnConnect);
        pnlURL.add(btnDelete);
        pnlURL.add(btnUpdate, "wrap");
        
        // Info panel        
        lblVersion = new JLabel(I18N.tr("Version :"));
        lblTitle = new JLabel(I18N.tr("Name :"));
        pnlURL.add(lblVersion, "wrap");
        pnlURL.add(lblTitle, "wrap");
        txtDescription = new JTextArea();
        txtDescription.setEditable(false);
        txtDescription.setLineWrap(true);
        JScrollPane comp = new JScrollPane(txtDescription);
        pnlURL.add(comp, "span 2, height 150!, grow");
        this.add(pnlURL);
    }

    /**
     * Load a list of servers stored in file in the current workspace if the
     * file doesn't exist a list of default URL is loaded.
     *
     * @return
     */
    private ArrayList<String> loadWMSServers() {
        // Create a temporary workspace to compute future path
        try {
            if (wmsFileList.exists()) {
                return readWMSServerFile(new FileInputStream(wmsFileList));
            } else {
                return readWMSServerFile(WMSConnectionPanel.class.getResourceAsStream(WMS_SERVER_FILE));
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
        ArrayList<String> serversList = new ArrayList<>();
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
            PrintWriter pw = new PrintWriter(wmsFileList);
            for (String server : serversWms) {
                pw.println(server);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            LOGGER.error(I18N.tr("Cannot save the list of WMS url"));
        }
    }

    /**
     * When the user click on the connect button a background job is started to
     * display some informations about the WMS server.
     */
    private void connect() {
        WmsConnectionJob wmsConnectionJob = new WmsConnectionJob();
        if(executorService != null) {
            executorService.execute(wmsConnectionJob);
        } else {
            wmsConnectionJob.execute();
        }
    }

    /**
     * Replace an empty string by a null.
     *
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
        if (service == null) {
            return I18N.tr("Please connect to the WMS server");
        }

        return null;
    }

    @Override
    public URL getIconURL() {
        return null;
    }

    /**
     * Gets the WMService instance that describes the WMS connection
     *
     * @return A WMService instance.
     */
    public WMService getServiceDescription() {
        return service;
    }

    /**
     * Return the current MapLayer.
     *
     * @return
     */
    public MapLayer getMapLayer() {
        return client;
    }

    /**
     * Return a MapLayer corresponding to a URL.
     *
     * @param host
     * @return
     * @throws ConnectException
     * @throws IOException
     */
    public MapLayer getMapLayer(String host) throws ConnectException,
            IOException {
        if (client == null) {
            WMService wmsClient = new WMService(host, WMService.WMS_1_1_1);
            client = wmsClient.getCapabilities().getTopLayer();
            return client;
        }
        return client;
    }

    /**
     * Return a supported image format if not return null.
     *
     * @param formats
     * @return
     */
    public String getFirstImageFormat(Vector<?> formats) {
        String[] preferredFormats = new String[]{"image/png", "image/jpeg",
            "image/gif", "image/tiff"};
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

    /**
     * Fetch information about the Wms service from the Internet
     */
    private class WmsConnectionJob extends SwingWorker {
        @Override
        public String toString() {
            return I18N.tr("Download information about the Wms service");
        }

        public void cancel() {
            cancel(true);
        }

        @Override
        protected Object doInBackground() throws Exception {
            String originalWmsURL = cmbURLServer.getSelectedItem();
            String wmsURL = originalWmsURL.trim();
            try {
                if (service == null) {
                    service = new WMService(wmsURL, WMService.WMS_1_1_1);
                } else {
                    if (!service.getServerUrl().equals(wmsURL)) {
                        service = new WMService(wmsURL, WMService.WMS_1_1_1);
                    }
                }
                service.initialize(false, timeout);
                client = service.getCapabilities().getTopLayer();
                configPanel.setClient(client);
                // client.getCapabilities(null, false, null);
                //This action populates the UI with all informations about the server.
                configPanel.initialize();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        lblVersion.setText(I18N.tr("Version :")
                                + changeNullForEmptyString(service.getVersion()));
                        lblTitle.setText(I18N.tr("Name :")
                                + changeNullForEmptyString(service.getTitle()));
                        txtDescription.setText(service.getTitle());
                        txtDescription.setCaretPosition(0);

                    }
                });

                if (!serversWms.contains(originalWmsURL)) {
                    serversWms.add(originalWmsURL);
                    saveWMSServerFile();
                }

            } catch (ConnectException e) {
                LOGGER.error(I18N.tr("Cannot connect to {0}", wmsURL), e);
            } catch (IOException e) {
                LOGGER.error(
                        I18N.tr("Cannot get capabilities of {0}", wmsURL), e);
            } catch (Throwable ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
        }
    }
}
