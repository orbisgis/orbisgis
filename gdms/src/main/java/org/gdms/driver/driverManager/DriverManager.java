/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver.driverManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileDriverRegister;
import org.gdms.driver.io.Exporter;
import org.gdms.driver.io.FileExporter;
import org.gdms.driver.io.FileImporter;
import org.gdms.driver.io.Importer;

/**
 * This class is responsible for storing all the available drivers.</p><p>
 *
 * It does not have any driver registered by default, it is up to the calling code to
 * register the drivers.</p>
 * <p>As this class is the entry point to retrieve drivers of any kind, it is the best place
 * to manage drivers associated to files. Consequently, it is up to instances of this
 * class to ensure that a file has only one driver associated to it. To do that,
 * It relies on an inner {@code FileDriverRegister}.
 */
public final class DriverManager {

        private Map<String, Class<? extends Driver>> driverClasses = new HashMap<String, Class<? extends Driver>>();
        private static final Logger LOG = Logger.getLogger(DriverManager.class);
        private List<DriverManagerListener> listeners = new ArrayList<DriverManagerListener>();
        private Map<String, Class<? extends Importer>> importClasses = new HashMap<String, Class<? extends Importer>>();
        private Map<String, Class<? extends Exporter>> exportClasses = new HashMap<String, Class<? extends Exporter>>();
        public static final String DEFAULT_SINGLE_TABLE_NAME = "main";
        private FileDriverRegister fdr = new FileDriverRegister();

        public FileImporter getFileImporter(File file) {
                for (Entry<String, Class<? extends Importer>> e : importClasses.entrySet()) {
                        if (FileImporter.class.isAssignableFrom(e.getValue())) {
                                FileImporter i = (FileImporter) getImporter(e.getKey());
                                for (String ex : i.getFileExtensions()) {
                                        if (file.getAbsolutePath().toLowerCase().endsWith(ex.toLowerCase())) {
                                                try {
                                                        i.setFile(file);
                                                } catch (DriverException exc) {
                                                        throw new DriverLoadException(exc);
                                                }
                                                return i;
                                        }
                                }
                        }
                }

                throw new DriverLoadException("No suitable importer for " + file.getAbsolutePath());
        }

        public FileExporter getFileExporter(File file) {
                for (Entry<String, Class<? extends Exporter>> e : exportClasses.entrySet()) {
                        if (FileExporter.class.isAssignableFrom(e.getValue())) {
                                FileExporter i = (FileExporter) getExporter(e.getKey());
                                for (String ex : i.getFileExtensions()) {
                                        if (file.getAbsolutePath().toLowerCase().endsWith(ex.toLowerCase())) {
                                                try {
                                                        i.setFile(file);
                                                } catch (DriverException exc) {
                                                        throw new DriverLoadException(exc);
                                                }
                                                return i;
                                        }
                                }
                        }
                }

                throw new DriverLoadException("No suitable exporter for " + file.getAbsolutePath());
        }

        /**
         * Get a driver suitable for the {@code File} file. If the {@code File} has already
         * been treated by this manager, and if it has not been removed, the same
         * {@code Driver} instance is returned. If it's the first time we meet
         * {@code file}, or if it has been removed since the last time we've seen
         * it, a brand new instance, associated to the {@code File} instance, is
         * returned.
         *
         * @param file
         * @return
         * @throw DriverLoadException if we can't find a Driver able to manage our file.
         */
        public FileDriver getDriver(File file) {
                if (fdr.contains(file)) {
                        return fdr.getDriver(file);
                } else {
                        //we must try to retrieve a ew suitable driver.
                        String[] names = getDriverNames();
                        for (int i = 0; i < names.length; i++) {
                                Driver driver = getDriver(names[i]);
                                if (driver instanceof FileDriver) {
                                        FileDriver fileDriver = (FileDriver) driver;
                                        String[] extensions = fileDriver.getFileExtensions();
                                        for (String extension : extensions) {
                                                if (file.getAbsolutePath().toLowerCase().endsWith(
                                                        extension.toLowerCase())) {
                                                        try {
                                                                fileDriver.setFile(file);
                                                        } catch (DriverException ex) {
                                                                throw new DriverLoadException(ex);
                                                        }
                                                        fdr.addFile(file, fileDriver);
                                                        return fileDriver;
                                                }
                                        }
                                }
                        }
                        throw new DriverLoadException("No suitable driver for " + file.getAbsolutePath());
                }
        }

