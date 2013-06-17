/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.workspace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Core Worskpace Folder information
 * 
 * See documentation related to java.beans management systems
 * 
 */

public class CoreWorkspace implements Serializable {
        private static final Logger LOGGER = Logger.getLogger(CoreWorkspace.class);
        private static final long serialVersionUID = 6L; /*<! Update this integer while adding properties (1 for each new property)*/

        public static final int MAJOR_VERSION = 4; // Load a workspace only if the major version is equal
        public static final int MINOR_VERSION = 0; // increment on new features
        public static final int REVISION_VERSION = 1; // increment on fix
        private PropertyChangeSupport propertySupport;
        private String applicationFolder = new File(System.getProperty("user.home"))
                .getAbsolutePath() + File.separator + ".OrbisGIS" + File.separator
                + MAJOR_VERSION + "." + MINOR_VERSION;
        public static final String PROP_APPLICATIONFOLDER = "applicationFolder";
        private String workspaceFolder;
        public static final String PROP_WORKSPACEFOLDER = "workspaceFolder";
        private String resultsFolder;
        public static final String PROP_RESULTSFOLDER = "resultsFolder";
        private String sourceFolder;
        public static final String PROP_SOURCEFOLDER = "sourceFolder";
        private String pluginFolder = "plugins";
        public static final String PROP_PLUGINFOLDER = "pluginFolder";
        private String tempFolder;
        private String pluginCache = "cache";
        public static final String PROP_PLUGINCACHE = "pluginCache";
        private String logFile = "orbisgis.log";
        public static final String PROP_LOGFILE = "logFile";
        private static final String CURRENT_WORKSPACE_FILENAME = "currentWorkspace.txt";
        private static final String ALL_WORKSPACE_FILENAME = "workspaces.txt";
        private String dataBaseUriFile = "database.uri";
        public static final String PROP_DATA_BASE_URI_FILE = "dataBaseUriFile";

