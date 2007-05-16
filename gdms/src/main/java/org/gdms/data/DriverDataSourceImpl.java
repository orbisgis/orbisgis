package org.gdms.data;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.spatial.PTTypes;


public class DriverDataSourceImpl {
    
    private ReadOnlyDriver driver;

    public DriverDataSourceImpl(ReadOnlyDriver driver) {
        this.driver = driver;
    }

    public int getType(String driverType) {
        if (driverType.equals(PTTypes.STR_GEOMETRY)) {
            return PTTypes.GEOMETRY;
        }
        return driver.getType(driverType);
    }
    
}