        /**
         * Remove the entry associating {@code file} with {@code driver}.
         *
         * @param file a file
         */
        public void removeFile(File file) {
                fdr.removeFile(file);
        }

        /**
         * Give access to the underlying {@code FileDriverRegister} instance.
         *
         * @return
         */
        public FileDriverRegister getFileDriverRegister() {
                return fdr;
        }

        public Importer getImporter(String name) {
                try {
                        Class<? extends Importer> importClass = importClasses.get(name);
                        if (importClass == null) {
                                throw new DriverLoadException("Importer not found: " + name);
                        }
                        return importClass.newInstance();
                } catch (InstantiationException e) {
                        throw new DriverLoadException(e);
                } catch (IllegalAccessException e) {
                        throw new DriverLoadException(e);
                }
        }

        public Exporter getExporter(String name) {
                try {
                        Class<? extends Exporter> exportClass = exportClasses.get(name);
                        if (exportClass == null) {
                                throw new DriverLoadException("Importer not found: " + name);
                        }
                        return exportClass.newInstance();
                } catch (InstantiationException e) {
                        throw new DriverLoadException(e);
                } catch (IllegalAccessException e) {
                        throw new DriverLoadException(e);
                }
        }

        /**
         * Get a new instance of a driver class registered with the name {@code name}
         * with the method {@link DriverManager#registerDriver(java.lang.Class) registerDriver}
         *
         * @param name
         * name of the desired driver
         *
         * @return
         *
         * @throws DriverLoadException
         * if the driver class represents an abstract class, an
         * interface, an array class, a primitive type, or void; or if
         * the class has no nullary constructor; or if the instantiation
         * fails for some other reason
         */
        public Driver getDriver(String name) {
                LOG.trace("Instantiating driver " + name);
                try {
                        Class<? extends Driver> driverClass = driverClasses.get(name);
                        if (driverClass == null) {
                                throw new DriverLoadException("Driver not found: " + name);
                        }
                        return driverClass.newInstance();
                } catch (InstantiationException e) {
                        throw new DriverLoadException(e);
                } catch (IllegalAccessException e) {
                        throw new DriverLoadException(e);
                }
        }

