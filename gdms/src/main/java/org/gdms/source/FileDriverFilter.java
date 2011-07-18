package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class FileDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		Driver rod = driver;
		return ((rod.getType() & SourceManager.FILE) == SourceManager.FILE);
	}

}
