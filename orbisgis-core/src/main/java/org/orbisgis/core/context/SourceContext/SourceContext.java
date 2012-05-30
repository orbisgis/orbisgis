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
package org.orbisgis.core.context.SourceContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.driverManager.DriverManagerListener;
import org.gdms.driver.io.Exporter;
import org.gdms.driver.io.Importer;
import org.gdms.source.SourceManager;


/**
 * SourceManager context
 */
public class SourceContext {
    private SourceManager sourceManager;
    private List<String> sourceFileExtensions = new ArrayList<String>();
    private DriverManagerListener registeredListener = null;
    private static final Logger LOGGER = Logger.getLogger(SourceContext.class);
    
    /**
     * Free resources
     */
    public void dispose() {
        try {
            sourceManager.saveStatus();
        } catch (DriverException ex) {
            //User can't see this error, then translation is useless
            LOGGER.error("Unable to save the Source Status !", ex);
        }
        if(registeredListener!=null) {
            sourceManager.getDriverManager().unregisterListener(registeredListener);
            registeredListener=null;
        }
    }
    /**
     * Constructor
     * @param sourceManager Instance of source manager
     */
    public SourceContext(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
        readSupportedFileExtensions();
        //Register driver listener, to update supported file extensions
        registeredListener = new SourceContextDriverListener();
        sourceManager.getDriverManager().registerListener(registeredListener);
    }
    /**
     * Read supported file extensions of all declared driver manager
     */
    private void readSupportedFileExtensions() {
        DriverManager driverManager = sourceManager.getDriverManager();
        for (String name : driverManager.getDriverNames()) {
            Driver dri = driverManager.getDriver(name);
            if (dri instanceof FileDriver) {
                String[] fileExtensions = ((FileDriver) dri).getFileExtensions();
                for(String fileExtension : fileExtensions) {
                    sourceFileExtensions.add(fileExtension.toLowerCase());
                }
            }
        }        
    }
    /**
     * 
     * @return The instance of source manager
     */
    public SourceManager getSourceManager() {
        return sourceManager;
    }
    /**
     * Read all supported extensions in all drivers registered in the driver manager.
     * Then compare to the specified file extension.
     * @param sourceFile The file name.
     * @return True if this file can be registered in the Source Manager.
     */
    public boolean isFileCanBeRegistered(File sourceFile) {
        String fileName = sourceFile.getName();
        int index = fileName.lastIndexOf('.');
        if (index >= 0 && index < fileName.length()) {
            String ext = fileName.substring(index + 1);
            return sourceFileExtensions.contains(ext.toLowerCase());
        } else {
            return false;
        }
    }
        
    /**
     * Return true if the data source manager has only system tables
     * @return 
     */
    public boolean isDataSourceManagerEmpty() {
        for(String sourceName : sourceManager.getSourceNames()) {
            if(!sourceManager.getSource(sourceName).isSystemTableSource()) {
                return false;
            }
        }
        return true;
    }
    
    private class SourceContextDriverListener implements DriverManagerListener {

        public void driverAdded(String driverId, Class<? extends Driver> driverClass) {
            readSupportedFileExtensions();
        }

        public void driverRemoved(String driverId, Class<? extends Driver> driverClass) {
            readSupportedFileExtensions();
        }

        public void importerAdded(String driverId, Class<? extends Importer> importerClass) {
        }

        public void importerRemoved(String driverId, Class<? extends Importer> importerClass) {
        }

        public void exporterAdded(String driverId, Class<? extends Exporter> exporterClass) {
        }

        public void exporterRemoved(String driverId, Class<? extends Exporter> exporterClass) {
        }
    }
}
