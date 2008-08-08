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
package org.gdms.triangulation.sweepLine4CDT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Triangle;

public class CDTCircumCircle {
	private static final double EPSILON = 1E-4;
	private static final GeometryFactory gf = new GeometryFactory();

	private Coordinate centre;
	private double radius;
	private Envelope envelope;

	public CDTCircumCircle(final Coordinate p0, final Coordinate p1,
			final Coordinate p2) {
		centre = Triangle.circumcentre(p0, p1, p2);
		radius = Math.sqrt((centre.x - p0.x) * (centre.x - p0.x)
				+ (centre.y - p0.y) * (centre.y - p0.y));
		envelope = new Envelope(centre.x - radius, centre.x + radius, centre.y
				- radius, centre.y + radius);
	}

	public boolean contains(final Coordinate coordinate) {
		return centre.distance(coordinate) < radius + EPSILON;
	}

	public Envelope getEnvelopeInternal() {
		return envelope;
	}

	public Coordinate getCentre() {
		return centre;
	}

	public double getRadius() {
		return radius;
	}

	public Geometry getGeometry() {
		return gf.createPoint(centre).buffer(radius);
	}
}