package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class DBDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		Driver rod = driver;
		return ((rod.getSupportedType() & SourceManager.DB) == SourceManager.DB);
	}

}
