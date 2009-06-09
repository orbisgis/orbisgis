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
package org.orbisgis.core.renderer.legend.carto;

import java.awt.Graphics;

import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.symbol.StandardPointSymbol;

public interface ProportionalLegend extends Legend {

	int LINEAR = 1;
	int LOGARITHMIC = 2;
	int SQUARE = 3;

	int getMaxSize();

	/**
	 * Sets the maximum size for the symbols in the proportional legend
	 *
	 * @param minArea
	 */
	void setMaxSize(int maxSize);

	/**
	 * Get the symbol used to create the proportional instances
	 *
	 * @return
	 */
	StandardPointSymbol getSampleSymbol();

	/**
	 * Set the symbol that will be used to create the proportional instances
	 *
	 * @param symbol
	 */
	void setSampleSymbol(StandardPointSymbol symbol);

	/**
	 * Set the method to calculate the proportional size. It can be either
	 * LINEAR, LOGARITHMIC or SQUARE
	 *
	 * @param method
	 * @throws DriverException
	 */
	void setMethod(int method) throws DriverException;

	/**
	 * Get the method used to calculate the proportional size. It can be either
	 * LINEAR, LOGARITHMIC or SQUARE
	 *
	 * @return
	 */
	int getMethod();

	/**
	 * Sets the field used to compute the size of the proportional symbol
	 *
	 * @param fieldName
	 */
	void setClassificationField(String fieldName);

	/**
	 * Get the field used to compute the size of the proportional symbol
	 *
	 * @return
	 */
	String getClassificationField();

	/**
	 * draws the preview with the big point having the specified size
	 *
	 * @param g
	 * @param bigSize
	 */
	void drawImage(Graphics g, int bigSize);

	/**
	 * Gets the size of an image with the specified size for the bigest point
	 *
	 * @param g
	 * @param bigSize
	 * @return
	 */
	int[] getImageSize(Graphics g, int bigSize);

}
