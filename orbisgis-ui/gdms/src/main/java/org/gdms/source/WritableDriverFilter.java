package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class WritableDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		return driver.isCommitable();
	}

}
