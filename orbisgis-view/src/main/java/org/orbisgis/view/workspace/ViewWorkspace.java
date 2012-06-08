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
package org.orbisgis.view.workspace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import org.orbisgis.core.workspace.CoreWorkspace;

/**
 * View workspace contains file and folder information
 * that contains GUI related data.
 */


public class ViewWorkspace {
    private static final long serialVersionUID = 1L;
    private PropertyChangeSupport propertySupport;
    private CoreWorkspace coreWorkspace;
    public ViewWorkspace(CoreWorkspace coreWorkspace) {
        propertySupport = new PropertyChangeSupport(this);
        this.coreWorkspace = coreWorkspace;
        SIFPath = coreWorkspace.getWorkspaceFolder() + File.separator + "sif" ;
        mapContextPath = coreWorkspace.getWorkspaceFolder() + File.separator + "maps";
    }
        private String dockingLayoutFile = "docking_layout.xml";
        public static final String PROP_DOCKINGLAYOUTFILE = "dockingLayoutFile";
        private String SIFPath = "";
        public static final String PROP_SIFPATH = "SIFPath";
        private String mapContextPath;
        public static final String PROP_MAPCONTEXTPATH = "mapContextPath";

        /**
         * Get the value of mapContextPath
         * This folder contains all serialised Map Context shown in
         * the Map Context library
         * @return the value of mapContextPath
         */
        public String getMapContextPath() {
                return mapContextPath;
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

    /**
     * Get the value of SIFPath
     *
     * @return the value of SIFPath
     */
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

    /**
     * Get the value of dockingLayoutFile
     *
     * @return the value of dockingLayoutFile
     */
    public String getDockingLayoutFile() {
        return dockingLayoutFile;
    }

    /**
     * @return The full path of the layout file
     */
    public String getDockingLayoutPath() {
        return coreWorkspace.getWorkspaceFolder()+File.separator+dockingLayoutFile;
    }
    /**
     * Set the value of dockingLayoutFile
     * The docking layout file contain the panels layout and editors opened in the last OrbisGis instance
     * @param dockingLayoutFile new value of dockingLayoutFile
     */
    public void setDockingLayoutFile(String dockingLayoutFile) {
        String oldDockingLayoutFile = this.dockingLayoutFile;
        this.dockingLayoutFile = dockingLayoutFile;
        propertySupport.firePropertyChange(PROP_DOCKINGLAYOUTFILE, oldDockingLayoutFile, dockingLayoutFile);
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
