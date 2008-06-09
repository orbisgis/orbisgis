package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultUniqueValueLegend extends AbstractClassifiedLegend
		implements UniqueValueLegend {

	private ArrayList<Value> values = new ArrayList<Value>();
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

	public void addClassification(Value value, Symbol symbol) {
		values.add(value);
		symbols.add(symbol);
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
			Symbol classificationSymbol = this.symbols.get(values
					.indexOf(value));
			if (classificationSymbol != null) {
				Symbol symbol = RenderUtils.buildSymbolToDraw(
						classificationSymbol, geom);
				symbols.add(symbol);
			} else {
				symbols.add(getDefaultSymbol());
			}
		}
		return symbols;
	}

	public String getLegendTypeName() {
		return "Unique Value Legend";
	}

	public Value[] getClassificationValues() {
		return values.toArray(new Value[0]);
	}

	public Symbol getValueSymbol(Value value) {
		return symbols.get(values.indexOf(value));
	}

}
