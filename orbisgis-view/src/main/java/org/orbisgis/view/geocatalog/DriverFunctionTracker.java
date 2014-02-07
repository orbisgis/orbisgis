package org.orbisgis.view.geocatalog;

import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.coreapi.api.DriverFunctionContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Nicolas Fortin
 */
public class DriverFunctionTracker implements ServiceTrackerCustomizer<DriverFunction, DriverFunction> {
    private DriverFunctionContainer driverFunctionContainer;
    private BundleContext bundleContext;


    /**
     * @param bundleContext Active bundle context
     * @param driverFunctionContainer Instance of container of DriverFunction
     */
    public DriverFunctionTracker(BundleContext bundleContext, DriverFunctionContainer driverFunctionContainer) {
        this.bundleContext = bundleContext;
        this.driverFunctionContainer = driverFunctionContainer;
    }

    @Override
    public DriverFunction addingService(ServiceReference<DriverFunction> reference) {
        DriverFunction service = bundleContext.getService(reference);
        driverFunctionContainer.addDriverFunction(service);
        return service;
    }

    @Override
    public void modifiedService(ServiceReference<DriverFunction> reference, DriverFunction service) {
        // No property to read
    }

    @Override
    public void removedService(ServiceReference<DriverFunction> reference, DriverFunction service) {
        driverFunctionContainer.removeDriverFunction(service);
    }
}
