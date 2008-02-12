package org.orbisgis.geoview.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ProportionnalMethod {

	private DataSource ds;
	private String fieldName;
	private double maxValue;

	private final static int MIN_SURFACE = 10;

	// The surface reference must be greater or equals than 10.
	private int minSymbolArea;
	
	

	public ProportionnalMethod(DataSource ds, String fieldName) {
		this.ds = ds;
		this.fieldName = fieldName;
	}

	// TODO what the surfRef parameter is used to
	public void build(int minSymbolArea) throws DriverException {

		if (minSymbolArea >= 10) {
			this.minSymbolArea = minSymbolArea;
		} else {
			this.minSymbolArea = MIN_SURFACE;
		}

		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);

		maxValue = valeurs[valeurs.length - 1];


	}
	
	

	public double getSymbolCoef() {
		return minSymbolArea / maxValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	
	
	

}
