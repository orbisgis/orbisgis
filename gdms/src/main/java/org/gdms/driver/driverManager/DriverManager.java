/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.driverManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileDriverRegister;

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
        private static final Logger logger = Logger.getLogger(DriverManager.class);

        public static final String DEFAULT_SINGLE_TABLE_NAME = "main";
        private FileDriverRegister fdr = new FileDriverRegister();
        
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
        public FileDriver getDriver(File file){
                if(fdr.contains(file)){
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
                        throw new DriverLoadException("No suitable driver for "+ file.getAbsolutePath());
                }
        }
        
        /**
         * Remove the entry associating {@code file} with {@code driver}.
         */
        public void removeFile(File file){
                fdr.removeFile(file);
        }
        
        /**
         * Give access to the underlying {@code FileDriverRegister} instance.
         * @return 
         */
        public FileDriverRegister getFileDriverRegister(){
                return fdr;
        }

        /**
         * Get a new instance of a driver class registered with the name {@code name}
         * with the method {@link DriverManager#registerDriver(java.lang.Class) registerDriver}
         *
         * @param name
         *            name of the desired driver
         *
         * @return
         *
         * @throws DriverLoadException
         *             if the driver class represents an abstract class, an
         *             interface, an array class, a primitive type, or void; or if
         *             the class has no nullary constructor; or if the instantiation
         *             fails for some other reason
         */
        public Driver getDriver(String name) {
                logger.trace("Instantiating driver " + name);
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
         * @param driverClass a class extending Driver
         */
        public void registerDriver(Class<? extends Driver> driverClass) {
                logger.trace("Registering driver " + driverClass.getName());
                        Driver driver;
                        try {
                                driver = driverClass.newInstance();
                                driverClasses.put(driver.getDriverId(), driverClass);
                        } catch (InstantiationException e) {
                                throw new IllegalArgumentException(
                                        "The driver cannot be instantiated", e);
                        } catch (IllegalAccessException e) {
                                throw new IllegalArgumentException(
                                        "The driver cannot be instantiated", e);
                        }
        }
        
        /**
         * Unregisters a driver class from this DriverManager.
         * @param driverId the driver id (result of myDriver.getDriverId()) of the driver to remove
         */
        public void unregisterDriver(String driverId) {
                driverClasses.remove(driverId);
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
         *            the name of a registered driver
         *
         * @return the Driver Class, or null if there is none by that name
         */
        public Class<? extends Driver> getDriverClassByName(String driverName) {
                return driverClasses.get(driverName);
        }

        /**
         * Gets all the registered drivers that comply with the specified DriverFilter
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
                        } catch (IllegalAccessException e) {
                                // ignore
                        }
                }

                return drivers.toArray(new Driver[drivers.size()]);
        }
}
