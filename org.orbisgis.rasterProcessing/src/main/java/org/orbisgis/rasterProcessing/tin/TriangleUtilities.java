package org.orbisgis.rasterProcessing.tin;

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