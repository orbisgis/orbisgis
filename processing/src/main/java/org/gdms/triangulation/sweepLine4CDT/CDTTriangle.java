package org.gdms.triangulation.sweepLine4CDT;

import java.util.List;

import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.index.SpatialIndex;

public class CDTTriangle {
	private Triangle triangle;
	private boolean[] containsConstraint = new boolean[] { false, false, false };

	private SpatialIndex verticesSpatialIndex;

	private CDTCircumCircle circumCircle;

	public CDTTriangle(final Triangle triangle, final PSLG pslg) {
		this.triangle = triangle;
		verticesSpatialIndex = pslg.getVerticesSpatialIndex();
		circumCircle = new CDTCircumCircle(triangle);

		verticesSpatialIndex.query(circumCircle.getEnvelopeInternal());
	}

	public void legalization(final CDTVertex vertex) {

	}

	public boolean respectDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = verticesSpatialIndex
				.query(circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (circumCircle.contains(v.getCoordinate())) {
				return false;
			}
		}
		return true;
	}

	public boolean respectWeakerDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = verticesSpatialIndex
				.query(circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (circumCircle.contains(v.getCoordinate())) {
				// TODO : add some more condition here before returning false...
				return false;
			}
		}
		return true;
	}
}