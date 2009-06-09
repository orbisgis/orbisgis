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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.core.ui.editors.map.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import org.orbisgis.core.map.MapTransform;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Implementations of this interface represents the handlers of the geometries
 * that operate on the geometry they belong when moved by the user
 *
 * @author Fernando Gonzlez Corts
 */
public interface Handler {
	public static final String THE_GEOMETRY_IS_NOT_VALID = "The geometry is not valid";

	/**
	 * Gets the real world coordinates of the handler
	 *
	 * @return
	 */
	public Point2D getPoint();

	/**
	 * Moves the handler to the real world coordinates passed as arguments and
	 * returns a new Geometry reflecting the change
	 *
	 * @param x
	 * @param y
	 *
	 * @return Geometry
	 */
	public Geometry moveTo(double x, double y)
			throws CannotChangeGeometryException;

	/**
	 * Draws the handler on the Graphics2d argument
	 *
	 * @param g2
	 * @param color
	 * @param ec
	 */
	public void draw(Graphics2D g2, Color color, ToolManager tm,
			MapTransform transform);

	/**
	 * Removes the vertex handled by this handler and returns a new Geometry
	 * reflecting the changes
	 *
	 * @return
	 * @throws CannotChangeGeometryException
	 *             If the vertex cannot be removed due to geometrical
	 *             constraints (i.e. a line must have at least two points, ...)
	 */
	public Geometry remove() throws CannotChangeGeometryException;

	/**
	 * Returns the geometry id this handler belongs to
	 *
	 * @return
	 */
	public int getGeometryIndex();
}
