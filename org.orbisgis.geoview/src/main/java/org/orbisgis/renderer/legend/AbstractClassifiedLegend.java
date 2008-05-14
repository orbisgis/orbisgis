package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;


abstract class AbstractClassifiedLegend extends AbstractSimpleLegend implements
		ClassifiedLegend {

	private String fieldName;
	private Symbol defaultSymbol;
	private ArrayList<Symbol> symbols;

	public AbstractClassifiedLegend() {
		defaultSymbol = SymbolFactory.createNullSymbol();
	}

	public void setDataSource(SpatialDataSourceDecorator ds)
			throws DriverException {
		super.setDataSource(ds);
		invalidateCache();
	}

	public void setClassificationField(String fieldName) throws DriverException {
		this.fieldName = fieldName;
		invalidateCache();
	}

	public void setDefaultSymbol(Symbol defaultSymbol) {
		this.defaultSymbol = defaultSymbol;
		invalidateCache();
	}

	public Symbol getDefaultSymbol() {
		return defaultSymbol;
	}

	protected void invalidateCache() {
		symbols = null;
	}

	protected ArrayList<Symbol> getSymbols() throws RenderException {
		if (symbols == null) {
			if (getDataSource() == null) {
				throw new RenderException("The legend is "
						+ "not associated to a DataSource");
			}
			if (fieldName == null) {
				throw new RenderException("No classification field specified");
			}
			int fieldIndex;
			try {
				fieldIndex = getDataSource().getFieldIndexByName(fieldName);
				if (fieldIndex == -1) {
					throw new RenderException("There is no '" + fieldName
							+ "' field in the source: "
							+ getDataSource().getName());
				}
				symbols = doClassification(fieldIndex);
			} catch (DriverException e) {
				throw new RenderException("Error while accessing the "
						+ "data: cannot draw", e);
			}
		}

		return symbols;
	}

	protected abstract ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException, RenderException;

	public Symbol getSymbol(long row) throws RenderException {
		ArrayList<Symbol> symbols = getSymbols();
		Symbol ret;
		if (symbols == null) {
			ret = getDefaultSymbol();
		} else {
			ret = symbols.get((int) row);
		}
		return ret;
	}

	protected String getClassificationField() {
		return fieldName;
	}

}
