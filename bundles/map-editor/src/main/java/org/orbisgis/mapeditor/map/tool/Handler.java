/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.mapeditor.map.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.vividsolutions.jts.geom.TopologyException;
import org.orbisgis.coremap.map.MapTransform;


import com.vividsolutions.jts.geom.Geometry;

/**
 * Implementations of this interface represents the handlers of the geometries
 * that operate on the geometry they belong when moved by the user
 * 
 * @author Fernando Gonzlez Corts
 */
public interface Handler {

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
	public Geometry remove() throws TopologyException;

	/**
	 * Returns the geometry id this handler belongs to
	 * 
	 * @return
	 */
	public long getGeometryPK();
}
