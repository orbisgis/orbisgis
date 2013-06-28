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

import org.apache.log4j.Logger;
import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Watch for GDMS SQL Driver services and register them to the driver manager.
 * This extension keep the reference of a driver by its name.
 * @author Nicolas Fortin
 */
public class DriverTracker extends ServiceTracker<Driver, String> {
    private DriverManager driverManager;
    private BundleContext bundleContext;
    private static final Logger LOGGER = Logger.getLogger(DriverTracker.class);

    /**
     *
     * @param context The bundle context to be used by the tracker.
     * @param driverManager GDMS Sql function manager
     */
    public DriverTracker(BundleContext context, DriverManager driverManager) {
        super(context, Driver.class, null);
        this.driverManager = driverManager;
        this.bundleContext = context;
    }
    
    /**
     * Overrides the <tt>ServiceTracker</tt> functionality to inform
     * the application object about the added service.
     * @param ref The service reference of the added service.
     * @return The service object to be used by the tracker.
    **/
    @Override
    public String addingService(ServiceReference<Driver> ref)
    {
        Driver newDriver = bundleContext.getService(ref);
        String name = "";
        // If the error is not catch then all other Functions services are skipped.
        try {
                driverManager.registerDriver(newDriver.getClass());
                name = newDriver.getDriverId();
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
    public void modifiedService(ServiceReference<Driver> ref, String svc)
    {
        // We do not track Service property change
    }

    /**
     * Overrides the <tt>ServiceTracker</tt> functionality to inform
     * the application object about the removed service.
     * @param ref The service reference of the removed service.
     * @param svc The service object of the removed service.
    **/
    @Override
    public void removedService(ServiceReference<Driver> ref, String svc)
    {
        driverManager.unregisterDriver(svc);
    }
}
