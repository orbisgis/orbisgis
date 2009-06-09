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
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * JUMP is Copyright (C) 2003 Vivid Solutions
 *
 * This program implements extensions to JUMP and is
 * Copyright (C) 2004 Integrated Systems Analysts, Inc.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Integrated Systems Analysts, Inc.
 * 630C Anchors St., Suite 101
 * Fort Walton Beach, Florida
 * USA
 *
 * (850)862-7321
 */

package org.geoalgorithm.jts.operation;

import org.geoalgorithm.jts.util.GeoUtils;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.DefaultCoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class ISAGeneralization {

	private Geometry geometry;
	private double tolerance;

	public ISAGeneralization(Geometry geometry, double tolerance) {
		this.geometry = geometry;
		this.tolerance = tolerance;

	}

	public Geometry reducePoints() {
		if (geometry instanceof GeometryCollection) {
			GeometryFactory geoFac = geometry.getFactory();
			GeometryCollection gc = (GeometryCollection) geometry;
			Geometry[] geos = new Geometry[gc.getNumGeometries()];
			if (!gc.isEmpty()) {
				for (int i = 0; i < gc.getNumGeometries(); i++) {
					geos[i] = reduceGeo(gc.getGeometryN(i), tolerance);
				}
				return new GeometryCollection(geos, geoFac);
			} else {
				return geometry;
			}
		} else {
			return reduceGeo(geometry, tolerance);
		}
	}

	private Geometry reduceGeo(Geometry geometry, double tolerance) {
		if (geometry instanceof LineString) // open poly
		{
			return GeoUtils.reducePoints(geometry, tolerance);
		} else if (geometry instanceof LinearRing) // closed poly (no holes)
		{
			return GeoUtils.reducePoints(geometry, tolerance);
		} else if (geometry instanceof Polygon) // poly with 0 or more holes
		{
			return GeoUtils.reducePoints(geometry, tolerance);
		} else if (geometry instanceof MultiLineString) {
			MultiLineString mls = (MultiLineString) geometry;
			LineString[] lineStrings = new LineString[mls.getNumGeometries()];
			GeometryFactory geoFac = geometry.getFactory();

			if (!mls.isEmpty()) {
				for (int i = 0; i < mls.getNumGeometries(); i++) {
					lineStrings[i] = (LineString) GeoUtils.reducePoints(mls
							.getGeometryN(i), tolerance);
				}
				return new MultiLineString(lineStrings, geoFac);
			} else {
				return geometry;
			}
		} else if (geometry instanceof MultiPolygon) {
			MultiPolygon mp = (MultiPolygon) geometry;
			Polygon[] polys = new Polygon[mp.getNumGeometries()];
			GeometryFactory geoFac = geometry.getFactory();
			DefaultCoordinateSequenceFactory dcsf = DefaultCoordinateSequenceFactory
					.instance();

			if (!mp.isEmpty()) {
				for (int i = 0; i < mp.getNumGeometries(); i++) {
					Polygon poly = (Polygon) GeoUtils.reducePoints(mp
							.getGeometryN(i), tolerance);
					CoordinateSequence cs = dcsf.create(poly.getCoordinates());
					polys[i] = new Polygon(new LinearRing(cs, geoFac), null,
							geoFac);
				}
				return new MultiPolygon(polys, geoFac);
			} else {
				return geometry;
			}
		} else {
			return geometry;
		}
	}
}