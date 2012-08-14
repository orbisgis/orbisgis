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
import java.awt.Point;
import java.awt.geom.Point2D;

import org.orbisgis.core.map.MapTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public abstract class AbstractHandler implements Handler {

	protected static GeometryFactory gf = new GeometryFactory();
	protected int vertexIndex;
	protected Coordinate point;
	protected com.vividsolutions.jts.geom.Geometry geometry;
	protected int geomIndex;

	/**
	 * Creates a new PointHandler
	 * 
	 * @param p
	 *            Primitive this handler belongs to
	 * @param vertexIndex
	 *            index of the vertex this handler represents
	 * @param primitiveIndex
	 *            Index of the primitive in the Theme it was read
	 * @param x
	 * @param y
	 */
	public AbstractHandler(com.vividsolutions.jts.geom.Geometry g,
			int vertexIndex, Coordinate p, int geomIndex) {
		this.vertexIndex = vertexIndex;
		this.point = p;
		this.geometry = g;
		this.geomIndex = geomIndex;
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#draw(java.awt.Graphics2D)
	 */
	public void draw(Graphics2D g2, Color color, ToolManager tm,
			MapTransform transform) {
		g2.setColor(color);
		Point p = transform.fromMapPoint(getPoint());
		int tol = tm.getUITolerance();
		g2.drawRect(p.x - tol / 2, p.y - tol / 2, tol, tol);
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#getPoint()
	 */
	public Point2D getPoint() {
		return new Point2D.Double(point.x, point.y);
	}

	
	public int getGeometryIndex() {
		return geomIndex;
	}

}
