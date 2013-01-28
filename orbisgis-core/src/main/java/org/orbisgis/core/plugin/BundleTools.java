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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleRevision;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Functions used by Bundle Host.
 * @author Nicolas Fortin
 */
public class BundleTools {
    private final static Logger LOGGER = Logger.getLogger(BundleTools.class);
    private final static I18n I18N = I18nFactory.getI18n(BundleTools.class);
    private BundleTools() {        
    }

    /**
     * Find if the bundle already exists and retrieve it.
     * @param hostBundle
     * @param symbolicName
     * @param version
     * @return
     */
    private static Bundle getBundle(BundleContext hostBundle, String symbolicName, Version version) {
        Bundle[] bundles = hostBundle.getBundles();
        for (int i = 0; (bundles != null) && (i < bundles.length); i++)
        {
            String sym = bundles[i].getSymbolicName();
            Version ver = bundles[i].getVersion();
            if ((symbolicName != null)
                    && (sym != null)
                    && symbolicName.equals(sym)
                    && version.equals(ver))
            {
                return bundles[i];
            }
        }
        return null;
    }
    /**
     * Register in the host bundle the provided list of bundle reference
     * @param hostBundle Host BundleContext
     * @param bundleToInstall Bundle Reference array
     */
    public static void installBundles(BundleContext hostBundle,BundleReference[] bundleToInstall) {
            for(BundleReference bundleRef : bundleToInstall) {
                    if(bundleRef.getBundleJarContent()==null) {
                            LOGGER.warn(I18N.tr("OrbisGIS package does not contain the {0} bundle plugin",bundleRef.getArtifactId()));
                    } else {
                        try {
                                Bundle builtInBundle = hostBundle.installBundle(bundleRef.getBundleUri(),
                                bundleRef.getBundleJarContent());
                                //Do not start if bundle is a fragment bundle (no Activator in fragment)
                                if(bundleRef.isAutoStart() && ((
                                        builtInBundle.adapt(BundleRevision.class).getTypes() &
                                        BundleRevision.TYPE_FRAGMENT) == 0)) {
                                        LOGGER.debug("Starting "+bundleRef.getArtifactId()+"..");
                                        builtInBundle.start();
                                        LOGGER.debug("Started");
                                }
                        } catch(BundleException ex) {
                                LOGGER.error(ex.getLocalizedMessage(), ex);
                        }
                    }
            }
    }
    /**
     * Read the class path, open all Jars and folders, retrieve the package list.
     * @return List of package name
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
            // TODO Java7 check for non-symlink,
            // without this check it might generate an infinite loop 
            // @link http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)
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
