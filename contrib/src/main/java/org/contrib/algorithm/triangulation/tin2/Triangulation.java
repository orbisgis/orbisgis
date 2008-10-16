package org.contrib.algorithm.triangulation.tin2;

import java.util.Map;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;

public class Triangulation {
	private SetOfVertices vertices;
	private SetOfTriangles triangles;

	public Triangulation(final SpatialDataSourceDecorator inSds,
			final String spatialFieldName) throws DriverException {
		this(inSds, spatialFieldName, null);
	}

	public Triangulation(final SpatialDataSourceDecorator inSds,
			final String spatialFieldName, final String gidFieldName)
			throws DriverException {
		final long rowCount = inSds.getRowCount();

		inSds.setDefaultGeometry(spatialFieldName);

		// 1st step: add all the (constraining) vertices
		if (null == gidFieldName) {
			vertices = new SetOfVertices(inSds.getFullExtent());
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Geometry geometry = inSds.getGeometry(rowIndex);
				vertices.addAll(geometry.getCoordinates());
			}
		} else {
			int gidFieldIndex = inSds.getFieldIndexByName(gidFieldName);

			vertices = new SetOfVertices(inSds.getFullExtent());
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Geometry geometry = inSds.getGeometry(rowIndex);
				final int gid = inSds.getFieldValue(rowIndex, gidFieldIndex)
						.getAsInt();
				vertices.addAll(geometry.getCoordinates(), gid);
			}
		}
	}

	public void mesh(IProgressMonitor pm) {
		// build the 1st triangle
		triangles = new SetOfTriangles(vertices);
		triangles.add(new Triangle(vertices, 0, 1, 2));

		// then iterate over each already sorted set of vertices
		for (int i = 3; i < vertices.size(); i++) {

			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / vertices.size()));
				}
			}

			triangles.mesh(i);
		}
	}

	public Map<Integer, Triangle> getTriangles() {
		return triangles.getTriangles();
	}
}