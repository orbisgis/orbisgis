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
package org.orbisgis.view.workspace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.orbisgis.core.workspace.CoreWorkspaceImpl;

/**
 * View workspace contains file and folder information
 * that contains GUI related data.
 */


public class ViewWorkspace implements org.orbisgis.viewapi.workspace.ViewWorkspace {
    private static final long serialVersionUID = 1L;

    private PropertyChangeSupport propertySupport;
    private CoreWorkspaceImpl coreWorkspace;
    public ViewWorkspace(CoreWorkspaceImpl coreWorkspace) {
        propertySupport = new PropertyChangeSupport(this);
        this.coreWorkspace = coreWorkspace;
        SIFPath = coreWorkspace.getWorkspaceFolder() + File.separator + "sif" ;
        mapContextPath = coreWorkspace.getWorkspaceFolder() + File.separator + "maps";
    }
        private String dockingLayoutFile = "docking_layout.xml";
        private String SIFPath = "";
        private String mapContextPath;
        
        @Override
        public String getMapContextPath() {
                return mapContextPath;
        }

        @Override
        public CoreWorkspaceImpl getCoreWorkspace() {
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
        propertySupport.removePropertyChangeListener(prop,listener);
    }
    /**
     * Create minimal resource inside an empty workspace folder
     * @param workspaceFolder
     * @throws IOException Error while writing files or the folder is not empty
     */
    public static void initWorkspaceFolder(File workspaceFolder) throws IOException {
        CoreWorkspaceImpl.initWorkspaceFolder(workspaceFolder);
    }
    /**
     * Check if the provided folder can be loaded has the workspace
     * @param workspaceFolder
     * @return True if valid
     */
    public static boolean isWorkspaceValid(File workspaceFolder) {
        // not exist or empty
        // contain the version file with same major version
        if(!workspaceFolder.exists()) {
                return true;
        }
        if(!workspaceFolder.isDirectory()) {
                return false;
        }
        if(workspaceFolder.listFiles().length==0) {
                return true;
        }
        File versionFile = new File(workspaceFolder, CoreWorkspaceImpl.VERSION_FILE);
        if(!versionFile.exists()) {
                return false;
        }       
        // Read the version file of the workspace folder
        BufferedReader fileReader=null;
        try {
                fileReader = new BufferedReader(new FileReader(
                               versionFile));
                String line = fileReader.readLine();
                if(line!=null) {
                        return Integer.valueOf(line).equals(CoreWorkspaceImpl.MAJOR_VERSION);
                }
        } catch (IOException e) {
                throw new RuntimeException("Cannot read the workspace location", e);
        } finally{
            try {
                if(fileReader!=null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot read the workspace location", e);
            }
        }
        return false;            
    }
}
