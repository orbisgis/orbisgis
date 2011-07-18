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
package org.gdms.driver.solene;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public final class Geometry3DUtilities {

	/**
	 * Obtention de la normale a un anneau par calcul. Il s'agit de recuperer
	 * trois points distincts de la structure. Le premier des trois est le
	 * premier dans la liste des sommets. Le second est choisi de maniere a
	 * maximiser la distance au premier. Le troisieme est celui qui maximise (en
	 * valeur absolue) l'aire du triangle forme par les trois points.
         *
         * @param polygon
         * @return 
         */
	public static Coordinate computeNormal(final Polygon polygon) {
		int i2 = 0;
		int i3 = 0;
		final Coordinate[] summits = polygon.getExteriorRing().getCoordinates();

		double distance = 0d;
		for (int i = 1; i < summits.length; i++) {
			final double tmp = summits[0].distance(summits[i]);
			if (tmp > distance) {
				distance = tmp;
				i2 = i;
			}
		}

		double aire = 0d;
		final Coordinate premierArc = sub(summits[0], summits[i2]);
		Coordinate secondArc = null;
		Coordinate normale = null;
		Coordinate resultNorm = null;
		for (int i = 1; i < summits.length; i++) {
			secondArc = sub(summits[0], summits[i]);
			normale = cross(premierArc, secondArc);
			final double tmp = length(normale);
			if (tmp > aire) {
				resultNorm = new Coordinate(normale);
				aire = tmp;
				i3 = i;
			}
		}
		return (i3 < i2) ? negate(normalize(resultNorm))
				: normalize(resultNorm);
	}

	private static Coordinate sub(final Coordinate u, final Coordinate v) {
		return new Coordinate(v.x - u.x, v.y - u.y, v.z - u.z);
	}

	private static Coordinate cross(final Coordinate u, final Coordinate v) {
		return new Coordinate(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x
				* v.y - u.y * v.x);
	}

	private static double length(final Coordinate u) {
		return Math.sqrt(scalarProduct(u, u));
	}

	private static Coordinate normalize(final Coordinate u) {
		final double tmp = length(u);
		return new Coordinate(u.x / tmp, u.y / tmp, u.z / tmp);
	}

	private static Coordinate negate(final Coordinate u) {
		return new Coordinate(-u.x, -u.y, -u.z);
	}

	public static double scalarProduct(Coordinate u, Coordinate v) {
		return u.x * v.x + u.y * v.y + u.z * v.z;
	}

	public static Polygon reverse(final Polygon polygon) {
		final int nbHoles = polygon.getNumInteriorRing();
		final LinearRing[] holes = new LinearRing[nbHoles];
		for (int i = 0; i < nbHoles; i++) {
			holes[i] = new GeometryFactory().createLinearRing(polygon
					.getInteriorRingN(i).getCoordinateSequence());
		}
		final LinearRing shell = new GeometryFactory()
				.createLinearRing(((LineString) polygon.getExteriorRing()
						.reverse()).getCoordinateSequence());

		return new GeometryFactory().createPolygon(shell, holes);
	}

        private Geometry3DUtilities() {
        }
}