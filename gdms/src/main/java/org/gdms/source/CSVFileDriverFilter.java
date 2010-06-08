package org.gdms.source;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class CSVFileDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		if (driver instanceof CSVStringDriver) {
			ReadOnlyDriver rod = (ReadOnlyDriver) driver;
			return ((rod.getType() & SourceManager.FILE) == SourceManager.FILE);
		}
		return false;
	}

}
