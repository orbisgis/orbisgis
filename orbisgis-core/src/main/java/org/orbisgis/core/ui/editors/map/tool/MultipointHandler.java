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

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class MultipointHandler extends AbstractHandler implements Handler {

	private int pointIndex;

	public MultipointHandler(Geometry g, int pointIndex, int vertexIndex,
			Coordinate p, int geomIndex) {
		super(g, vertexIndex, p, geomIndex);
		this.pointIndex = pointIndex;
	}

	/**
	 * @see org.estouro.theme.Handler#moveTo(double, double)
	 */
	public Geometry moveTo(double x, double y)
			throws CannotChangeGeometryException {
		Coordinate p = new Coordinate(x, y);
		MultiPoint mp = (MultiPoint) geometry.clone();
		Point[] points = new Point[mp.getNumGeometries()];
		for (int i = 0; i < points.length; i++) {
			if (i == pointIndex) {
				PointHandler handler = new PointHandler((Point) mp
						.getGeometryN(i), Primitive.POINT_GEOMETRY_TYPE, 0, p,
						geomIndex);
				points[i] = (Point) handler.moveJTSTo(x, y);
			} else {
				points[i] = (Point) mp.getGeometryN(i);
			}

		}

		mp = gf.createMultiPoint(points);
		if (!mp.isValid()) {
			throw new CannotChangeGeometryException(THE_GEOMETRY_IS_NOT_VALID);
		}

		return mp;
	}

	/**
	 * @see org.estouro.theme.Handler#remove()
	 */
	public Geometry remove() throws CannotChangeGeometryException {

		MultiPoint mp = (MultiPoint) geometry;
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < mp.getNumGeometries(); i++) {
			if (i != pointIndex) {
				points.add((Point) mp.getGeometryN(i));
			}
		}

		mp = gf.createMultiPoint(points.toArray(new Point[0]));
		if (!mp.isValid()) {
			throw new CannotChangeGeometryException(THE_GEOMETRY_IS_NOT_VALID);
		}

		return mp;
	}

}
