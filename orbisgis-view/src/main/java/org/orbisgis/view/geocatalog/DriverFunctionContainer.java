package org.orbisgis.view.geocatalog;

import org.h2gis.h2spatialapi.DriverFunction;

/**
 * A class that contains jdbc driver function.
 * @author Nicolas Fortin
 */
public interface DriverFunctionContainer {
    /**
     * @param driverFunction Driver function to add
     */
    void addDriverFunction(DriverFunction driverFunction);

    /**
     * @param driverFunction Driver function to remove
     */
    void removeDriverFunction(DriverFunction driverFunction);
}
