package org.orbisgis.geoview.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ProportionnalMethod {

	private DataSource ds;
	private String fieldName;
	private double maxValue;

	private final static int MIN_SURFACE = 10;

	// The surface reference must be greater or equals than 10.
	private int surfRef;

	public ProportionnalMethod(DataSource ds, String fieldName) {
		this.ds = ds;
		this.fieldName = fieldName;
	}

	// TODO what the surfRef parameter is used to
	public void build(int surfRef) throws DriverException {

		if (surfRef >= 10) {
			this.surfRef = surfRef;
		} else {
			this.surfRef = MIN_SURFACE;
		}

		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);

		maxValue = valeurs[valeurs.length - 1];

		// Work on progress for more proportional methods
		// int i = 0;
		// int minIndex = 0;
		// while (valeurs[minIndex] == Double.MIN_VALUE) {
		// minIndex++;
		// }
		//
		// double legMinValeur = valeurs[minIndex];
		// double legMedValeur = valeurs[(int) (valeurs.length / 2)];
		// double legMaxValeur = valeurs[valeurs.length - 1];
		// int legMinIndex = valeurs.length + 1;
		// int legMedIndex = valeurs.length + 1;
		// int legMaxIndex = valeurs.length + 1;
		//
		// int fieldIndex;
		// fieldIndex = ds.getFieldIndexByName(fieldName);
		//
		// double value;
		// for (i = 0; i < valeurs.length; i++) {
		//
		// value = ds.getFieldValue(i, fieldIndex).getAsDouble();
		//
		// if (value == valeurs[minIndex]) {
		// legMinIndex = i;
		// } else if (value == valeurs[(int) (valeurs.length / 2)]) {
		// legMedIndex = i;
		// } else if (value == valeurs[valeurs.length - 1]) {
		// legMaxIndex = i;
		// }
		// }

	}

	public double getSymbolCoef() {
		return surfRef / maxValue;
	}

}