        /**
         * bean constructor
         */
        public CoreWorkspace() {
                propertySupport = new PropertyChangeSupport(this);

                //Read default workspace
                loadCurrentWorkSpace();
        }
        private String getDefaultJDBCConnectionString() {
            return new StringBuilder().append("jdbc:h2:").append(getWorkspaceFolder()).append(File.separator).append("database").toString();
        }
        /**
         * Read the file located at {@link #getDataBaseUriFilePath()}
         * @return Content of the workspace database uri
         */
        public String getJDBCConnectionReference() {
            String uriFile = getDataBaseUriFilePath();
            if(uriFile==null) {
                return getDefaultJDBCConnectionString();
            }
            File dbUriFile = new File(uriFile);
            if(dbUriFile.exists()) {
                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader(dbUriFile));
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        return line;
                    }
                } catch (IOException ex) {
                    LOGGER.error("Could not read the DataBase URI from workspace",ex);
                }
            }
            return getDefaultJDBCConnectionString();
        }
        /**
         * Get the value of dataBaseUriFile
         *
         * @return the value of dataBaseUriFile or null if workspaceFolder is not defined
         */
        public String getDataBaseUriFilePath() {
                if(workspaceFolder==null) {
                    return null;
                }
                return workspaceFolder + File.separator + dataBaseUriFile;
        }

        /**
         * Set the value of dataBaseUriFile
         *
         * @param dataBaseUriFile new value of dataBaseUriFile
         */
        public void setDataBaseUriFilePath(String dataBaseUriFile) {
                String olddataBaseUriFile = this.dataBaseUriFile;
                this.dataBaseUriFile = dataBaseUriFile;
                propertySupport.firePropertyChange(PROP_DATA_BASE_URI_FILE, olddataBaseUriFile, dataBaseUriFile);
        }

        /**
         * Get the value of pluginCache
         *
         * @return the value of pluginCache
         */
        public String getPluginCache() {
                return applicationFolder + File.separator + pluginCache;
        }

        /**
         * Set the value of pluginCache
         *
         * @param pluginCache new value of pluginCache
         */
        public void setPluginCache(String pluginCache) {
                String oldPluginCache = this.pluginCache;
                this.pluginCache = pluginCache;
                propertySupport.firePropertyChange(PROP_PLUGINCACHE, oldPluginCache, pluginCache);
        }

        /**
         * Get the value of logFile
         *
         * @return the value of logFile
         */
        public String getLogFile() {
                return logFile;
        }

        /**
         * @return The full path of the log file
         */
        public String getLogPath() {
                return applicationFolder + File.separator + logFile;
        }

        /**
         * Set the value of logFile
         *
         * @param logFile new value of logFile
         */
        public void setLogFile(String logFile) {
                String oldLogFile = this.logFile;
                this.logFile = logFile;
                propertySupport.firePropertyChange(PROP_LOGFILE, oldLogFile, logFile);
        }

        public void writeKnownWorkspaces(List<File> paths) throws IOException {
                File appFolder = new File(applicationFolder + File.separator);
                File currentWK = new File(appFolder, ALL_WORKSPACE_FILENAME);
                // Create folder
                if(!appFolder.exists()) {
                    if(!appFolder.mkdirs() && !appFolder.exists()) {
                        throw new IOException("Unable to create folders.");
                    }
                }
                BufferedWriter writer = null;
                try {
                        writer = new BufferedWriter(new FileWriter(currentWK));
                        for (File path : paths) {
                                writer.write(path.getAbsolutePath());
                                writer.newLine();
                        }
                } finally {
                        if (writer != null) {
                                writer.close();
                        }
                }
        }

        /**
         * Read the workspace path list
         *
         * @return
         */
        public List<File> readKnownWorkspacesPath() {
                List<File> knownPath = new ArrayList<File>();

                File currentWK = new File(applicationFolder + File.separator + ALL_WORKSPACE_FILENAME);
                if (currentWK.exists()) {
                        BufferedReader fileReader = null;
                        try {
                                fileReader = new BufferedReader(new FileReader(
                                        currentWK));
                                String line;
                                while ((line = fileReader.readLine()) != null) {
                                        File currentDir = new File(line);
                                        if (currentDir.exists()) {
                                                knownPath.add(currentDir);
                                        }
                                }
                        } catch (IOException e) {
                                LOGGER.warn("Cannot read the workspace location " + currentWK + " .", e);
                        } finally {
                                try {
                                        if (fileReader != null) {
                                                fileReader.close();
                                        }
                                } catch (IOException e) {
                                        LOGGER.warn("Cannot close the file at location" + currentWK + " .", e);
                                }
                        }
                }
                if (knownPath.isEmpty() && workspaceFolder != null) {
                        knownPath.add(new File(workspaceFolder));
                }
                if (knownPath.isEmpty()) {
                        knownPath.add(new File(System.getProperty("user.home"), "OrbisGIS" + File.separator));
                }
                return knownPath;
        }

        /**
         * Clear or set the default workspace path
         *
         * @param path The path or null to clear it
         * @throws IOException
         */
        public void setDefaultWorkspace(File path) throws IOException {
                File currentWK = new File(applicationFolder + File.separator + CURRENT_WORKSPACE_FILENAME);
                if (path == null) {
                        if (currentWK.exists()) {
                                currentWK.delete();
                        }
                        return;
                }
                File fApp = new File(applicationFolder);
                if (!fApp.exists()) {
                        fApp.mkdirs();
                }
                BufferedWriter writer = null;
                try {
                        writer = new BufferedWriter(new FileWriter(currentWK));
                        writer.write(path.getAbsolutePath());
                } finally {
                        if (writer != null) {
                                writer.close();
                        }
                }
        }

        /**
         *
         * @return The default workspace folder or null if there is no default
         * workspace
         */
        public File readDefaultWorkspacePath() {

                File currentWK = new File(applicationFolder + File.separator + CURRENT_WORKSPACE_FILENAME);
                if (currentWK.exists()) {
                        String currentDir;
                        BufferedReader fileReader = null;
                        try {
                                fileReader = new BufferedReader(new FileReader(
                                        currentWK));
                                currentDir = fileReader.readLine();
                        } catch (IOException e) {
                                throw new RuntimeException("Cannot read the workspace location" + currentWK + " .", e);
                        } finally {
                                try {
                                        if (fileReader != null) {
                                                fileReader.close();
                                        }
                                } catch (IOException e) {
                                        throw new RuntimeException("Cannot close the file at location" + currentWK + " .", e);
                                }
                        }
                        return new File(currentDir);
                } else {
                        return null;
                }
        }

        /**
         * At startup, load application configuration
         */
        private void loadCurrentWorkSpace() {
                resultsFolder = "results";
                sourceFolder = "sources";
                tempFolder = "temp";
        }

        /**
         * Get the value of applicationFolder
         *
         * @return the value of applicationFolder
         */
        public String getApplicationFolder() {
                return applicationFolder;
        }

        /**
         * Set the value of applicationFolder
         *
         * @param applicationFolder new value of applicationFolder
         */
        public void setApplicationFolder(String applicationFolder) {
                String oldApplicationFolder = this.applicationFolder;
                this.applicationFolder = applicationFolder;
                propertySupport.firePropertyChange(PROP_APPLICATIONFOLDER, oldApplicationFolder, applicationFolder);
        }

        /**
         * Get the value of tempFolder
         *
         * @return the value of tempFolder
         */
        public String getTempFolder() {
                return workspaceFolder + File.separator + tempFolder;
        }

        /**
         * Set the value of tempFolder
         *
         * @param tempFolder new value of tempFolder
         */
        public void setTempFolder(String tempFolder) {
                this.tempFolder = tempFolder;
        }

        /**
         * Get the value of pluginFolder
         *
         * @return the value of pluginFolder
         */
        public String getPluginFolder() {
                return applicationFolder + File.separator + pluginFolder;
        }

        /**
         * Set the value of pluginFolder
         *
         * @param pluginFolder new value of pluginFolder
         */
        public void setPluginFolder(String pluginFolder) {
                String oldPluginFolder = this.pluginFolder;
                this.pluginFolder = pluginFolder;
                propertySupport.firePropertyChange(PROP_PLUGINFOLDER, oldPluginFolder, pluginFolder);
        }

        /**
         * Get the value of sourceFolder
         *
         * @return the value of sourceFolder
         */
        public String getSourceFolder() {
                return workspaceFolder + File.separator + sourceFolder;
        }

        /**
         * Set the value of sourceFolder
         *
         * @param sourceFolder new value of sourceFolder
         */
        public void setSourceFolder(String sourceFolder) {
                String oldSourceFolder = this.sourceFolder;
                this.sourceFolder = sourceFolder;
                propertySupport.firePropertyChange(PROP_SOURCEFOLDER, oldSourceFolder, sourceFolder);
        }

        /**
         * Get the value of resultsFolder
         *
         * @return the value of resultsFolder
         */
        public String getResultsFolder() {
                return workspaceFolder + File.separator + resultsFolder;
        }

        /**
         * Set the value of resultsFolder
         *
         * @param resultsFolder new value of resultsFolder
         */
        public void setResultsFolder(String resultsFolder) {
                String oldResultsFolder = this.resultsFolder;
                this.resultsFolder = resultsFolder;
                propertySupport.firePropertyChange(PROP_RESULTSFOLDER, oldResultsFolder, resultsFolder);
        }

        /**
         * Get the value of workspaceFolder
         *
         * @return the value of workspaceFolder
         */
        public String getWorkspaceFolder() {
                return workspaceFolder;
        }

        /**
         * Set the value of workspaceFolder
         *
         * @param workspaceFolder new value of workspaceFolder
         */
        public void setWorkspaceFolder(String workspaceFolder) {
                String oldWorkspaceFolder = this.workspaceFolder;
                this.workspaceFolder = workspaceFolder;
                propertySupport.firePropertyChange(PROP_WORKSPACEFOLDER, oldWorkspaceFolder, workspaceFolder);
        }

        /**
         * Add a property-change listener for all properties. The listener is
         * called for all properties.
         *
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener
         * instance
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertySupport.addPropertyChangeListener(listener);
        }

        /**
         * Add a property-change listener for a specific property. The listener
         * is called only when there is a change to the specified property.
         *
         * @param prop The static property name PROP_..
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener
         * instance
         */
        public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertySupport.addPropertyChangeListener(prop, listener);
        }

        /**
         * Remove the specified listener from the list
         *
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertySupport.removePropertyChangeListener(listener);
        }

        /**
         * Remove the specified listener for a specified property from the list
         *
         * @param prop The static property name PROP_..
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertySupport.removePropertyChangeListener(prop, listener);
        }
}
