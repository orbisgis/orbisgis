package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.classification.ProportionnalMethod;
import org.orbisgis.pluginManager.PluginManager;

public class DefaultProportionalLegend extends AbstractClassifiedLegend
		implements ProportionalLegend {

	private int minSymbolArea = 3000;
	private int method = 1;
	private double sqrtFactor;

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();

		ProportionnalMethod proportionnalMethod = new ProportionnalMethod(
				getDataSource(), getClassificationField());
		proportionnalMethod.build(minSymbolArea);

		int rowCount = (int) getDataSource().getRowCount();
		int coefType = 1;

		double coefSymb;
		double surface;
		double symbolSize = 0;
		for (int i = 0; i < rowCount; i++) {
			double value = getDataSource().getFieldValue(i, fieldIndex)
					.getAsDouble();

			switch (method) {
			
			case 1:
			coefSymb = Math.abs(proportionnalMethod.getSymbolCoef());

			surface = Math.abs(value) * coefSymb;

			symbolSize = Math.sqrt(surface / coefType);
			
			break;
			
			case 2 :
			
			coefSymb = Math.abs(minSymbolArea / (Math.pow(proportionnalMethod.getMaxValue(), (1 / sqrtFactor))));
				surface = Math.pow(Math.abs(value), (1 / sqrtFactor)) * coefSymb;
				
			symbolSize= Math.sqrt(surface / coefType);
				
			break;
			
			case 3:
				
			coefSymb = Math.abs(minSymbolArea / Math.log(Math.abs(proportionnalMethod.getMaxValue())));
			surface = Math.abs(Math.log(Math.abs(value))) * coefSymb;
			
			symbolSize = Math.sqrt(surface / coefType);
				
			break;
			}
			ret.add(SymbolFactory.createCirclePolygonSymbol(Color.BLACK,
					Color.red, (int) Math.round(symbolSize)));
			
		}

		return ret;
	}

	public void setMinSymbolArea(int minSymbolArea) {
		this.minSymbolArea = minSymbolArea;

	}

	public void setLinearMethod() {
		method  = 1;
		try {
			setDataSource(getDataSource());
		} catch (DriverException e) {			
			PluginManager.error("Cannot refresh the layer: " + e.getMessage(), e);
		}
	}

	

	public void setSquareMethod(double sqrtFactor) {
		method  = 2;
		this.sqrtFactor = sqrtFactor;
		
		try {
			setDataSource(getDataSource());
		} catch (DriverException e) {			
			PluginManager.error("Cannot refresh the layer: " + e.getMessage(), e);
		}

	}
	
	public void setLogarithmicMethod() {
		method  = 3;
		try {
			setDataSource(getDataSource());
		} catch (DriverException e) {			
			PluginManager.error("Cannot refresh the layer: " + e.getMessage(), e);
		}

	}

}
