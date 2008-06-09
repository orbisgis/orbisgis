package org.orbisgis.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ProportionalMethod {

	private DataSource ds;
	private String fieldName;
	private double maxValue;

	private final static int MIN_SURFACE = 10;

	// The surface reference must be greater or equals than 10.
	private int minSymbolArea;

	public ProportionalMethod(DataSource ds, String fieldName) {
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

	/**
	 * Compute the symbol size using a linear method
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 * 
	 * @param value
	 * @param coefType
	 * @return
	 */
	public double getLinearSize(double value, int coefType) {
		double coefSymb = Math.abs(getSymbolCoef());

		double surface = Math.abs(value) * coefSymb;

		return Math.sqrt(surface / coefType);
	}

	/**
	 * Compute the symbol size using a squareroot method
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 * 
	 * @param value
	 * @param sqrtFactor
	 * @param coefType
	 * @return
	 */
	public double getSquareSize(double value, double sqrtFactor, int coefType) {
		double coefSymb = Math.abs(minSymbolArea
				/ (Math.pow(getMaxValue(), (1 / sqrtFactor))));
		double surface = Math.pow(Math.abs(value), (1 / sqrtFactor)) * coefSymb;

		return Math.sqrt(surface / coefType);
	}

	/**
	 * Compute the symbol size using a logarithm  method
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 * 
	 * @param value
	 * @param coefType
	 * @return
	 */
	public double getLogarithmicSize(double value, int coefType) {
		double coefSymb = Math.abs(minSymbolArea
				/ Math.log(Math.abs(getMaxValue())));
		double surface = Math.abs(Math.log(Math.abs(value))) * coefSymb;

		return Math.sqrt(surface / coefType);
	}
}
