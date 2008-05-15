package org.orbisgis.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public interface Symbol {

	/**
	 * Draws the symbol in the specified graphics object
	 *
	 * @param g
	 * @param geom
	 * @param at
	 * @param permission
	 *            manager that can be asked for permission to draw
	 * @return The area used by this draw operation
	 * @throws DriverException
	 */
	Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException;

	/**
	 * Returns true if the symbol will draw the specified geometry
	 *
	 * @param geom
	 * @return
	 */
	boolean willDraw(Geometry geom);

	/**
	 * Sets the name of the symbol
	 *
	 * @param name
	 */
	void setName(String name);

	/**
	 * Gets the name of the symbol
	 *
	 * @return
	 */
	String getName();

	/**
	 * Adds the specified symbol at the end of the children list
	 *
	 * @param symbol
	 */
	void addSymbol(Symbol symbol);

	/**
	 * Adds the specified symbol at the specified index
	 *
	 * @param index
	 * @param symbol
	 */
	void addSymbol(int index, Symbol symbol);

	/**
	 * Removes the symbol at the specified index
	 *
	 * @param index
	 */
	void removeSymbol(int index);

	/**
	 * Removes the specified symbol
	 *
	 * @param symbol
	 * @return true if the symbol was in the children and was removed, false
	 *         otherwise
	 */
	boolean removeSymbol(Symbol symbol);

	/**
	 * True if the symbol accepts children, false otherwise
	 *
	 * @return
	 */
	boolean acceptsChildren();

}
