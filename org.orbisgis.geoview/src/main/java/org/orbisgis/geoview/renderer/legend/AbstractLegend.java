package org.orbisgis.geoview.renderer.legend;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

public abstract class AbstractLegend implements Legend {

	private SpatialDataSourceDecorator sds;

	public void setDataSource(SpatialDataSourceDecorator ds)
			throws DriverException {
		this.sds = ds;
	}

	public SpatialDataSourceDecorator getDataSource() {
		return sds;
	}

	public Legend[] getLegends() {
		return new Legend[0];
	}
}
