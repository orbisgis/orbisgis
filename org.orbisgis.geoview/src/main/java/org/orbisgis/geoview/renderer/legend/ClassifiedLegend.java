package org.orbisgis.geoview.renderer.legend;

import org.gdms.driver.DriverException;

public interface ClassifiedLegend extends Legend {

	/**
	 * Sets the default symbol for those features that does not match any of the
	 * classifications in this legend. By default this symbol is null and those
	 * features won't be drawn
	 *
	 * @param lesoutres
	 */
	void setDefaultSymbol(Symbol defaultSymbol);

	/**
	 * Sets the field used to classify the features
	 *
	 * @param fieldName
	 *            Name to read in the DataSource to test against the
	 *            classification values
	 * @throws DriverException
	 *             If there is a problem reading the source of data
	 */
	public void setClassificationField(String fieldName) throws DriverException;


}
