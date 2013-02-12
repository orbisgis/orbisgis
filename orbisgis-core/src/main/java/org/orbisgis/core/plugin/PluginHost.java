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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.log4j.Logger;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
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
    private FunctionTracker functionTracker; // Track Gdms Function Services
    private Framework framework;
    private final static int STOP_TIMEOUT = 15000;
    private static final Logger LOGGER = Logger.getLogger(PluginHost.class);
    private File pluginCacheFolder;
    private List<PackageDeclaration> packageList = new ArrayList<PackageDeclaration>();
    /**
     * 
     * @param pluginCacheFolder Cache folder
     */
    public PluginHost(File pluginCacheFolder) {
        this.pluginCacheFolder = pluginCacheFolder;
        packageList.add(new PackageDeclaration("org.orbisgis.view", 4,0,0));
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
    private String getExtraPackage() {
        //Build a set of packages to skip programmaticaly defined packages
        Set<String> packagesName = new HashSet<String>();
        List<String> sortedPackagesExport = new ArrayList<String>();
        for(PackageDeclaration packInfo : packageList) {
            packagesName.add(packInfo.getPackageName());
            addPackage(packInfo,sortedPackagesExport);
        }
        // Fetch built-ins OSGi bundles package declarations
        Collection<PackageDeclaration> packageDeclarations = BundleTools.fetchManifests();
        for(PackageDeclaration packageDeclaration : packageDeclarations) {
            if(!packagesName.contains(packageDeclaration.getPackageName())) {
                packagesName.add(packageDeclaration.getPackageName());
                addPackage(packageDeclaration,sortedPackagesExport);
            }
        }
        // Export Host provided packages, by classpaths
        List<String> classPathExtensions = BundleTools.getAvailablePackages();
        for(String ext : classPathExtensions) {
            if(!packagesName.contains(ext) && !ext.startsWith("org.osgi.")) {
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
     * Start the Framework
     */
    public void start() {
        Map<String, String> frameworkConfig = new HashMap<String,String>();
        // Define service interface exported by Framework orbisgis-core
        frameworkConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,getExtraPackage());
        // Persistance data
        frameworkConfig.put(Constants.FRAMEWORK_STORAGE, pluginCacheFolder.getAbsolutePath());
        framework = createEmbeddedFramework(frameworkConfig);
        try {
            framework.init();
            framework.start();  
            openTrackers();
        } catch(BundleException ex) {
            LOGGER.error(ex.getLocalizedMessage(),ex);
        }
    }
    private void openTrackers() {
            //Track GDMS Function services
            functionTracker =  new FunctionTracker(framework.getBundleContext(),
            Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager());
            functionTracker.open();            
    }
    private void closeTrackers() {
            functionTracker.close();
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
}
