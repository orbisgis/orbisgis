package org.orbisgis.renderer.legend;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

public abstract class AbstractLegend implements Legend {

	private SpatialDataSourceDecorator sds;
	private int layer;
	private String name;

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

	public void setLayer(int i) {
		this.layer = i;
	}

	protected int getLayer() {
		return layer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
