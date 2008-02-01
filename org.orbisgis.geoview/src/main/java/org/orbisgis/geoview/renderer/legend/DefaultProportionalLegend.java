package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.classification.ProportionnalMethod;

public class DefaultProportionalLegend extends AbstractClassifiedLegend
		implements ProportionalLegend {

	private int minSymbolArea = 3000;

	// By default 0 = linear proportionnal method
	// 1 = rootsquare proportionnal method
	// 2 = log proportionnal method

	private int statisticMethod = 0;

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();

		ProportionnalMethod proportionnalMethod = new ProportionnalMethod(
				getDataSource(), getFieldName());
		proportionnalMethod.build(minSymbolArea);
		
		int rowCount = (int)getDataSource().getRowCount();
		int coefType = 1;
		
		double coefSymb;
		double surface;
		Boolean negatif;
		double symbolSize;
		for (int i = 0; i < rowCount; i++) {
			
			double value = getDataSource().getFieldValue(i, fieldIndex).getAsDouble();
			
			
				coefSymb = Math.abs(proportionnalMethod.surfRef
					/ proportionnalMethod.maxValue);			
			
			 surface = Math.abs(value) * coefSymb;			
			
			negatif = (value < 0);
			symbolSize = Math.sqrt(surface / coefType);
			ret.add(SymbolFactory.createCirclePolygonSymbol(Color.BLACK, Color.red,
					(int) Math.round(symbolSize)));
			
						
		}

		return ret;
	}

	public void setMinSymbolArea(int minSymbolArea) {
		this.minSymbolArea = minSymbolArea;

	}


}
