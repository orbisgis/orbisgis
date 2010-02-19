package org.gdms.source;

import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class NotDriverFilter implements DriverFilter {

	private DriverFilter filter;

	public NotDriverFilter(DriverFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean acceptDriver(Driver driver) {
		return !filter.acceptDriver(driver);
	}
}
