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
package org.orbisgis.framework;

import java.io.File;
import java.util.*;

import org.apache.felix.framework.Logger;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Core internal manager for OSGI Framework
 * @author Nicolas Fortin
 */
public class PluginHost {
    private Framework framework;
    private final static int STOP_TIMEOUT = 15000;
    private final static int BUNDLE_STATE_CHECK_INTERVAL = 100;
    private File pluginCacheFolder;
    private List<PackageDeclaration> packageList = new ArrayList<PackageDeclaration>();
    private Logger LOGGER;
    /**
     * 
     * @param pluginCacheFolder Cache folder
     */
    public PluginHost(File pluginCacheFolder, Logger logger) {
        this.pluginCacheFolder = pluginCacheFolder;
        packageList.add(new PackageDeclaration("org.orbisgis.view", 4,0,0));
        this.LOGGER = logger;
    }
    /**
     * The host will automatically export all packages, but without version information.
     * This method give the ability to introduce a package constraint on export, like version number.
     * @param packageInfo Extanded package information, providing a new version
     * to packages help to identify incompatibilities between bundles.
     */
    public void exportCorePackage(PackageDeclaration packageInfo) throws IllegalStateException {
        if(framework!=null) {
            throw new IllegalStateException("The OSGI framework has been already initialised");
        }
        packageList.add(packageInfo);
    }
    private void addPackage(PackageDeclaration packInfo,List<String> sortedPackagesExport) {
        if(!packInfo.isVersionDefined()) {
            sortedPackagesExport.add(packInfo.getPackageName());
        } else {
            sortedPackagesExport.add(packInfo.getPackageName()+
                    "; version="+packInfo.getVersion());
        }
    }
    /**
     * Parse classpath to find all packages name available. Write them all without version information,
     * Except for defined packages through exportCorePackage(), 
     * @return 
     */
    private String getExtraPackage(Set<String> ignorePackages) {
        BundleTools bundleTools = new BundleTools(LOGGER);
        //Build a set of packages to skip programmaticaly defined packages
        Set<String> packagesName = new HashSet<>(ignorePackages);
        List<String> sortedPackagesExport = new ArrayList<String>();
        for(PackageDeclaration packInfo : packageList) {
            packagesName.add(packInfo.getPackageName());
            addPackage(packInfo,sortedPackagesExport);
        }
        // Fetch built-ins OSGi bundles package declarations
        Collection<PackageDeclaration> packageDeclarations = bundleTools.fetchManifests();
        for(PackageDeclaration packageDeclaration : packageDeclarations) {
            if(!packagesName.contains(packageDeclaration.getPackageName())) {
                packagesName.add(packageDeclaration.getPackageName());
                addPackage(packageDeclaration,sortedPackagesExport);
            }
        }
        // Export Host provided packages, by classpaths
        List<String> classPathExtensions = bundleTools.getAvailablePackages();
        for(String ext : classPathExtensions) {
            if(!packagesName.contains(ext) && !ext.startsWith("org.osgi.") && !ext.startsWith("java.")) {
                sortedPackagesExport.add(ext);
            }            
        }
        // Sort export package
        Collections.sort(sortedPackagesExport);
        StringBuilder sb = new StringBuilder();        
        for(String ext : sortedPackagesExport) {
            if(sb.length()!=0) {
                sb.append(",");
            }
            sb.append(ext);
        }
        return sb.toString();
    }

