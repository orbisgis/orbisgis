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
package org.orbisgis.wkgui.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.framework.CoreWorkspaceImpl;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * View workspace contains file and folder information
 * that contains GUI related data.
 */

@Component
public class ViewWorkspaceImpl implements ViewWorkspace, CoreWorkspace {
    private static final long serialVersionUID = 1L;
    private static final I18n I18N = I18nFactory.getI18n(ViewWorkspaceImpl.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewWorkspaceImpl.class);
    /**
     * Buffer to copy resource to file
     */
    private static final int BUFFER_LENGTH = 4096;
    private PropertyChangeSupport propertySupport;
    private CoreWorkspaceImpl coreWorkspace;
    private Map<String, DataSourceFactory> dataSourceFactories = new HashMap<>();
    private static boolean alwaysStop = false;


    @Activate
    public void activate(BundleContext bc) throws BundleException {
        Version bundleVersion = bc.getBundle().getVersion();
        coreWorkspace = new CoreWorkspaceImpl(bundleVersion.getMajor(), bundleVersion.getMinor(),
                bundleVersion.getMicro(), bundleVersion.getQualifier(), new org.apache.felix
                .framework.Logger());
        propertySupport = new PropertyChangeSupport(this);
        if(alwaysStop || !showGUI()) {
            if(!alwaysStop) {
                bc.getBundle(0).stop();
            }
            alwaysStop = true;
            throw new BundleException("Canceled by user");
        } else {
            SIFPath = getWorkspaceFolder() + File.separator + "sif";
            mapContextPath = getWorkspaceFolder() + File.separator + "maps";
        }
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceFactories.put(serviceProperties.get(DataSourceFactory.OSGI_JDBC_DRIVER_NAME).toLowerCase(),
                dataSourceFactory);
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    public void removeDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceFactories.remove(serviceProperties.get(DataSourceFactory.OSGI_JDBC_DRIVER_NAME).toLowerCase());
    }

    private boolean showGUI() {
        {
            try {
                // Create a local DataSourceService to check connection properties
                DataSourceService dataSourceService = new DataSourceService();
                for(Map.Entry<String, DataSourceFactory> entry : dataSourceFactories.entrySet()) {
                    Map<String, String> properties = new HashMap<>();
                    properties.put(DataSourceFactory.OSGI_JDBC_DRIVER_NAME, entry.getKey());
                    dataSourceService.addDataSourceFactory(entry.getValue(), properties);
                }
                String errorMessage = "";
                boolean connectionValid = false;
                do {
                    if (WorkspaceSelectionDialog.showWorkspaceFolderSelection(null, coreWorkspace, errorMessage)) {
                        /////////////////////
                        // Check connection
                        dataSourceService.setCoreWorkspace(coreWorkspace);
                        try {
                            dataSourceService.activate();
                            try(Connection connection = dataSourceService.getConnection()) {
                                DatabaseMetaData meta = connection.getMetaData();
                                LOGGER.info(I18N.tr("Data source available {0} version {1}", meta
                                        .getDriverName(), meta.getDriverVersion()));
                                connectionValid = true;
                            }
                        } catch (SQLException ex) {
                            errorMessage = ex.getLocalizedMessage();
                            connectionValid = false;
                        }
                    } else {
                        // User cancel, stop OrbisGIS
                        return false;
                    }
                    if(connectionValid) {
                        return true;
                    }
                } while (!connectionValid);
            } catch (Exception ex) {
                LOGGER.error("Could not init workspace", ex);
            }
        }
        return false;
    }

    private static final String DEFAULT_DOCKING_LAYOUT_FILE = "docking_layout.xml";
    private String dockingLayoutFile = DEFAULT_DOCKING_LAYOUT_FILE;
    private String SIFPath = "";
    private String mapContextPath;

    @Override
    public String getMapContextPath() {
        return mapContextPath;
    }

    @Override
    public CoreWorkspace getCoreWorkspace() {
        return coreWorkspace;
    }

    /**
     * Set the value of mapContextPath
     *
     * @param mapContextPath new value of mapContextPath
     */
    public void setMapContextPath(String mapContextPath) {
        String oldMapContextPath = this.mapContextPath;
        this.mapContextPath = mapContextPath;
        propertySupport.firePropertyChange(PROP_MAPCONTEXTPATH, oldMapContextPath, mapContextPath);
    }

    @Override
    public String getSIFPath() {
        return SIFPath;
    }

    /**
     * Set the value of SIFPath
     *
     * @param SIFPath new value of SIFPath
     */
    public void setSIFPath(String SIFPath) {
        String oldSIFPath = this.SIFPath;
        this.SIFPath = SIFPath;
        propertySupport.firePropertyChange(PROP_SIFPATH, oldSIFPath, SIFPath);
    }

    @Override
    public String getDockingLayoutFile() {
        return dockingLayoutFile;
    }

    @Override
    public String getDockingLayoutPath() {
        return coreWorkspace.getWorkspaceFolder() + File.separator + dockingLayoutFile;
    }

