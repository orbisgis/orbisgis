package org.orbisgis.renderer.legend;

import org.gdms.driver.DriverException;

public interface LabelLegend extends ClassifiedLegend {

	/**
	 * Sets the field used to get the size of the label
	 *
	 * @param fieldName
	 *            Name to read in the DataSource to calculate the size
	 * @throws DriverException
	 *             If there is a problem reading the source of data
	 */
	public void setLabelSizeField(String fieldName) throws DriverException;

	public void setFontSize(int fontSize);

}
