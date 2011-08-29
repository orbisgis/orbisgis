package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverFilter;

/**
 * This filter checks that a driver is associated to a File, not to a DB instance or stored in memory.
 * @author alexis
 */
public class FileDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		Driver rod = driver;
		return (rod.getType() & SourceManager.FILE) == SourceManager.FILE;
	}

}
