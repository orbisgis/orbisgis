package org.gdms.source;

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverFilter;

public class RasterDriverFilter implements DriverFilter {

	@Override
	public boolean acceptDriver(Driver driver) {
		Driver rod = driver;
		return ((rod.getSupportedType() & SourceManager.RASTER) == SourceManager.RASTER);
	}

}
