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
package org.orbisgis.base.workspace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;

/**
 * Core Worskpace Folder information
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 */

public class CoreWorkspace implements Serializable {
    private static final long serialVersionUID = 4L; /*<! Update this integer while adding properties (1 for each new property)*/
    private PropertyChangeSupport propertySupport;
    
    
    private String workspaceFolder = new File(System.getProperty("user.home")).getAbsolutePath() + File.separator + "OrbisGIS";
    public static final String PROP_WORKSPACEFOLDER = "workspaceFolder";
    private String resultsFolder = workspaceFolder + File.separator + "results";
    public static final String PROP_RESULTSFOLDER = "resultsFolder";
    private String sourceFolder = workspaceFolder + File.separator + "sources";
    public static final String PROP_SOURCEFOLDER = "sourceFolder";
    private String pluginFolder = new File("lib" + File.separator + "ext").getAbsolutePath();
    public static final String PROP_PLUGINFOLDER = "pluginFolder";
    private String tempFolder = workspaceFolder + File.separator + "temp";

    /**
     * Get the value of tempFolder
     *
     * @return the value of tempFolder
     */
    public String getTempFolder() {
        return tempFolder;
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
        return pluginFolder;
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
        return sourceFolder;
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
        return resultsFolder;
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
     * bean constructor
     */
    public CoreWorkspace() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    
    /**
     * Add a property-change listener for all properties.
     * The listener is called for all properties.
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    /**
     * Add a property-change listener for a specific property.
     * The listener is called only when there is a change to 
     * the specified property.
     * @param prop The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(prop, listener);
    }
    /**
     * Remove the specified listener from the list
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Remove the specified listener for a specified property from the list
     * @param prop The static property name PROP_..
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(prop,listener);
    }
}
