package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.csv.CSVDriver;
import org.gdms.driver.driverManager.DriverFilter;

public class CSVFileDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		if (driver instanceof CSVDriver) {
			Driver rod = driver;
			return ((rod.getType() & SourceManager.FILE) == SourceManager.FILE);
		}
		return false;
	}

}
