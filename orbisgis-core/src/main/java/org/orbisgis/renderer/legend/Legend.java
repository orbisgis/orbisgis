package org.orbisgis.renderer.legend;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

/**
 * Interface used by the layer model and the renderer to draw the sources
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface Legend {

	/**
	 * Gets the symbol to draw the specified row of the DataSource specified by
	 * the setDataSource method
	 *
	 * @param row
	 * @return
	 * @throws RenderException
	 *             if there is some problem that makes impossible the drawing of
	 *             the layer
	 */
	Symbol getSymbol(long row) throws RenderException;

	/**
	 * associates the specified DataSource with this legend. This method is
	 * suitable to perform a catch of the symbols for each of the row in the
	 * DataSource
	 *
	 * @param ds
	 * @throws DriverException
	 *             If there is some problem exploring the DataSource
	 */
	void setDataSource(SpatialDataSourceDecorator ds) throws DriverException;

	/**
	 * Gets the legend's name
	 *
	 * @return
	 */
	String getName();

	/**
	 * Sets the legend's name. Only meaningful for user interface purposes
	 *
	 * @param name
	 */
	void setName(String name);

	/**
	 * Gets the name of the legend type
	 *
	 * @return
	 */
	String getLegendTypeName();

}
