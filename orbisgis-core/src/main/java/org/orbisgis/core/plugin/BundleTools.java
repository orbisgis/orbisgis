/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
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
    private final static String BUNDLE_DIRECTORY = "bundle";
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
     * String version of bundle state index
     * @param i Bundle state like {@link Bundle#ACTIVE}
     * @return Bundle state string version
     */
    public static String getStateString(int i) {
        switch (i) {
            case Bundle.ACTIVE:
                return "Active   ";
            case Bundle.INSTALLED:
                return "Installed";
            case Bundle.RESOLVED:
                return "Resolved ";
            case Bundle.STARTING:
                return "Starting ";
            case Bundle.STOPPING:
                return "Stopping ";
            default:
                return "Unknown  ";
        }
    }

    /**
     * Register in the host bundle the provided list of bundle reference
     * @param hostBundle Host BundleContext
     * @param bundleToInstall Bundle Reference array
     */
    /*
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
    */

    /**
     * @param bundle The bundle instance
     * @return True if the bundle has no Activator and cannot be started
     */
    private static boolean isFragment(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    /**
     * @param bundle The bundle instance
     * @return True if the bundle has no Activator and cannot be started
     */
    private static String getFragmentHost(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST);
    }

    /**
     * Delete OSGi fragment bundles that are both in OSGi cache and in bundle sub-dir
     * @param bundleCache OSGi bundle cache  ex: ~/.Orbisgis/4.X/cache/
     */
    public static void deleteFragmentInCache(File bundleCache) {
        if(bundleCache.exists()) {
            // List bundles in the /bundle subdirectory
            File bundleFolder = new File(BUNDLE_DIRECTORY);
            if(!bundleFolder.exists()) {
                return;
            }
            File[] files = bundleFolder.listFiles();
            if (files != null) {
                List<String> fragmentBundlesArtifacts = new ArrayList<>(files.length);
                // Search for Fragment in /bundle/ subdir
                for(File file : files) {
                    if(FilenameUtils.isExtension(file.getName(),"jar")) {
                        // Read Manifest
                        try {
                            JarFile jar = new JarFile(file);
                            Manifest manifest = jar.getManifest();
                            if(manifest!=null && manifest.getMainAttributes()!=null) {
                                String artifact = manifest.getMainAttributes().getValue(Constants.FRAGMENT_HOST);
                                if(artifact != null) {
                                    fragmentBundlesArtifacts.add(parseManifest(manifest, null).getArtifactId());
                                }
                            }
                        } catch (IOException ex) {
                            LOGGER.error("Error while reading Jar manifest:\n"+file.getPath());
                        }
                    }
                }
                // Remove folders in bundle cache that contain a fragment cache
                File[] cacheFolders = bundleCache.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
                if(cacheFolders != null) {
                    for (File folder : cacheFolders) {
                        try {
                            // Get the first folder, may contain only one ex:"version0.0"
                            File[] cacheBundleFolder = folder.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
                            if (cacheBundleFolder != null && cacheBundleFolder.length > 0) {
                                // Read Jar manifest
                                File jarBundle = new File(cacheBundleFolder[0], "bundle.jar");
                                if (jarBundle.exists()) {
                                    // Read artifact
                                    String artifact = parseJarManifest(jarBundle, null).getArtifactId();
                                    if (fragmentBundlesArtifacts.contains(artifact)) {
                                        // Delete the cache folder
                                        FileUtils.deleteDirectory(folder);
                                        if(folder.exists()) {
                                            LOGGER.error("Cannot delete a bundle cache folder, library may not be up to" +
                                                    " date, please delete the following folder and restart OrbisGIS:" +
                                                    "\n"+folder.getPath());
                                        } else {
                                            LOGGER.info(I18N.tr("Delete fragment bundle {0} in cache directory", artifact));
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            LOGGER.error("Error while reading Jar manifest:\n"+folder.getPath());
                        }
                    }
                }
            }
        }
    }
    /**
     * Register in the host bundle the provided list of bundle reference
     * @param hostBundle Host BundleContext
     * @param nonDefaultBundleDeploying Bundle Reference array to deploy bundles in a non default way (install&start)
     */
    public static void installBundles(BundleContext hostBundle,BundleReference[] nonDefaultBundleDeploying) {
        //Create a Map of nonDefaultBundleDeploying by their artifactId
        Map<String,BundleReference> customDeployBundles = new HashMap<String, BundleReference>(nonDefaultBundleDeploying.length);
        for(BundleReference ref : nonDefaultBundleDeploying) {
            customDeployBundles.put(ref.getArtifactId(),ref);
        }

        // List bundles in the /bundle subdirectory
        File bundleFolder = new File(BUNDLE_DIRECTORY);
        if(!bundleFolder.exists()) {
            return;
        }
        File[] files = bundleFolder.listFiles();
        List<File> jarList = new ArrayList<File>();
        if (files != null) {
            for(File file : files) {
                if(FilenameUtils.isExtension(file.getName(),"jar")) {
                    jarList.add(file);
                }
            }
        }
        if (!jarList.isEmpty()) {
            Map<String,Bundle> installedBundleMap = new HashMap<String,Bundle>();
            Set<String> fragmentHosts = new HashSet<>();

            // Keep a reference to bundles in the framework cache
            for (Bundle bundle : hostBundle.getBundles()) {
                String key = bundle.getSymbolicName();
                installedBundleMap.put(key, bundle);
                String fragmentHost = getFragmentHost(bundle);
                if(fragmentHost != null) {
                    fragmentHosts.add(fragmentHost);
                }
            }

            //
            final List<Bundle> installedBundleList = new LinkedList<Bundle>();
            for (File jarFile : jarList) {
                // Extract version and symbolic name of the bundle
                String key="";
                BundleReference jarRef;
                try {
                    List<PackageDeclaration> packageDeclarations = new ArrayList<PackageDeclaration>();
                    jarRef = parseJarManifest(jarFile, packageDeclarations);
                    key = jarRef.getArtifactId();
                } catch (IOException ex) {
                    LOGGER.error(ex.getLocalizedMessage(),ex);
                    // Do not install this jar
                    continue;
                }
                // Retrieve from the framework cache the bundle at this location
                Bundle installedBundle = installedBundleMap.remove(key);

                // Read Jar manifest without installing it
                BundleReference reference = new BundleReference(""); // Default deploy
                try {
                    JarFile jar = new JarFile(jarFile);
                    Manifest manifest = jar.getManifest();
                    if(manifest!=null && manifest.getMainAttributes()!=null) {
                        String artifact = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
                        BundleReference customRef = customDeployBundles.get(artifact);
                        if(customRef!=null) {
                            reference = customRef;
                        }
                    }

                } catch (Exception ex) {
                    LOGGER.error(I18N.tr("Could not read bundle manifest"),ex);
                }

                try {
                    if(installedBundle != null) {
                        if(getFragmentHost(installedBundle) != null) {
                            // Fragment cannot be reinstalled
                            continue;
                        } else {
                            String installedBundleLocation = installedBundle.getLocation();
                            int verDiff = -1;
                            if(installedBundle.getVersion() != null && jarRef.getVersion()!=null) {
                                verDiff = installedBundle.getVersion().compareTo(jarRef.getVersion());
                            }
                            if (verDiff == 0) {
                                // If the same version or SNAPSHOT that is not used by fragments
                                if (!fragmentHosts.contains(installedBundle.getSymbolicName()) && (!installedBundleLocation.equals(jarFile.toURI().toString()) ||
                                        (installedBundle.getVersion() != null && "SNAPSHOT".equals(installedBundle.getVersion().getQualifier())))) {
                                    //if the location is not the same reinstall it
                                    LOGGER.info("Uninstall bundle " + installedBundle.getSymbolicName());
                                    installedBundle.uninstall();
                                    installedBundle = null;
                                }
                            } else if (verDiff < 0) {
                                // Installed version is older than the bundle version
                                LOGGER.info("Uninstall bundle " + installedBundle.getLocation());
                                installedBundle.uninstall();
                                installedBundle = null;
                            } else {
                                // Installed version is more recent than the bundle version
                                // Do not install this jar
                                continue;
                            }
                        }
                    }
                    // If the bundle is not in the framework cache install it
                    if ((installedBundle == null) && reference.isAutoInstall()) {
                        installedBundle = hostBundle.installBundle(jarFile.toURI().toString());
                        LOGGER.info("Install bundle "+installedBundle.getSymbolicName());
                        if (!isFragment(installedBundle) && reference.isAutoStart()) {
                            installedBundleList.add(installedBundle);
                        }
                    }
                }
                catch (BundleException ex) {
                    LOGGER.error("Error while installing bundle in bundle directory",ex);
                }
            }
            // Start new bundles
            for (Bundle bundle :installedBundleList) {
                try {
                    bundle.start();
                } catch (BundleException ex) {
                    LOGGER.error("Error while starting bundle in bundle directory",ex);
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
                    String value = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
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
     * @param manifest Jar Manifest
     * @param packages package array or null
     * @throws IOException
     */
    public static BundleReference parseManifest(Manifest manifest, List<PackageDeclaration> packages) throws IOException {
        Attributes attributes = manifest.getMainAttributes();
        String exports = attributes.getValue(Constants.EXPORT_PACKAGE);
        String versionProperty = attributes.getValue(Constants.BUNDLE_VERSION_ATTRIBUTE);
        Version version=null;
        if(versionProperty!=null) {
            version = new Version(versionProperty);
        }
        String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
        if(packages != null) {
            org.apache.felix.framework.Logger logger = new org.apache.felix.framework.Logger();
            // Use Apache Felix to parse the Manifest Export header
            List<BundleCapability> exportsCapability = ManifestParser.parseExportHeader(logger, null, exports, "0",
                    new Version(0, 0, 0));
            for (BundleCapability bc : exportsCapability) {
                Map<String, Object> attr = bc.getAttributes();
                // If the package contain a package name and a package version
                if (attr.containsKey(PACKAGE_NAMESPACE)) {
                    Version packageVersion = new Version(0, 0, 0);
                    if (attr.containsKey(Constants.VERSION_ATTRIBUTE)) {
                        packageVersion = (Version) attr.get(Constants.VERSION_ATTRIBUTE);
                    }
                    if (packageVersion.getMajor() != 0 || packageVersion.getMinor() != 0 || packageVersion.getMicro() != 0) {
                        packages.add(new PackageDeclaration((String) attr.get(PACKAGE_NAMESPACE),
                                packageVersion));
                    } else {
                        // No version, take the bundle version
                        packages.add(new PackageDeclaration((String) attr.get(PACKAGE_NAMESPACE),
                                version));
                    }
                }
            }
        }
        return new BundleReference(symbolicName,version);
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

    private static BundleReference parseJarManifest(File jarFilePath, List<PackageDeclaration> packages) throws IOException {
        JarFile jar = new JarFile(jarFilePath);
        return parseManifest(jar.getManifest(), packages);
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
                    LOGGER.debug("Unable to fetch packages in "+filePath.getAbsolutePath());
                }
            } else if (filePath.isDirectory()) {
                try {
                    parseDirectory(filePath,filePath, packages);
                } catch (SecurityException ex) {
                    LOGGER.debug("Unable to fetch the folder "+filePath.getAbsolutePath());
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
