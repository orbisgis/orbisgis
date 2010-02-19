package org.gdms.source;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class DBDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		ReadOnlyDriver rod = (ReadOnlyDriver) driver;
		return ((rod.getType() & SourceManager.DB) == SourceManager.DB);
	}

}
