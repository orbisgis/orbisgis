package org.gdms.driver.triangle;

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
public class NodeWriter {
	private PrintWriter out;
	private SpatialDataSourceDecorator sds;

	public NodeWriter(final File file, final DataSource dataSource)
			throws DriverException {
		sds = new SpatialDataSourceDecorator(dataSource);
		try {
			out = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	public void write() throws DriverException {
		// write header part...
		long numberOfPoints = 0;
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);
			numberOfPoints += g.getCoordinates().length;
		}
		out.printf("%d 2 1 0\n", numberOfPoints);

		// write body part...
		long pointIdx = 1;
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

	public void close() {
		out.close();
	}
}