    /**
     * Set the value of dockingLayoutFile
     * The docking layout file contain the panels layout and editors opened in the last OrbisGis instance
     *
     * @param dockingLayoutFile new value of dockingLayoutFile
     */
    public void setDockingLayoutFile(String dockingLayoutFile) {
        String oldDockingLayoutFile = this.dockingLayoutFile;
        this.dockingLayoutFile = dockingLayoutFile;
        propertySupport.firePropertyChange(PROP_DOCKINGLAYOUTFILE, oldDockingLayoutFile, dockingLayoutFile);
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(prop, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(prop, listener);
    }

    @Override
    public int getVersionMajor() {
        return coreWorkspace.getVersionMajor();
    }

    @Override
    public int getVersionMinor() {
        return coreWorkspace.getVersionMinor();
    }

    @Override
    public int getVersionRevision() {
        return coreWorkspace.getVersionRevision();
    }

    @Override
    public String getVersionQualifier() {
        return coreWorkspace.getVersionQualifier();
    }

    /**
     * Create minimal resource inside an empty workspace folder
     *
     * @param workspaceFolder
     * @throws IOException Error while writing files or the folder is not empty
     */
    public static void initWorkspaceFolder(File workspaceFolder, int version_major, int version_minor, int
            version_revision, String version_qualifier) throws IOException {
        CoreWorkspaceImpl.initWorkspaceFolder(workspaceFolder, version_major, version_minor, version_revision,
                version_qualifier);
        // Copy default window docking style
        try (InputStream xmlFileStream = ViewWorkspaceImpl.class.getResourceAsStream("default_docking_layout.xml")) {
            if (xmlFileStream != null) {
                try (FileOutputStream writer = new FileOutputStream(workspaceFolder + File.separator +
                        DEFAULT_DOCKING_LAYOUT_FILE)) {
                    byte[] buffer = new byte[BUFFER_LENGTH];
                    for (int n; (n = xmlFileStream.read(buffer)) != -1; ) {
                        writer.write(buffer, 0, n);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Unable to save the docking layout."), ex);
        }
    }

    /**
     * Check if the provided folder can be loaded has the workspace
     *
     * @param workspaceFolder
     * @return True if valid
     */
    public static boolean isWorkspaceValid(File workspaceFolder, int majorVersion) {
        // not exist or empty
        // contain the version file with same major version
        if (!workspaceFolder.exists()) {
            return true;
        }
        if (!workspaceFolder.isDirectory()) {
            return false;
        }
        File[] files = workspaceFolder.listFiles();
        if (files == null || files.length == 0) {
            return true;
        }
        File versionFile = new File(workspaceFolder, CoreWorkspaceImpl.VERSION_FILE);
        if (!versionFile.exists()) {
            return false;
        }
        // Read the version file of the workspace folder
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new FileReader(versionFile));
            String line = fileReader.readLine();
            if (line != null) {
                return Integer.valueOf(line).equals(majorVersion);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read the workspace location", e);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                // Ignore
            }
        }
        return false;
    }

    @Override
    public String getJDBCConnectionReference() {
        return coreWorkspace.getJDBCConnectionReference();
    }

    @Override
    public String getDataBaseUriFilePath() {
        return coreWorkspace.getDataBaseUriFilePath();
    }

    @Override
    public String getDataBaseUser() {
        return coreWorkspace.getDataBaseUser();
    }

    @Override
    public String getDataBasePassword() {
        return coreWorkspace.getDataBasePassword();
    }

    @Override
    public void setDataBaseUser(String user) {
        coreWorkspace.setDataBaseUser(user);
    }

    @Override
    public void setDataBasePassword(String password) {
        coreWorkspace.setDataBasePassword(password);
    }

    @Override
    public boolean isRequirePassword() {
        return coreWorkspace.isRequirePassword();
    }

    @Override
    public void setRequirePassword(boolean requirePassword) {
        coreWorkspace.setRequirePassword(requirePassword);
    }

    @Override
    public String getPluginCache() {
        return coreWorkspace.getPluginCache();
    }

    @Override
    public String getLogFile() {
        return coreWorkspace.getLogFile();
    }

    @Override
    public String getLogPath() {
        return coreWorkspace.getLogPath();
    }

    @Override
    public List<File> readKnownWorkspacesPath() {
        return coreWorkspace.readKnownWorkspacesPath();
    }

    @Override
    public File readDefaultWorkspacePath() {
        return coreWorkspace.readDefaultWorkspacePath();
    }

    @Override
    public String getApplicationFolder() {
        return coreWorkspace.getApplicationFolder();
    }

    @Override
    public String getTempFolder() {
        return coreWorkspace.getTempFolder();
    }

    @Override
    public String getPluginFolder() {
        return coreWorkspace.getPluginFolder();
    }

    @Override
    public String getSourceFolder() {
        return coreWorkspace.getSourceFolder();
    }

    @Override
    public String getWorkspaceFolder() {
        return coreWorkspace.getWorkspaceFolder();
    }
}
