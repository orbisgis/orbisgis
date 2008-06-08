package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class DefaultIntervalLegend extends AbstractClassifiedLegend implements
		IntervalLegend {

	private ArrayList<Interval> intervals = new ArrayList<Interval>();
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		SpatialDataSourceDecorator sds = getDataSource();
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Value value = sds.getFieldValue(i, fieldIndex);
			Symbol classificationSymbol = getSymbolFor(value);
			if (classificationSymbol != null) {
				Symbol symbol = RenderUtils.buildSymbolToDraw(
						classificationSymbol, sds.getGeometry(i));
				symbols.add(symbol);
			} else {
				symbols.add(getDefaultSymbol());
			}
		}
		return symbols;

	}

	public Symbol getSymbolFor(Value value) {
		for (int i = 0; i < intervals.size(); i++) {
			if (intervals.get(i).contains(value)) {
				return symbols.get(i);
			}
		}

		return getDefaultSymbol();
	}
	
	public Symbol getSymbolInterval(Interval inter){
		for (int i=0;i<intervals.size(); i++){
			if (intervals.get(i).equals(inter)){
				return symbols.get(i);
			}
		}
		return SymbolFactory.createNullSymbol();
	}
	
	public ArrayList<Interval> getIntervals(){
		return intervals;
	}

	public void addInterval(Value initialValue, boolean minIncluded,
			Value finalValue, boolean maxIncluded, Symbol symbol) {
		intervals.add(new Interval(initialValue, minIncluded, finalValue,
				maxIncluded));
		symbols.add(symbol);
	}

	public void addIntervalWithMaxLimit(Value finalValue, boolean included,
			Symbol symbol) {
		intervals.add(new Interval(null, false, finalValue, included));
		symbols.add(symbol);
	}

	public void addIntervalWithMinLimit(Value initialValue, boolean included,
			Symbol symbol) {
		intervals.add(new Interval(initialValue, included, null, false));
		symbols.add(symbol);
	}

	public String getLegendTypeName() {
		return "Interval Classified Legend";
	}
	

}
