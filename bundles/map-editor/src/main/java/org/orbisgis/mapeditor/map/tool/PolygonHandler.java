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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import org.orbisgis.mapeditor.map.geometryUtils.GeometryEdit;

public class PolygonHandler extends AbstractHandler implements Handler {

	private int holeIndex;

	public PolygonHandler(Geometry geom,
			int holeIndex, int vertexIndex, Coordinate p, long geomPk) {
		super(geom, vertexIndex, p, geomPk);
		this.holeIndex = holeIndex;
	}

	public Geometry removeVertex()
			throws TopologyException {
		Geometry ret;
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
			throw new TopologyException(I18N.tr("The geometry is not valid"));
		}

		return ret;
	}

    @Override
	public Geometry remove() throws TopologyException {
		com.vividsolutions.jts.geom.Geometry ret = removeVertex();
		if (!ret.isValid()) {
			throw new TopologyException(I18N.tr("The geometry is not valid"));
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

    @Override
	public Geometry moveTo(double x, double y)
			throws CannotChangeGeometryException {
		Geometry g = moveJTSTo(x, y);
		if (!g.isValid()) {
			throw new CannotChangeGeometryException(I18N.tr("The geometry is not valid"));
		}
		return g;
	}

	private LinearRing removePolygonVertex(int vertexIndex,
			Geometry p)
			throws TopologyException {
		Coordinate[] coords = GeometryEdit.removeVertex(vertexIndex, p, 4);
		if (vertexIndex == 0) {
			coords[coords.length - 1].x = coords[0].x;
			coords[coords.length - 1].y = coords[0].y;
		}

		LinearRing ret = gf.createLinearRing(coords);
		if (!ret.isValid()) {
			throw new TopologyException(I18N.tr("The geometry is not valid"));
		}

		return ret;
	}

}
