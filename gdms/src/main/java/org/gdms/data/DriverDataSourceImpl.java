package org.gdms.data;

import org.gdms.driver.GDBMSDriver;
import org.gdms.spatial.PTTypes;


public class DriverDataSourceImpl {
    
    private GDBMSDriver driver;

    public DriverDataSourceImpl(GDBMSDriver driver) {
        this.driver = driver;
    }

    public int getType(String driverType) {
        if (driverType.equals(PTTypes.STR_GEOMETRY)) {
            return PTTypes.GEOMETRY;
        }
        return driver.getType(driverType);
    }
    
}
