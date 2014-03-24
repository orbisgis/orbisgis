/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.map.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.geometryUtils.GeometryException;

public class PolygonHandler extends AbstractHandler implements Handler {

	private int holeIndex;

	public PolygonHandler(com.vividsolutions.jts.geom.Geometry geom,
			int holeIndex, int vertexIndex, Coordinate p, int geomIndex) {
		super(geom, vertexIndex, p, geomIndex);
		this.holeIndex = holeIndex;
	}

	public com.vividsolutions.jts.geom.Geometry removeVertex()
			throws GeometryException {
		com.vividsolutions.jts.geom.Geometry ret = null;
		Polygon p = (Polygon) geometry;
		if (holeIndex == -1) {
			LinearRing ring = removePolygonVertex(vertexIndex, p);
			LinearRing[] interiorRings = new LinearRing[p.getNumInteriorRing()];
			for (int i = 0; i < interiorRings.length; i++) {
				interiorRings[i] = gf.createLinearRing(p.getInteriorRingN(i)
						.getCoordinates());
			}
			ret = gf.createPolygon(gf.createLinearRing(ring.getCoordinates()),
					interiorRings);
		} else {
			LineString ls = p.getInteriorRingN(holeIndex);
			LinearRing ring = removePolygonVertex(vertexIndex, ls);
			LinearRing[] interiorRings = new LinearRing[p.getNumInteriorRing()];
			for (int i = 0; i < p.getNumInteriorRing(); i++) {
				if (i == holeIndex) {
					interiorRings[i] = gf.createLinearRing(ring
							.getCoordinates());
				} else {
					interiorRings[i] = gf.createLinearRing(p
							.getInteriorRingN(i).getCoordinates());
				}
			}
			ret = gf.createPolygon(gf.createLinearRing(p.getExteriorRing()
					.getCoordinates()), interiorRings);
		}

		if (!ret.isValid()) {
			throw new GeometryException(I18N.tr("The geometry is not valid"));
		}

		return ret;
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#remove()
	 */
        @Override
	public Geometry remove() throws GeometryException {
		com.vividsolutions.jts.geom.Geometry ret = removeVertex();
		if (!ret.isValid()) {
			throw new GeometryException(I18N.tr("The geometry is not valid"));
		}
		return ret;
	}

	public Polygon moveJTSTo(double x, double y)
			throws CannotChangeGeometryException {
		Polygon p = (Polygon) geometry.clone();
		Coordinate[] coords;
		if (holeIndex == -1) {
			coords = p.getCoordinates();
		} else {
			coords = p.getInteriorRingN(holeIndex).getCoordinates();
		}
		coords[vertexIndex].x = x;
		coords[vertexIndex].y = y;
		if (vertexIndex == 0) {
			coords[coords.length - 1].x = coords[0].x;
			coords[coords.length - 1].y = coords[0].y;
		}
		p.geometryChanged();

		return p;
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#moveTo(double,
	 *      double)
	 */
        @Override
	public Geometry moveTo(double x, double y)
			throws CannotChangeGeometryException {
		com.vividsolutions.jts.geom.Geometry g = moveJTSTo(x, y);
		if (!g.isValid()) {
			throw new CannotChangeGeometryException(I18N.tr("The geometry is not valid"));
		}
		return g;
	}

	private LinearRing removePolygonVertex(int vertexIndex,
			com.vividsolutions.jts.geom.Geometry p)
			throws GeometryException {
		Coordinate[] coords = GeometryEdit.removeVertex(vertexIndex, p, 4);
		if (vertexIndex == 0) {
			coords[coords.length - 1].x = coords[0].x;
			coords[coords.length - 1].y = coords[0].y;
		}

		LinearRing ret = gf.createLinearRing(coords);
		if (!ret.isValid()) {
			throw new GeometryException(I18N.tr("The geometry is not valid"));
		}

		return ret;
	}

}