    /**
     * Create a valid bundleContext, but do not start bundles
     */
    public void init(Map<String, String> frameworkConfig) {
        // Define service interface exported by Framework orbisgis-core
        Set<String> ignorePackages = new HashSet<>();
        String ignorePackageString = frameworkConfig.get("org.osgi.framework.system.packages.ignore");
        if(ignorePackageString != null) {
            StringTokenizer st = new StringTokenizer(ignorePackageString, ",");
            while(st.hasMoreTokens()) {
                String ignorePackage = st.nextToken();
                ignorePackages.add(ignorePackage);
            }
        }
        String fileExtraPackage = frameworkConfig.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        frameworkConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,getExtraPackage(ignorePackages)+(fileExtraPackage != null ? ","+fileExtraPackage : ""));
        // Apply ignore list on default export package
        if(!ignorePackages.isEmpty()) {
            StringBuilder newSysPackages = new StringBuilder();
            String defaultSysPackage = frameworkConfig.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
            if (defaultSysPackage == null) {
                defaultSysPackage = Util.getDefaultProperty(LOGGER, Constants.FRAMEWORK_SYSTEMPACKAGES);
            }
            StringTokenizer st = new StringTokenizer(defaultSysPackage, ",");
            while(st.hasMoreTokens()) {
                String packageDeclaration = st.nextToken();
                // Remove version and uses
                String packagePart = packageDeclaration.indexOf(';') > 0 ?
                        packageDeclaration.substring(0, packageDeclaration.indexOf(';')) : packageDeclaration;
                if(!ignorePackages.contains(packagePart.trim())) {
                    if(newSysPackages.length() != 0) {
                        newSysPackages.append(",");
                    }
                    newSysPackages.append(packageDeclaration);
                }
            }
            frameworkConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES, newSysPackages.toString());
        }
        // Persistence's data
        frameworkConfig.put(Constants.FRAMEWORK_STORAGE, pluginCacheFolder.getAbsolutePath());
        framework = createEmbeddedFramework(frameworkConfig);
        try {
            framework.init();
        } catch(BundleException ex) {
            LOGGER.log(Logger.LOG_ERROR, ex.getLocalizedMessage(), ex);
        }
    }
    /**
     * Start the Framework
     */
    public void start() {
        try {
            framework.start();  
            openTrackers();
        } catch(BundleException ex) {
            LOGGER.log(Logger.LOG_ERROR, ex.getLocalizedMessage(), ex);
        }
    }
    private void openTrackers() {
    }
    private void closeTrackers() {
    }
    /**
     * Stop the host Framework, and wait that all bundles are stopped
     * @throws BundleException
     * @throws InterruptedException 
     */
    public void stop() throws BundleException, InterruptedException {
        framework.stop();
        closeTrackers();
        framework.waitForStop(STOP_TIMEOUT);
    }
    /**
     * @return The OSGI host framework
     */
    final public Framework getFramework() {
        return framework;
    }    
    /**
     * @return The BundleContext of the host
     */
    final public BundleContext getHostBundleContext() {
        return framework.getBundleContext();
    }
    /**
     * Create an embedded Framework.
     * @param frameworkConfig Framework parameters
     * @return Framework instance
     * @throws IllegalStateException If the Framework cannot be created
     */
    private Framework createEmbeddedFramework(Map<String, String> frameworkConfig)
    {
        ServiceLoader<FrameworkFactory> factoryLoader = ServiceLoader.load(FrameworkFactory.class);
        Iterator<FrameworkFactory> it = factoryLoader.iterator();
        if(!it.hasNext()) {
            throw new IllegalStateException("FrameworkFactory service could not be created.");
        }
        return it.next().newFramework(frameworkConfig);        
    }

    /**
     * @param timeout Ms, wait this time max
     * @return True if the state is stable.
     */
    public boolean waitForBundlesStableState(int timeout) {
        long begin = System.currentTimeMillis();
        while(!isBundleStateStable()) {
            if(System.currentTimeMillis() > begin + timeout) {
                return isBundleStateStable();
            } else {
                try {
                    Thread.sleep(BUNDLE_STATE_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    return isBundleStateStable();
                }
            }
        }
        return isBundleStateStable();
    }

    private boolean isBundleStateStable() {
        Set<Integer> unStableStates = new HashSet<Integer>(Arrays.asList(new Integer[]{
                Bundle.SIGNERS_ALL,
                Bundle.SIGNERS_TRUSTED,
                Bundle.START_ACTIVATION_POLICY,
                Bundle.STARTING,
                Bundle.STOP_TRANSIENT,
                Bundle.STOPPING}));
        for (Bundle bundle : getHostBundleContext().getBundles()) {
            if(unStableStates.contains(bundle.getState())) {
                return false;
            }
        }
        return true;
    }
}
