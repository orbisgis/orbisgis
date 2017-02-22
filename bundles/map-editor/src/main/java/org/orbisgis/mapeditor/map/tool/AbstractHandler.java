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
import java.awt.Point;
import java.awt.geom.Point2D;

import org.orbisgis.coremap.map.MapTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 */
public abstract class AbstractHandler implements Handler {
        protected static final I18n I18N = I18nFactory.getI18n(AbstractHandler.class);
	protected int vertexIndex;
	protected Coordinate point;
	protected Geometry geometry;
    protected GeometryFactory gf;
	protected long geomIndex;

	/**
	 * Creates a new PointHandler
	 * 
	 * @param p
	 *            Primitive this handler belongs to
	 * @param vertexIndex
	 *            index of the vertex this handler represents
	 * @param p
     * @param geomIndex
	 */
	public AbstractHandler(Geometry g,
			int vertexIndex, Coordinate p, long geomIndex) {
        this.gf = g.getFactory();
		this.vertexIndex = vertexIndex;
		this.point = p;
		this.geometry = g;
		this.geomIndex = geomIndex;
	}

	public void draw(Graphics2D g2, Color color, ToolManager tm,
			MapTransform transform) {
		g2.setColor(color);
		Point p = transform.fromMapPoint(getPoint());
		int tol = tm.getUITolerance();
		g2.drawRect(p.x - tol / 2, p.y - tol / 2, tol, tol);
	}

	public Point2D getPoint() {
		return new Point2D.Double(point.x, point.y);
	}

	/**
	 * removes the vertex from the JTS geometry
	 *
	 * @param g
	 *
	 * @return
	 *
	 * @throws CannotChangeGeometryException
	 */
	protected Coordinate[] removeVertex(int vertexIndex,
			Geometry g, int minNumVertex)
			throws CannotChangeGeometryException {
		Coordinate[] coords = g.getCoordinates();
		if (coords.length <= minNumVertex) {
			throw new CannotChangeGeometryException(
					I18N.tr("Invalid geometry. Too few vertex")); //$NON-NLS-1$
		}
		Coordinate[] newCoords = new Coordinate[coords.length - 1];
		for (int i = 0; i < vertexIndex; i++) {
			newCoords[i] = new Coordinate(coords[i].x, coords[i].y);
		}
		if (vertexIndex != coords.length - 1) {
			for (int i = vertexIndex + 1; i < coords.length; i++) {
				newCoords[i - 1] = new Coordinate(coords[i].x, coords[i].y);
			}
		}
	
		return newCoords;
	}

	public long getGeometryPK() {
		return geomIndex;
	}

}
