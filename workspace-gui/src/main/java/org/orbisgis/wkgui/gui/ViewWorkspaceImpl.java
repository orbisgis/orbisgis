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
package org.orbisgis.wkgui.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import org.orbisgis.framework.CoreWorkspaceImpl;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * View workspace contains file and folder information
 * that contains GUI related data.
 */
public class ViewWorkspaceImpl implements ViewWorkspace {
    private static final long serialVersionUID = 1L;
    private static final I18n I18N = I18nFactory.getI18n(ViewWorkspaceImpl.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewWorkspaceImpl.class);
    /**
     * Buffer to copy resource to file
     */
    private static final int BUFFER_LENGTH = 4096;
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private CoreWorkspaceImpl coreWorkspace;

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

    public ViewWorkspaceImpl(CoreWorkspaceImpl coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
        SIFPath = coreWorkspace.getWorkspaceFolder() + File.separator + "sif";
        mapContextPath = coreWorkspace.getWorkspaceFolder() + File.separator + "maps";
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

    /**
     * Create minimal resource inside an empty workspace folder
     *
     * @param workspaceFolder
     * @param version_major
     * @param version_minor
     * @param version_revision
     * @param version_qualifier
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
     * @param majorVersion
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
}
