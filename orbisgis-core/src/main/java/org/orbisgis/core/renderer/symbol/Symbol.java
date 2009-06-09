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
package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Map;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.RenderPermission;

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
	 * @return The area used by this draw operation. It will be taken into
	 *         account by the RenderPermission
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
	boolean acceptGeometry(Geometry geom);

	/**
	 * Returns true if this symbol is suitable for a geometry field with the
	 * specified geometry type constraint.
	 *
	 * @param geometryConstraint
	 *            The geometry constraint. Null if there is no geometry type
	 *            constraint, this is, the field can have any type of geometries
	 * @return
	 */
	boolean acceptGeometryType(GeometryConstraint geometryConstraint);

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
	 * True if the symbol accepts children, false otherwise
	 *
	 * @return
	 */
	boolean acceptsChildren();

	/**
	 * Gets the number of child in this symbol
	 *
	 * @return
	 */
	public int getSymbolCount();

	/**
	 * Gets the i-th child
	 *
	 * @param i
	 *
	 * @return
	 */
	public Symbol getSymbol(int i);

	/**
	 * Returns an unique id. It can be whatever unique string. If this string
	 * changes, previous versions of the symbol collection could not be read.
	 * For persistence purposes.
	 *
	 * @return
	 */
	String getId();

	/**
	 * Gets the human readable description of this type of symbol
	 *
	 * @return
	 */
	String getClassName();

	/**
	 * Creates a new instance of this symbol
	 *
	 * @return
	 */
	Symbol cloneSymbol();

	/**
	 * Gets the properties of this symbol to save it to disk
	 *
	 * @return
	 */
	Map<String, String> getPersistentProperties();

	/**
	 * Sets the persistent properties returned previously by
	 * getPersistentProperties
	 *
	 * @param props
	 */
	void setPersistentProperties(Map<String, String> props);

	/**
	 * Creates a new symbol of this type but containing the specified color
	 *
	 * @param color
	 * @return The derived symbol or null if this symbol cannot be derived
	 */
	Symbol deriveSymbol(Color color);

}
