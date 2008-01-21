package org.orbisgis.geoview.rasterProcessing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

class PolyWriter {
	private PrintWriter out;
	private SpatialDataSourceDecorator sds;

	private class Edge {
		long startVertexIdx;
		long endVertexIdx;

		Edge(long startVertexIdx, long endVertexIdx) {
			this.startVertexIdx = startVertexIdx;
			this.endVertexIdx = endVertexIdx;
		}
	}

	PolyWriter(final File file, final DataSource dataSource)
			throws DriverException {
		sds = new SpatialDataSourceDecorator(dataSource);
		try {
			out = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	void write() throws DriverException {
		final Map<Integer, Coordinate> mapOfVertices = new HashMap<Integer, Coordinate>();
		final Map<Integer, Edge> mapOfEdges = new HashMap<Integer, Edge>();

		preProcess(mapOfVertices, mapOfEdges);

		// write node header part...
		out.printf("%d 2 1 0\n", mapOfVertices.size());

		// write node body part...
		for (long pointIdx = 1; pointIdx <= mapOfVertices.size(); pointIdx++) {

		}
		
		long pointIdx = 1;
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);
			for (Coordinate c : g.getCoordinates()) {
				out.printf("%d %g %g %d\n", pointIdx, c.x, c.y, rowIndex);
				pointIdx++;
			}
		}
		// write edge header part...
		out.printf("%d 0\n", mapOfEdges.size());

		// write edge body part...

		out.flush();
		out.close();
	}

	private void preProcess(final Map<Integer, Coordinate> mapOfVertices,
			final Map<Integer, Edge> mapOfEdges) throws DriverException {
		int verticeIdx = 1;
		for (long rowIndex = 0; rowIndex < sds.getRowCount(); rowIndex++) {
			final Geometry g = sds.getGeometry(rowIndex);

			if (g instanceof Point) {
				mapOfVertices.put(verticeIdx, ((Point) g).getCoordinate());
				verticeIdx++;
			} else if (g instanceof MultiPoint) {

			}
		}

	}

	void close() {
		out.close();
	}
}