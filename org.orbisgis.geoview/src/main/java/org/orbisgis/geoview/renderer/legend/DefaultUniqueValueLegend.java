package org.orbisgis.geoview.renderer.legend;

import java.util.ArrayList;
import java.util.HashMap;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultUniqueValueLegend extends AbstractClassifiedLegend
		implements UniqueValueLegend {

	private HashMap<Value, Symbol> classifications = new HashMap<Value, Symbol>();

	public void addClassification(Value value, Symbol symbol) {
		classifications.put(value, symbol);
		invalidateCache();
	}

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		SpatialDataSourceDecorator sds = getDataSource();
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Value value = sds.getFieldValue(i, fieldIndex);
			Geometry geom = sds.getGeometry(i);
			Symbol symbol = RenderUtils.buildSymbolToDraw(classifications
					.get(value), geom);
			if (symbol != null) {
				symbols.add(symbol);
			} else {
				symbols.add(getDefaultSymbol());
			}
		}
		return symbols;
	}

}