        /**
         * Registers a driver class into this DriverManager.
         *
         * @param driverClass a class extending Driver
         */
        public void registerDriver(Class<? extends Driver> driverClass) {
                LOG.trace("Registering driver " + driverClass.getName());
                Driver driver;
                try {
                        driver = driverClass.newInstance();
                        driverClasses.put(driver.getDriverId(), driverClass);
                        fireDriverAdded(driver.getDriverId(), driverClass);
                } catch (InstantiationException e) {
                        throw new IllegalArgumentException(
                                "The driver cannot be instantiated", e);
                } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "The driver cannot be instantiated", e);
                }
        }

        public void registerImporter(Class<? extends Importer> importerClass) {
                LOG.trace("Registering driver " + importerClass.getName());
                try {
                        Importer driver = importerClass.newInstance();
                        importClasses.put(driver.getImporterId(), importerClass);
                        fireImporterAdded(driver.getImporterId(), importerClass);
                } catch (InstantiationException e) {
                        throw new IllegalArgumentException(
                                "The importer cannot be instantiated", e);
                } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "The importer cannot be instantiated", e);
                }
        }

        public void registerExporter(Class<? extends Exporter> exporterClass) {
                LOG.trace("Registering driver " + exporterClass.getName());
                try {
                        Exporter driver = exporterClass.newInstance();
                        exportClasses.put(driver.getExporterId(), exporterClass);
                        fireExporterAdded(driver.getExporterId(), exporterClass);
                } catch (InstantiationException e) {
                        throw new IllegalArgumentException(
                                "The exporter cannot be instantiated", e);
                } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "The exporter cannot be instantiated", e);
                }
        }

        /**
         * Unregisters a driver class from this DriverManager.
         *
         * @param driverId the driver id (result of myDriver.getDriverId()) of the driver to remove
         */
        public void unregisterDriver(String driverId) {
                Class<? extends Driver> r = driverClasses.remove(driverId);
                if (r != null) {
                        fireDriverRemoved(driverId, r);
                }
        }

        public void unregisterImporter(String importerId) {
                Class<? extends Importer> r = importClasses.remove(importerId);
                if (r != null) {
                        fireImporterRemoved(importerId, r);
                }
        }

        public void unregisterExporter(String exporterId) {
                Class<? extends Exporter> r = exportClasses.remove(exporterId);
                if (r != null) {
                        fireExporterRemoved(exporterId, r);
                }
        }

        /**
         * Gets the name of all the registered drivers.
         *
         * @return an array containing the names
         */
        public String[] getDriverNames() {
                return driverClasses.keySet().toArray(new String[driverClasses.size()]);
        }

        /**
         * Gets the Driver Class which as been registered with the specified name.
         *
         * @param driverName
         * the name of a registered driver
         *
         * @return the Driver Class, or null if there is none by that name
         */
        public Class<? extends Driver> getDriverClassByName(String driverName) {
                return driverClasses.get(driverName);
        }

        /**
         * Gets all the registered drivers that comply with the specified DriverFilter
         *
         * @param driverFilter
         * @return
         */
        public Driver[] getDrivers(DriverFilter driverFilter) {
                ArrayList<Driver> drivers = new ArrayList<Driver>();

                Iterator<Class<? extends Driver>> iterator = driverClasses.values().iterator();

                while (iterator.hasNext()) {
                        try {
                                Driver driver = iterator.next().newInstance();
                                if (driverFilter.acceptDriver(driver)) {
                                        drivers.add(driver);
                                }
                        } catch (InstantiationException e) {
                                // ignore
                                LOG.warn("Failed to instanciate a driver class.", e);
                        } catch (IllegalAccessException e) {
                                // ignore
                                LOG.warn("Failed to instanciate a driver class.", e);
                        }
                }

                return drivers.toArray(new Driver[drivers.size()]);
        }

        private void fireDriverAdded(String i, Class<? extends Driver> d) {
                for (DriverManagerListener l : listeners) {
                        l.driverAdded(i, d);
                }
        }

        private void fireDriverRemoved(String i, Class<? extends Driver> d) {
                for (DriverManagerListener l : listeners) {
                        l.driverRemoved(i, d);
                }
        }

        private void fireImporterAdded(String i, Class<? extends Importer> d) {
                for (DriverManagerListener l : listeners) {
                        l.importerAdded(i, d);
                }
        }

        private void fireImporterRemoved(String i, Class<? extends Importer> d) {
                for (DriverManagerListener l : listeners) {
                        l.importerRemoved(i, d);
                }
        }

        private void fireExporterAdded(String i, Class<? extends Exporter> d) {
                for (DriverManagerListener l : listeners) {
                        l.exporterAdded(i, d);
                }
        }

        private void fireExporterRemoved(String i, Class<? extends Exporter> d) {
                for (DriverManagerListener l : listeners) {
                        l.exporterRemoved(i, d);
                }
        }

        /**
         * Registers a DriverManagerListener with this DriverManager.
         *
         * @param l a listener
         * @throws NullPointerException if a null listener if given
         */
        public void registerListener(DriverManagerListener l) {
                if (l != null) {
                        listeners.add(l);
                } else {
                        throw new NullPointerException("listener cannot be null");
                }
        }

        /**
         * Unregisters a DriverManagerListener from this DriverManager.
         *
         * @param l a listener
         * @return true if the listener was actually removed
         */
        public boolean unregisterListener(DriverManagerListener l) {
                return listeners.remove(l);
        }
}
