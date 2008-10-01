package org.gdms.source;

import org.gdms.driver.ReadWriteDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class WritableDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		return driver instanceof ReadWriteDriver;
	}

}
