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
package org.contrib.algorithm.triangulation.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.PrecisionModel;

public final class JtsGeometryUtil {

	private JtsGeometryUtil() {
	}

	/**
	 * Add a evelation (z) value for a coordinate that is on a line segment.
	 * 
	 * @param coordinate
	 *            The Coordinate.
	 * @param line
	 *            The line segment the coordinate is on.
	 */
	public static void addElevation(final Coordinate coordinate,
			final LineSegment line) {
		double z = getElevation(line, coordinate);
		coordinate.z = z;
	}

	public static double getElevation(final LineSegment line,
			final Coordinate coordinate) {
		Coordinate c0 = line.p0;
		Coordinate c1 = line.p1;
		double fraction = coordinate.distance(c0) / line.getLength();
		double z = c0.z + (c1.z - c0.z) * (fraction);
		return z;
	}

	public static void addElevation(PrecisionModel precisionModel,
			Coordinate coordinate, LineSegment3D line) {
		addElevation(coordinate, line);
		coordinate.z = precisionModel.makePrecise(coordinate.z);

	}

	public static LineSegment addLength(final LineSegment line,
			final double startDistance, final double endDistance) {
		double angle = line.angle();
		Coordinate c1 = offset(line.p0, angle, -startDistance);
		Coordinate c2 = offset(line.p1, angle, endDistance);
		return new LineSegment(c1, c2);

	}

	public static Coordinate offset(final Coordinate coordinate,
			final double angle, final double distance) {
		double newX = coordinate.x + distance * Math.cos(angle);
		double newY = coordinate.y + distance * Math.sin(angle);
		Coordinate newCoordinate = new Coordinate(newX, newY);
		return newCoordinate;

	}

}
