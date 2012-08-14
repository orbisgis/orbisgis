/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.map.export;

import java.awt.Graphics2D;

public interface Scale {

	/**
	 * Draws the scale based in the specified MapTransform
	 * 
	 * @param g
	 *            Graphics object to draw to
	 * @param dpi
	 *            Resolution of the output image
	 */
	void drawScale(Graphics2D g, int dpi);

	/**
	 * Get the name of the scale type
	 * 
	 * @return
	 */
	String getScaleTypeName();

	/**
	 * Get the number of parts in this scale
	 * 
	 * @return
	 */
	public int getPartCount();

	/**
	 * Set the number of parts in this scale
	 * 
	 * @param partCount
	 * @throws IllegalArgumentException
	 *             If this scale doesn't accept the specified number of parts
	 */
	public void setPartCount(int partCount) throws IllegalArgumentException;

	/**
	 * Get the part width in centimeters
	 * 
	 * @return
	 */
	public double getPartWidth();

	/**
	 * Set the part width in centimeters
	 * 
	 * @param partWidth
	 */
	public void setPartWidth(double partWidth);

	/**
	 * Get the height of the scale
	 * 
	 * @return
	 */
	public double getHeight();

	/**
	 * Set the height of the scale
	 * 
	 * @param height
	 */
	public void setHeight(double height);

	/**
	 * Gets an array of {@link #getPartCount()} elements with true in the parts
	 * that appear with text
	 * 
	 * @return
	 */
	public boolean[] getLabeledParts();

	/**
	 * Set an array of {@link #getPartCount()} elements with true in the parts
	 * that appear with text
	 * 
	 * @throws IllegalArgumentException
	 *             if the array doesn't contain the right number of elements
	 */
	public void setPartsWithText(boolean[] partsWithText)
			throws IllegalArgumentException;

	/**
	 * Gets an array of {@link #getPartCount()} elements with true in the parts
	 * that appear remarked somehow
	 * 
	 * @return
	 */
	public boolean[] getRemarkedParts();

	/**
	 * Set an array of {@link #getPartCount()} elements with true in the parts
	 * that appear remarked somehow
	 * 
	 * @throws IllegalArgumentException
	 *             if the array doesn't contain the right number of elements
	 */
	public void setRemarkedParts(boolean[] remarkedParts);

	/**
	 * Get the scale denominator. For example 1000 in a 1:1000 scale
	 * 
	 * @return
	 */
	public double getScaleDenominator();

	/**
	 * Get the scale denominator. For example 1000 in a 1:1000 scale
	 * 
	 * @param scaleDenominator
	 */
	public void setScaleDenominator(double scaleDenominator);
}
