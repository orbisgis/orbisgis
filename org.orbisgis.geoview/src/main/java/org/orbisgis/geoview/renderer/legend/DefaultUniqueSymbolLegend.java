package org.orbisgis.geoview.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

public class DefaultUniqueSymbolLegend extends AbstractLegend implements
		UniqueSymbolLegend {

	private Symbol symbol;
	private ArrayList<Symbol> symbols;

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	private ArrayList<Symbol> getSymbols() throws RenderException {
		if (symbols == null) {
			if (getDataSource() == null) {
				throw new RenderException("The legend is "
						+ "not associated to a DataSource");
			}
			try {
				symbols = doClassification();
			} catch (DriverException e) {
				throw new RenderException("Error while accessing the "
						+ "data: cannot draw", e);
			}
		}

		return symbols;
	}

	protected ArrayList<Symbol> doClassification() throws DriverException,
			RenderException {
		SpatialDataSourceDecorator sds = getDataSource();
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Symbol symbolToDraw = RenderUtils.buildSymbolToDraw(getSymbol(),
					sds.getGeometry(i));
			if (symbolToDraw != null) {
				ret.add(symbolToDraw);
			} else {
				ret.add(SymbolFactory.createNullSymbol());
			}
		}

		return ret;
	}

	public Symbol getSymbol(long row) throws RenderException {
		ArrayList<Symbol> symbols = getSymbols();
		Symbol ret;
		if (symbols == null) {
			ret = getSymbol();
		} else {
			ret = symbols.get((int) row);
		}
		return ret;
	}

}
