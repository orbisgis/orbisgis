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

import org.apache.log4j.Logger;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Watch for GDMS SQL Function services and register them to the function manager.
 * This extension keep the reference of a function by its name.
 * @author Nicolas Fortin
 */
public class FunctionTracker extends ServiceTracker<Function, String> {
    private FunctionManager functionManager;
    private BundleContext bundleContext;
    private static final Logger LOGGER = Logger.getLogger(FunctionTracker.class);
    
    /**
     * 
     * @param context The bundle context to be used by the tracker.
     * @param functionManager GDMS Sql function manager 
     */
    public FunctionTracker(BundleContext context, FunctionManager functionManager) {        
        super(context, Function.class, null);
        this.functionManager = functionManager;
        this.bundleContext = context;
    }
    
    /**
     * Overrides the <tt>ServiceTracker</tt> functionality to inform
     * the application object about the added service.
     * @param ref The service reference of the added service.
     * @return The service object to be used by the tracker.
    **/
    @Override
    public String addingService(ServiceReference<Function> ref)
    {
        Function newSqlFunction = bundleContext.getService(ref);
        String name = "";
        // If the error is not catch then all other Functions services are skipped.
        try {
                name = functionManager.addFunction(newSqlFunction.getClass());
        } catch (IllegalArgumentException ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
        }
        return name;
    }

    /**
     * Overrides the <tt>ServiceTracker</tt> functionality to inform
     * the application object about the modified service.
     * @param ref The service reference of the modified service.
     * @param svc The service object of the modified service.
    **/
    @Override
    public void modifiedService(ServiceReference<Function> ref, String svc)
    {
        // Remove then add this function
        functionManager.remove(svc);
        Function newSqlFunction = bundleContext.getService(ref);
        functionManager.addFunction(newSqlFunction.getClass());        
    }

    /**
     * Overrides the <tt>ServiceTracker</tt> functionality to inform
     * the application object about the removed service.
     * @param ref The service reference of the removed service.
     * @param svc The service object of the removed service.
    **/
    @Override
    public void removedService(ServiceReference<Function> ref, String svc)
    {
        functionManager.remove(svc);
    }
}
