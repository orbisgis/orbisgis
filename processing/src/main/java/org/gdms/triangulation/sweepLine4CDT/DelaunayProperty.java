package org.gdms.triangulation.sweepLine4CDT;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.index.SpatialIndex;

public class DelaunayProperty {
	private static Logger logger = Logger
			.getLogger(CDTTriangle.class.getName());

	private SpatialIndex verticesSpatialIndex;
	private Set<CDTTriangle> triangles;

	public DelaunayProperty(SpatialIndex verticesSpatialIndex,
			Set<CDTTriangle> triangles) {
		this.verticesSpatialIndex = verticesSpatialIndex;
		this.triangles = triangles;
	}

	@SuppressWarnings("unchecked")
	public boolean check(String msg) {
		for (CDTTriangle cdtTriangle : triangles) {
			List<CDTVertex> sublistOfVertices = verticesSpatialIndex
					.query(cdtTriangle.getEnvelope());

			for (CDTVertex c : sublistOfVertices) {
				if ((!cdtTriangle.isAVertex(c))
						&& (!cdtTriangle.respectDelaunayProperty(c.getCoordinate()))) {
					logger.info("====> " + msg + " point " + c.getCoordinate()
							+ " disturbs Delaunay property for triangle [ "
							+ cdtTriangle.getPolygon() + " ]");
//					return false;
				}
			}
		}
		return true;
	}
}