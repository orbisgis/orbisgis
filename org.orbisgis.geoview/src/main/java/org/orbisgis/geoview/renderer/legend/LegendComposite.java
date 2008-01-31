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
		Symbol[] symbols = new Symbol[legends.length];
		for (int i = 0; i < legends.length; i++) {
			symbols[i] = legends[i].getSymbol(row);
		}
		Symbol ret = SymbolFactory.createSymbolComposite(symbols);
		return ret;
	}

	public void setDataSource(SpatialDataSourceDecorator ds)
			throws DriverException {
		super.setDataSource(ds);
		for (Legend classification : legends) {
			classification.setDataSource(ds);
		}
	}

}
