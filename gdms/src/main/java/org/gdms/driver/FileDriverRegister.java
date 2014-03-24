/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.driver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gdms.driver.driverManager.DriverLoadException;

/**
 * This class is dedicated to the management of the drivers that are associated to 
 * source files. We must be sure, at any moment, that for every source file we have
 * one, and only one, driver associated to it. Without this constraint, we can't ensure
 * the reliability of our concurrency access.</p>
 * <p>This class relies on a Map to run. 
 * @author alexis
 */


public class FileDriverRegister {

        private Map<File, FileDriver> fileDriverMap;
        
        /**
         * Creates a new {@code FileDriverRegister}, without any reference to any File in it.
         */
        public FileDriverRegister(){
                fileDriverMap = new HashMap<File, FileDriver>();
        }
        
        /**
         * Add a new entry in this register. Be aware that, for a given File {@code f},
         * with two drivers {@code d1} and {@code d2}, the following calls :</p><p>
         * <p>{@code fdr.addFile(f, d1);}</p>
         * <p>{@code fdr.addFile(f, d2);}</p>
         * <p>will result in the association of the File {@code f} to the Driver 
         * {@code d2}.
         * @param f
         * @param d 
         * @throw DriverLoadException if {@code f} or {@code d} is null.
         */
        public void addFile(File f, FileDriver d){
                if (f==null || d==null){
                        throw new DriverLoadException("You can't register a file without associated driver");
                }
                fileDriverMap.put(f, d);
        }
        
        /**
         * Checks whether this register contains an entry with key {@code file}
         * @param file
         * @return 
         * {@code true} if 
         */
        public boolean contains(File file){
                return fileDriverMap.containsKey(file);
        }
        
        /**
         * Get the driver associated to {@code file}, if any, null otherwise.
         * @return The driver associated to {@code file}, if any, null otherwise.
         */
        public FileDriver getDriver(File file) {
                return fileDriverMap.get(file);
        }
        
        /**
         * Remove the entry corresponding to {@code file}. If it was not present
         * in the register, does nothing.
         * @param file
         * @return 
         */
        public FileDriver removeFile(File file){
                return fileDriverMap.remove(file);
        }
        
}
