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
package org.orbisgis.processing.tin;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

class TriangleUtilities {
	private final static double EPSILON = 1.0E-8;

	static boolean isOnlyComposedOfPointsOrMultiPoints(
			final SpatialDataSourceDecorator sds) throws DriverException {
		boolean isOfNodeFileType = true;
		// is it a .node (only composed of [Multi]Point) or a .poly file ?
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);
			if (!((g instanceof Point) || (g instanceof MultiPoint))) {
				isOfNodeFileType = false;
			}
		}
		return isOfNodeFileType;
	}

	static long getNumberOfPoints(final SpatialDataSourceDecorator sds)
			throws DriverException {
		long numberOfPoints = 0;
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);
			numberOfPoints += g.getCoordinates().length;
		}
		return numberOfPoints;
	}

	static int floatingPointCompare(final double a, final double b) {
		final double delta = Math.abs(a - b);
		final double mean = (Math.abs(a) + Math.abs(b)) / 2;
		if (delta < EPSILON * mean) {
			return 0;
		} else if ((a - b) < 0) {
			return -1;
		} else {
			return 1;
		}
	}
}