package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.classification.ProportionalMethod;
import org.orbisgis.pluginManager.PluginManager;

public class DefaultProportionalLegend extends AbstractClassifiedLegend
		implements ProportionalLegend {

	private static final int LINEAR = 1;
	private static final int SQUARE = 2;
	private static final int LOGARITHMIC = 3;

	private int minSymbolArea = 3000;
	private int method = LINEAR;
	private double sqrtFactor;

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();

		ProportionalMethod proportionnalMethod = new ProportionalMethod(
				getDataSource(), getClassificationField());
		proportionnalMethod.build(minSymbolArea);

		int rowCount = (int) getDataSource().getRowCount();
		// TODO what's the use of this variable
		int coefType = 1;

		double symbolSize = 0;
		for (int i = 0; i < rowCount; i++) {
			double value = getDataSource().getFieldValue(i, fieldIndex)
					.getAsDouble();

			switch (method) {

			case LINEAR:
				symbolSize = proportionnalMethod.getLinearSize(value, coefType);

				break;

			case SQUARE:
				symbolSize = proportionnalMethod.getSquareSize(value,
						sqrtFactor, coefType);

				break;

			case LOGARITHMIC:

				symbolSize = proportionnalMethod.getLogarithmicSize(value,
						coefType);

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

	public void setLinearMethod() throws DriverException {
		method = LINEAR;
		setDataSource(getDataSource());
	}

	public void setSquareMethod(double sqrtFactor) throws DriverException {
		method = SQUARE;
		this.sqrtFactor = sqrtFactor;

		setDataSource(getDataSource());

	}

	public void setLogarithmicMethod() throws DriverException {
		method = LOGARITHMIC;
		setDataSource(getDataSource());

	}

}
