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
package org.orbisgis.framework;

import org.apache.felix.framework.Logger;
import org.orbisgis.frameworkapi.CoreWorkspace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Core Workspace Folder information
 * 
 * See documentation related to java.beans management systems
 * 
 */

public class CoreWorkspaceImpl implements CoreWorkspace {
    private final Logger LOGGER;
    private static final long serialVersionUID = 6L; /*<! Update this integer while adding properties (1 for each new property)*/
    private PropertyChangeSupport propertySupport;
    private final String applicationFolder;
    private static final String DEFAULT_JDBC_USER = "sa";
    private static final boolean DEFAULT_JDBC_REQUIREPASSWORD = false;
    private String workspaceFolder;
    private String sourceFolder;
    private String pluginFolder = "plugins";
    private String tempFolder;
    private String pluginCache = "cache";
    private String logFile = "orbisgis.log";
    private String databaseUser = DEFAULT_JDBC_USER;
    private String databasePassword = "";
    private String jdbcURI = "";
    private boolean requirePassword = DEFAULT_JDBC_REQUIREPASSWORD;
    private static final String CURRENT_WORKSPACE_FILENAME = "currentWorkspace.txt";
    private static final String ALL_WORKSPACE_FILENAME = "workspaces.txt";
    private static final String DATA_BASE_URI_FILE = "database.uri";
    private final int version_major;
    private final int version_minor;
    private final int version_revision;
    private final String version_qualifier;

    public CoreWorkspaceImpl(int version_major, int version_minor, int version_revision, String version_qualifier, Logger logger) {
        this.LOGGER = logger;
        this.version_major = version_major;
        this.version_minor = version_minor;
        this.version_revision = version_revision;
        this.version_qualifier = version_qualifier;
        applicationFolder = getDefaultApplicationFolder();
        propertySupport = new PropertyChangeSupport(this);

        //Read default workspace
        loadCurrentWorkSpace();
    }

    private String getDefaultApplicationFolder() {
        return new File(System.getProperty("user.home"))
                .getAbsolutePath() + File.separator + ".OrbisGIS" + File.separator
                + version_major + "." + version_minor;
    }
    /**
     *
     * @return True if the selected JDBC connection require a password. H2 doesn't require password by default.
     */
    @Override
    public boolean isRequirePassword() {
        return requirePassword;
    }

    /**
     * @param requirePassword Is selected JDBC connection require a password
     */
    @Override
    public void setRequirePassword(boolean requirePassword) {
        this.requirePassword = requirePassword;
    }

