package org.orbisgis.geoview.renderer.legend;

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
	 * Gets the legends that are contained in this legend. It will return some
	 * result if this is a composite of legends. Otherwise it will return an
	 * empty array
	 *
	 * @return
	 */
	Legend[] getLegends();

}
