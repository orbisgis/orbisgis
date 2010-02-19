package org.gdms.source;

import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class AndDriverFilter implements DriverFilter {

	private DriverFilter[] filters;

	public AndDriverFilter(DriverFilter... filters) {
		this.filters = filters;
	}

	@Override
	public boolean acceptDriver(Driver driver) {
		for (DriverFilter filter : filters) {
			if (!filter.acceptDriver(driver)) {
				return false;
			}
		}
		
		return true;
	}
}
