package org.orbisgis.processing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This driver is just able to deal with 2D points (and not 3D ones) !
 */
class NodeWriter {
	private PrintWriter out;
	private SpatialDataSourceDecorator sds;

	NodeWriter(final File file, final DataSource dataSource)
			throws DriverException {
		sds = new SpatialDataSourceDecorator(dataSource);
		try {
			out = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	void write() throws DriverException {
		// write header part...
		out.printf("%d 2 1 0\n", TriangleUtilities.getNumberOfPoints(sds));

		// write body part...
		long pointIdx = 0;
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);
			for (Coordinate c : g.getCoordinates()) {
				out.printf("%d %g %g %d\n", pointIdx, c.x, c.y, rowIndex);
				pointIdx++;
			}
		}
		out.flush();
		out.close();
	}

	void close() {
		out.close();
	}
}