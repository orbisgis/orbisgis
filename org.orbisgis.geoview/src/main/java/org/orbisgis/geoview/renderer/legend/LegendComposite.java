package org.orbisgis.geoview.renderer.legend;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

public class LegendComposite extends AbstractLegend implements Legend {

	private Legend[] legends;

	public LegendComposite(Legend[] legends) {
		this.legends = legends;
	}

	public Legend[] getLegends() {
		return legends;
	}

	public Symbol getSymbol(long row) throws RenderException {
		return legends[getLayer()].getSymbol(row);
	}

	public void setDataSource(SpatialDataSourceDecorator ds)
			throws DriverException {
		super.setDataSource(ds);
		for (Legend classification : legends) {
			classification.setDataSource(ds);
		}
	}

	public int getNumLayers() {
		return legends.length;
	}

}
