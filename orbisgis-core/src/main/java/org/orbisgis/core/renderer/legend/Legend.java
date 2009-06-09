/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.renderer.legend;

import java.awt.Graphics2D;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.symbol.Symbol;

/**
 * Interface used by the layer model and the renderer to draw the sources
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface Legend {

	/**
	 * Indicates that when a row is modified, only its symbol is affected and
	 * should be recalculated
	 */
	int ONLY_AFFECTED = 1;

	/**
	 * Indicates that when a row is modified all the symbols can have been
	 * modified
	 */
	int ALL = 2;

	/**
	 * Adds a listener of legend changes to the legend.
	 *
	 * @param listener
	 */
	void addLegendListener(LegendListener listener);

	/**
	 * Removes a listener of legend changes from the legend
	 *
	 * @param listener
	 */
	void removeLegendListener(LegendListener listener);

	/**
	 * This method is called once when the DataSource is created to allow the
	 * legend to perform some global calculation like getting min and max
	 * values, etc.
	 *
	 * @param sds
	 *            DataSource of the layer this legend is associated to
	 * @throws RenderException
	 *             If there is some problem during the preprocess
	 */
	void preprocess(SpatialDataSourceDecorator sds) throws RenderException;

	/**
	 * Gets the symbol to draw the specified row of the specified DataSource
	 *
	 * @param sds
	 *            DataSource that will be drawn
	 * @param row
	 *            Row of the data source the returned symbol will draw
	 *
	 * @return The symbol to draw the specified row of the DataSource. Null
	 *         means the row won't be drawn
	 * @throws RenderException
	 *             if there is some problem that makes impossible the drawing of
	 *             the layer
	 */
	Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException;

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
	 * Returns an unique id. It can be whatever unique string. If this string
	 * changes, previous versions of the legend won't be read. For persistence
	 * purposes.
	 *
	 * @return
	 */
	String getLegendTypeId();

	/**
	 * Returns a human readable description of this implementation
	 *
	 * @return
	 */
	String getLegendTypeName();

	/**
	 * Returns this object as a JAXB object ready to be marshalled
	 *
	 * @return The JAXB object or null if this legend is not persistent
	 */
	Object getJAXBObject();

	/**
	 * Loads the legend
	 *
	 * @param jaxbObject
	 *            File to save the legend
	 */
	void setJAXBObject(Object jaxbObject);

	/**
	 * Creates a new empty instance of this legend
	 *
	 * @return
	 */
	Legend newInstance();

	/**
	 * Draws the image of the legend in the specified graphics.
	 *
	 * @param g
	 */
	void drawImage(Graphics2D g);

	/**
	 * Gets the size of the image being draw in the {@link drawImage} method
	 *
	 * @param g
	 *
	 * @return
	 */
	int[] getImageSize(Graphics2D g);

	/**
	 * Set the minimum scale to use this legend. Use Integer.MIN_VALUE to
	 * specify no limit
	 *
	 * @param min
	 */
	void setMinScale(int min);

	/**
	 * Set the maximum scale to use this legend. Use Integer.MAX_VALUE to
	 * specify no limit
	 *
	 * @param max
	 */
	void setMaxScale(int max);

	/**
	 * Get the minimum scale to use this legend. Integer.MIN_VALUE means there
	 * is no limit
	 */
	int getMinScale();

	/**
	 * Get the maximum scale to use this legend. Integer.MAX_VALUE means there
	 * is no limit
	 */
	int getMaxScale();

	/**
	 * Get the JAXB context used to marshall and unmarshall the JAXB objects
	 * returned by this legend
	 *
	 * @return
	 */
	String getJAXBContext();

	/**
	 * Get the symbols to refresh when a new row is added or an existing row is
	 * removed or modified. This can be just the affected symbol
	 * {@link #ONLY_AFFECTED} or all the symbols {@link #ALL}
	 *
	 * @return
	 */
	int getSymbolsToUpdateOnRowModification();


	/**
	 * Set the legend visibility.
	 * @param visible
	 */
	void setVisible(boolean visible);


	/**
	 * Set if the legend is visible or not.
	 * @return
	 */
	boolean isVisible();
}
