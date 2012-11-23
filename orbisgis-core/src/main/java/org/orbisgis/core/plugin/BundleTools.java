/*
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
package org.orbisgis.core.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;

/**
 * Functions used by Bundle Host
 * @author Nicolas Fortin
 */
public class BundleTools {
    private final static Logger LOGGER = Logger.getLogger(BundleTools.class);
    private BundleTools() {        
    }

    /**
     * 
     * @return 
     */
    public static List<String> getAvailablePackages() {
        List<String> packages = new ArrayList<String>(getAllPackages());
        Collections.sort(packages);
        return packages;
    }
    private static Set<String> getAllPackages() {
        Set<String> packages = new HashSet<String>();
        String[] pathElements = System.getProperty("java.class.path").split(":");
        for (String element : pathElements) {
            File filePath = new File(element);
            if (element.endsWith("jar")) {
                try {
                    parseJar(filePath, packages);
                } catch (IOException ex) {
                    LOGGER.debug("Unable to fetch packages in "+filePath.getAbsolutePath(),ex);
                }
            } else if (filePath.isDirectory()) {
                try {
                    parseDirectory(filePath,filePath, packages);
                } catch (SecurityException ex) {
                    LOGGER.debug("Unable to fetch the folder "+filePath.getAbsolutePath(),ex);
                }
            }
        }
        return packages;
    }

    private static void parseDirectory(File rootPath, File path, Set<String> packages) throws SecurityException {
        File[] files = path.listFiles();
        for (File file : files) {
            //TODO Java7 check for non-symlink, infinite loop 
            //@link http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".class")) {
                    String parentPath = file.getParent().substring(rootPath.getAbsolutePath().length()+1);
                    packages.add(parentPath.replace(File.separator, "."));
                }
            } else {
                parseDirectory(rootPath,file, packages);
            }
        }
    }

    private static void parseJar(File jarFilePath, Set<String> packages) throws IOException {
        JarFile jar = new JarFile(jarFilePath);
        Enumeration<? extends JarEntry> entryEnum = jar.entries();
        while (entryEnum.hasMoreElements()) {
            JarEntry entry = entryEnum.nextElement();
            if (!entry.isDirectory()) {
                final String path = entry.getName();
                if (path.endsWith(".class")) {
                    // Extract folder
                    String parentPath = (new File(path)).getParent();
                    if(parentPath!=null) {
                        packages.add(parentPath.replace(File.separator, "."));
                    }
                }
            }
        }
    }
}
