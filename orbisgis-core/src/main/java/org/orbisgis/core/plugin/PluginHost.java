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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
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
    private Framework framework;
    private final static int STOP_TIMEOUT = 15000;
    private static final Logger LOGGER = Logger.getLogger(PluginHost.class);
    
    public PluginHost(File pluginCacheFolder) {
        Map<String, String> frameworkConfig = new HashMap<String,String>();
        // Define service interface exported by Framework orbisgis-core
        frameworkConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,getExtraPackage());
        // Persistance data
        frameworkConfig.put(Constants.FRAMEWORK_STORAGE, pluginCacheFolder.getAbsolutePath());
        framework = createEmbeddedFramework(frameworkConfig);
    }
    
    private String getExtraPackage() {
        StringBuilder sb = new StringBuilder();   
        // Export GDMS Function service
        sb.append( "org.gdms.data,"
                + "org.gdms.sql.function,"
                +"org.gdms.data.types,"
                + "org.gdms.data.values,");
        // Core export framework
        sb.append("org.osgi.framework version=1.6");
        return sb.toString();
    }
    /**
     * Start the Framework
     */
    public void start() {
        try {
            framework.init();
            framework.start();  
            // Start the host activator
            Activator hostActivator = new Activator();
            hostActivator.start(getHostBundleContext());
        } catch(BundleException ex) {
            LOGGER.error(ex.getLocalizedMessage(),ex);
        }
    }
    /**
     * Stop the host Framework, and wait that all bundles are stopped
     * @throws BundleException
     * @throws InterruptedException 
     */
    public void stop() throws BundleException, InterruptedException {
        framework.stop();
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
    private static Framework createEmbeddedFramework(Map<String, String> frameworkConfig)
    {
        ServiceLoader<FrameworkFactory> factoryLoader = ServiceLoader.load(FrameworkFactory.class);
        Iterator<FrameworkFactory> it = factoryLoader.iterator();
        if(!it.hasNext()) {
            throw new IllegalStateException("FrameworkFactory service could not be created.");
        }
        return it.next().newFramework(frameworkConfig);        
    }
}