    public void writeVersionFile() throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(workspaceFolder,VERSION_FILE)));
            writer.write(Integer.toString(version_major));
            writer.newLine();
            writer.write(Integer.toString(version_minor));
            writer.newLine();
            writer.write(Integer.toString(version_revision));
            writer.newLine();
            writer.write(version_qualifier);
            writer.newLine();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }



    /**
     * Create minimal resource inside an empty workspace folder
     * @param workspaceFolder
     * @throws IOException Error while writing files or the folder is not empty
     */
    public static void initWorkspaceFolder(File workspaceFolder, int version_major, int version_minor,
                                           int version_revision, String version_qualifier) throws IOException {
        if(!workspaceFolder.exists()) {
            if(!workspaceFolder.mkdirs()) {
                throw new IOException("Cannot create workspace directory");
            }
        }
        File[] files = workspaceFolder.listFiles();
        if (files != null && files.length != 0) {
            // This method must be called with empty folder only
            throw new IOException("Workspace folder must be empty");
        }
        CoreWorkspaceImpl coreWorspace = new CoreWorkspaceImpl(version_major, version_minor, version_revision,
                version_qualifier, new Logger());
        coreWorspace.setWorkspaceFolder(workspaceFolder.getAbsolutePath());
        coreWorspace.writeVersionFile();
        coreWorspace.writeUriFile();
    }

    /**
     * Write the uri file. {@link #setWorkspaceFolder} must have been called before.
     * @throws IOException Write error.
     */
    public void writeUriFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(workspaceFolder, DATA_BASE_URI_FILE)))) {
            writer.write(jdbcURI+"\n");
            writer.write(databaseUser+"\n");
            writer.write(requirePassword + "\n");
        }
    }

    private static String getDefaultJDBCConnectionString(String workspaceFolder) {
        return "jdbc:h2:" + new File(workspaceFolder + File.separator +
                "database;MV_STORE=FALSE;DB_CLOSE_DELAY=30;DEFRAG_ALWAYS=TRUE").toURI().getRawPath();
    }

    @Override
    public String getJDBCConnectionReference() {
        if(jdbcURI.isEmpty()) {
            return getDefaultJDBCConnectionString(getWorkspaceFolder());
        } else {
            return jdbcURI;
        }
    }

    /**
     * JDBC uri.In order to keep settings call {@link #writeUriFile()}
     * @param jdbcURI "jdbc:.." URI
     */
    public void setJDBCConnectionReference(String jdbcURI) {
        this.jdbcURI = jdbcURI;
    }

    private void readJDBCConnectionReference() {
        // Set default value, if one is missing
        jdbcURI = getDefaultJDBCConnectionString(getWorkspaceFolder());
        databaseUser = DEFAULT_JDBC_USER;
        requirePassword = DEFAULT_JDBC_REQUIREPASSWORD;
        // Parse configuration file
        String uriFile = getDataBaseUriFilePath();
        if(uriFile!=null) {
            File dbUriFile = new File(uriFile);
            if (dbUriFile.exists()) {
                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader(dbUriFile));
                    String line;
                    if ((line = fileReader.readLine()) != null) {
                        jdbcURI = line;
                    }
                    if ((line = fileReader.readLine()) != null) {
                        databaseUser = line;
                    }
                    if ((line = fileReader.readLine()) != null) {
                        if(!line.isEmpty()) {
                            requirePassword = Boolean.parseBoolean(line);
                        } else {
                            requirePassword = DEFAULT_JDBC_REQUIREPASSWORD;
                        }
                    }
                } catch (IOException ex) {
                    LOGGER.log(Logger.LOG_ERROR, "Could not read the DataBase URI from workspace", ex);
                }
            }
        }
    }
    @Override
    public String getDataBaseUriFilePath() {
        if(workspaceFolder==null) {
            return null;
        }
        return workspaceFolder + File.separator + DATA_BASE_URI_FILE;
    }

    @Override
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

    @Override
    public String getLogFile() {
        return logFile;
    }

    @Override
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

    @Override
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
                    LOGGER.log(Logger.LOG_WARNING, "Cannot read the workspace location " + currentWK + " .", e);
            } finally {
                try {
                    if (fileReader != null) {
                        fileReader.close();
                    }
                } catch (IOException e) {
                    LOGGER.log(Logger.LOG_WARNING, "Cannot close the file at location" + currentWK + " .", e);
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

    @Override
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
        sourceFolder = "sources";
        tempFolder = "temp";
    }

    @Override
    public String getApplicationFolder() {
        return applicationFolder;
    }

    @Override
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

    @Override
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

    @Override
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


    @Override
    public String getWorkspaceFolder() {
        return workspaceFolder;
    }

    /**
     * Set the value of workspaceFolder.
     * Load configuration files in this folder.
     * @param workspaceFolder new value of workspaceFolder
     */
    public void setWorkspaceFolder(String workspaceFolder) {
        String oldWorkspaceFolder = this.workspaceFolder;
        this.workspaceFolder = workspaceFolder;
        readJDBCConnectionReference();
        propertySupport.firePropertyChange(PROP_WORKSPACEFOLDER, oldWorkspaceFolder, workspaceFolder);
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
    public String getDataBaseUser() {
        return databaseUser;
    }

    @Override
    public String getDataBasePassword() {
        return databasePassword;
    }

    @Override
    public void setDataBaseUser(String user) {
        this.databaseUser = user;
    }

    @Override
    public void setDataBasePassword(String password) {
        this.databasePassword = password;
    }

    @Override
    public int getVersionMajor() {
        return version_major;
    }

    @Override
    public int getVersionMinor() {
        return version_minor;
    }

    @Override
    public int getVersionRevision() {
        return version_revision;
    }

    @Override
    public String getVersionQualifier() {
        return version_qualifier;
    }
}
