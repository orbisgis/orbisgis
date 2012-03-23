package org.gdms.driver.driverManager;

import org.gdms.driver.Driver;

public interface DriverFilter {

    boolean acceptDriver(Driver driver);
}
