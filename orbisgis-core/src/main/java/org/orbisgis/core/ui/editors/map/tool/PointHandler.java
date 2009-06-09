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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

public class PointHandler extends AbstractHandler implements Handler {

	private String geometryType;

	public PointHandler(Geometry g, String geometryType, int vertexIndex,
			Coordinate p, int geomIndex) {
		super(g, vertexIndex, p, geomIndex);
		this.geometryType = geometryType;
	}

	public com.vividsolutions.jts.geom.Geometry moveJTSTo(final double x,
			final double y) throws CannotChangeGeometryException {
		Geometry ret = (Geometry) geometry.clone();
		ret.apply(new CoordinateSequenceFilter() {

			private boolean done = false;

			public boolean isGeometryChanged() {
				return true;
			}

			public boolean isDone() {
				return done;
			}

			public void filter(CoordinateSequence seq, int i) {
				if (i == vertexIndex) {
					seq.setOrdinate(i, 0, x);
					seq.setOrdinate(i, 1, y);
					done = true;
				}
			}

		});

		return ret;
	}

	public Geometry moveTo(double x, double y)
			throws CannotChangeGeometryException {
		com.vividsolutions.jts.geom.Geometry ret = moveJTSTo(x, y);
		if (!ret.isValid()) {
			throw new CannotChangeGeometryException(THE_GEOMETRY_IS_NOT_VALID);
		}
		return ret;
	}

	public com.vividsolutions.jts.geom.Geometry removeVertex()
			throws CannotChangeGeometryException {
		if (geometryType == Primitive.MULTIPOINT_GEOMETRY_TYPE) {
			return gf.createMultiPoint(removeVertex(vertexIndex, geometry, 1));
		} else if (geometryType == Primitive.LINE_GEOMETRY_TYPE) {
			return gf.createLineString(removeVertex(vertexIndex, geometry, 2));
		}

		throw new RuntimeException();
	}

	/**
	 * @see org.estouro.theme.Handler#remove()
	 */
	public Geometry remove() throws CannotChangeGeometryException {
		if (geometryType == Primitive.POINT_GEOMETRY_TYPE) {
			throw new CannotChangeGeometryException(
					"Cannot remove a vertex from a point geometry");
		} else if ((geometryType == Primitive.LINE_GEOMETRY_TYPE)
				|| (geometryType == Primitive.MULTIPOINT_GEOMETRY_TYPE)) {
			com.vividsolutions.jts.geom.Geometry g = removeVertex();
			if (!g.isValid()) {
				throw new CannotChangeGeometryException(
						THE_GEOMETRY_IS_NOT_VALID);
			}
			return g;
		}
		throw new RuntimeException();
	}

}
