package org.orbisgis.coreapi.api;

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

    /**
     * Found DriverFunction using file extension and driver type
     * @param ext Driver extension ex:shp
     * @param type Driver type, copy or link
     * @return Driver instance or null if not found.
     */
    DriverFunction getDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type );
}
