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
package org.orbisgis.view.toc.wms;

import com.vividsolutions.wms.MapLayer;
import com.vividsolutions.wms.WMService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspaceImpl;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.toc.icons.TocIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class WMSConnectionPanel extends JPanel implements UIPanel {

    private static final I18n I18N = I18nFactory.getI18n(LayerConfigurationPanel.class);
    private static final Logger LOGGER = Logger.getLogger(Catalog.class);
    private static final String WMS_SERVER_FILE = "wmsServerList.txt";
    private WideComboBox cmbURLServer;
    private JLabel lblVersion;
    private JLabel lblTitle;
    private JTextArea txtDescription;
    private MapLayer client;
    private WMService service;
    private ArrayList<String> serverswms;
    private LayerConfigurationPanel configPanel;

    /**
     * The WMSConnectionPanel is the first panel used to specify the URL of the
     * WMS server. It shows some informations on the server.
     *
     * @param configPanel
     */
    public WMSConnectionPanel(LayerConfigurationPanel configPanel) {
        this.configPanel = configPanel;
        JPanel pnlURL = new JPanel(new MigLayout());
       
        serverswms = loadWMSServers();
        cmbURLServer = new WideComboBox(serverswms.toArray(new String[serverswms.size()]));
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
                String item = cmbURLServer.getSelectedItem().toString();
                if (serverswms.contains(item)) {
                    serverswms.remove(item);
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

    private File getWMSFileListPath() {
        CoreWorkspaceImpl tempWorkspace = new CoreWorkspaceImpl();
        return new File(tempWorkspace.getApplicationFolder() + File.separator + WMS_SERVER_FILE);
    }

    /**
     * Load a list of servers stored in file in the current workspace if the
     * file doesn't exist a list of default URL is loaded.
     *
     * @return
     */
    private ArrayList<String> loadWMSServers() {
        // Create a temporary workspace to compute future path
        File file = getWMSFileListPath();
        try {
            if (file.exists()) {
                return readWMSServerFile(new FileInputStream(file));
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
            File file = getWMSFileListPath();
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
     * When the user click on the connect button a background job is started to
     * display some informations about the WMS server.
     */
    private void connect() {
        BackgroundManager bm = Services.getService(BackgroundManager.class);
        bm.nonBlockingBackgroundOperation(new WmsConnectionJob());

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
    private class WmsConnectionJob implements BackgroundJob {

        @Override
        public void run(ProgressMonitor pm) {
            String originalWmsURL = cmbURLServer.getSelectedItem().toString();
            String wmsURL = originalWmsURL.trim();
            try {
                if (service == null) {
                    service = new WMService(wmsURL, WMService.WMS_1_1_1);
                } else {
                    if (!service.getServerUrl().equals(wmsURL)) {
                        service = new WMService(wmsURL, WMService.WMS_1_1_1);
                    }
                }
                service.initialize();
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

                if (!serverswms.contains(originalWmsURL)) {
                    serverswms.add(originalWmsURL);
                    saveWMSServerFile();
                }

            } catch (ConnectException e) {
                LOGGER.error(I18N.tr("Cannot connect to {0}", wmsURL), e);
            } catch (IOException e) {
                LOGGER.error(
                        I18N.tr("Cannot get capabilities of {0}", wmsURL), e);
            }
        }

        @Override
        public String getTaskName() {
            return I18N.tr("Connecting to the server...");
        }
    }
}