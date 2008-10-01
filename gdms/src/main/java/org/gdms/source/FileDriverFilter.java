package org.gdms.source;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class FileDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		ReadOnlyDriver rod = (ReadOnlyDriver) driver;
		return ((rod.getType() & SourceManager.FILE) == SourceManager.FILE);
	}

}
