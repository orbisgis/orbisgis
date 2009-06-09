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
