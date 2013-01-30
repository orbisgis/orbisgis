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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.io.FilenameUtils;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Functions used by Bundle Host. As long as OrbisGIS is not entirely converted into OSGi bundles,
 * the host need to export packages using this class.
 * @author Nicolas Fortin
 */
public class BundleTools {
    private final static Logger LOGGER = Logger.getLogger(BundleTools.class);
    private final static I18n I18N = I18nFactory.getI18n(BundleTools.class);
    private final static String MANIFEST_FILENAME = "MANIFEST.MF";
    private final static String PACKAGE_NAMESPACE = "osgi.wiring.package";
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
     * This kind of package list does not contain versions and are useful for non-OSGi packages only.
     * @return List of package name
     */
    public static List<String> getAvailablePackages() {
        List<String> packages = new ArrayList<String>(getAllPackages());
        Collections.sort(packages);
        return packages;
    }
    private static List<String> getClassPath() {
        List<String> classPath = new LinkedList<String>();
        // Read declared class in the manifest
        try {
            Enumeration<URL> resources = BundleTools.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try {
                    Manifest manifest = new Manifest(url.openStream());
                    String value = manifest.getMainAttributes().getValue("Class-Path");
                    if(value!=null) {
                        String[] pathElements = value.split(" ");
                        if(pathElements==null) {
                            pathElements = new String[] {value};
                        }
                        classPath.addAll(Arrays.asList(pathElements));
                    }
                } catch (IOException ex) {
                    LOGGER.warn("Unable to retrieve Jar MANIFEST "+url,ex);
                }
            }
        } catch (IOException ex) {
            LOGGER.warn("Unable to retrieve Jar MANIFEST",ex);
        }
        // Read packages in the class path
        String javaClasspath = System.getProperty("java.class.path");
        String[] pathElements = javaClasspath.split(":");
        if(pathElements==null) {
            pathElements = new String[] {javaClasspath};
        }
        classPath.addAll(Arrays.asList(pathElements));
        return classPath;
    }
    /**
     * Read the class path, search for OSGi manifest declaration.
     * Reading MANIFEST is useful to read package versions of OSGi compliant Jars.
     * If a package version is not properly exported then an OSGi bundle that depends on this package with a specified
     * version could not be Resolved.
     * @return
     */
    public static Collection<PackageDeclaration> fetchManifests() {
        List<PackageDeclaration> packages = new LinkedList<PackageDeclaration>();
        List<String> pathElements = getClassPath();
        // Fetch
        for (String element : pathElements) {
            File filePath = new File(element);
            if (FilenameUtils.getExtension(element).equals("jar") && filePath.exists()) {
                try {
                    parseJarManifest(filePath, packages);
                } catch (IOException ex) {
                    LOGGER.debug("Unable to fetch packages in "+filePath.getAbsolutePath(),ex);
                }
            } else if (filePath.isDirectory()) {
                try {
                    parseDirectoryManifest(filePath, filePath, packages);
                } catch (SecurityException ex) {
                    LOGGER.debug("Unable to fetch the folder "+filePath.getAbsolutePath(),ex);
                }
            }
        }

        return packages;
    }

    /**
     * Parse a Manifest in order to extract Exported Package
     * @param manifest
     * @param packages
     * @throws IOException
     */
    public static void parseManifest(Manifest manifest, List<PackageDeclaration> packages) throws IOException {
        Attributes attributes = manifest.getMainAttributes();
        String exports = attributes.getValue(Constants.EXPORT_PACKAGE);
        org.apache.felix.framework.Logger logger = new org.apache.felix.framework.Logger();
        // Use Apache Felix to parse the Manifest Export header
        List<BundleCapability> exportsCapability = ManifestParser.parseExportHeader(logger,null,exports,"0",
                new Version(0,0,0));
        for(BundleCapability bc : exportsCapability) {
            Map<String,Object> attr = bc.getAttributes();
            // If the package contain a package name and a package version
            if(attr.containsKey(PACKAGE_NAMESPACE) && attr.containsKey(Constants.VERSION_ATTRIBUTE)) {
                packages.add(new PackageDeclaration((String)attr.get(PACKAGE_NAMESPACE),
                        (Version)attr.get(Constants.VERSION_ATTRIBUTE)));
            }
        }
    }
    private static void parseDirectoryManifest(File rootPath, File path, List<PackageDeclaration> packages) throws SecurityException {
        File[] files = path.listFiles();
        for (File file : files) {
            // TODO Java7 check for non-symlink,
            // without this check it might generate an infinite loop
            // @link http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)
            if (!file.isDirectory()) {
                if (file.getName().equals(MANIFEST_FILENAME)) {
                    try {
                        Manifest manifest = new Manifest(new FileInputStream(file));
                        parseManifest(manifest, packages);
                    } catch (Exception ex) {
                        LOGGER.warn("Unable to read manifest in "+file.getAbsolutePath(),ex);
                    }
                } if (FilenameUtils.getExtension(file.getName()).equals("jar") && file.exists()) {
                    try {
                        parseJarManifest(file, packages);
                    } catch (IOException ex) {
                        LOGGER.warn("Unable to fetch packages in "+file.getAbsolutePath(),ex);
                    }
                }
            } else {
                parseDirectoryManifest(rootPath, file, packages);
            }
        }
    }

    private static void parseJarManifest(File jarFilePath, List<PackageDeclaration> packages) throws IOException {
        JarFile jar = new JarFile(jarFilePath);
        parseManifest(jar.getManifest(), packages);
    }
    private static Set<String> getAllPackages() {
        Set<String> packages = new HashSet<String>();
        List<String> pathElements = getClassPath();
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
                if (file.getName().endsWith(".class")
                        && file.getParent().length()>rootPath.getAbsolutePath().length()) {
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
