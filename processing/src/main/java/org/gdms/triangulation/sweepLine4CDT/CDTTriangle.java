package org.gdms.triangulation.sweepLine4CDT;

import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.SpatialIndex;

public class CDTTriangle {
	private static Logger logger = Logger
			.getLogger(CDTTriangle.class.getName());

	private final static GeometryFactory gf = new GeometryFactory();

	private Polygon pTriangle;
	private SpatialIndex verticesSpatialIndex;
	private CDTCircumCircle circumCircle;
	private CDTVertex p0;
	private CDTVertex p1;
	private CDTVertex p2;
	private SortedSet<LineSegment> listOfConstrainingEdges;

	public CDTTriangle(final CDTVertex p0, final CDTVertex p1,
			final CDTVertex p2, final PSLG pslg) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;

		pTriangle = gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				p0.getCoordinate(), p1.getCoordinate(), p2.getCoordinate(),
				p0.getCoordinate() }), null);
		if (null != pslg) {
			verticesSpatialIndex = pslg.getVerticesSpatialIndex();
		}
		circumCircle = new CDTCircumCircle(p0.getCoordinate(), p1
				.getCoordinate(), p2.getCoordinate());

		listOfConstrainingEdges = p0.getEdges();
		listOfConstrainingEdges.addAll(p1.getEdges());
		listOfConstrainingEdges.addAll(p2.getEdges());
	}

	public void legalization(final CDTVertex vertex) {

	}

	/**
	 * This method tests the classical "empty circumcircle rule". That is,
	 * current triangle is Delaunay if the unique circle on which lie its three
	 * vertices (ie the circumcircle) does not contain any other vertex.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean respectDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = verticesSpatialIndex
				.query(circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (!respectDelaunayProperty(v.getCoordinate())) {
				return false;
			}
		}
		return true;
	}

	public boolean respectDelaunayProperty(Coordinate v) {
		if (circumCircle.contains(v)) {
			logger.info("point " + v
					+ "disturb Delaunay property for triangle [ "
					+ p0.getCoordinate() + ", " + p1.getCoordinate() + ", "
					+ p2.getCoordinate() + " ]");
			return false;
		}
		return true;
	}

	/**
	 * This method is an implementation of the weaker Delaunay property
	 * described in the "Sweep-line algorithm for constrained Delaunay
	 * triangulation" article (V Domiter and B Zalik, p. 450). If a vertex
	 * violates Delaunay property for a triangle (ie if it lies within its
	 * circumcircle), it MUST be hidden by (behind) a constraining edge to
	 * respect a weaker Delaunay property. Thus this vertex is not visible from
	 * the triangle.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean respectWeakerDelaunayProperty() {
		final List<CDTVertex> sublistOfVertices = verticesSpatialIndex
				.query(circumCircle.getEnvelopeInternal());
		for (CDTVertex v : sublistOfVertices) {
			if (!respectWeakerDelaunayProperty(v.getCoordinate())) {
				return false;
			}
		}
		return true;
	}

	protected boolean respectWeakerDelaunayProperty(Coordinate v) {
		if (circumCircle.contains(v)) {
			if (pTriangle.contains(gf.createPoint(v))) {
				throw new RuntimeException("Unreachable code");
			}

			if (!newVertexIsHiddenByAConstrainingEdge(v)) {
				logger.info("point " + v
						+ "disturb _Weaker_ Delaunay property for triangle [ "
						+ p0.getCoordinate() + ", " + p1.getCoordinate() + ", "
						+ p2.getCoordinate() + " ]");
				return false;
			}
		}
		return true;
	}

	protected boolean newVertexIsHiddenByAConstrainingEdge(Coordinate v) {
		if (pointsAreLocatedOnEachSidesOfTheLineConstraint(p0.getCoordinate(),
				p1.getCoordinate(), p2.getCoordinate(), v)) {
			// what still remains is to test if [p0, p1] is a constraining edge
			LineSegment ls = new LineSegment(p0.getCoordinate(), p1
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		} else if (pointsAreLocatedOnEachSidesOfTheLineConstraint(p1
				.getCoordinate(), p2.getCoordinate(), p0.getCoordinate(), v)) {
			// what still remains is to test if [p1, p2] is a constraining edge
			LineSegment ls = new LineSegment(p1.getCoordinate(), p2
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		} else if (pointsAreLocatedOnEachSidesOfTheLineConstraint(p2
				.getCoordinate(), p0.getCoordinate(), p1.getCoordinate(), v)) {
			// what still remains is to test if [p2, p0] is a constraining edge
			LineSegment ls = new LineSegment(p2.getCoordinate(), p0
					.getCoordinate());
			ls.normalize();
			return listOfConstrainingEdges.contains(ls);
		}
		throw new RuntimeException("Unreachable code");
	}

	/**
	 * This method tests if points c2 and c are located on each [c0,c1] axis
	 * side or not.
	 * 
	 * @param c0
	 * @param c1
	 * @param c2
	 * @param c
	 */
	protected static boolean pointsAreLocatedOnEachSidesOfTheLineConstraint(
			Coordinate c0, Coordinate c1, Coordinate c2, Coordinate c) {
		// scalarProduct(normal(c0,c1),c2) * scalarProduct(normal(c0,c1),c)
		Coordinate normal = new Coordinate(-c1.y + c0.y, c1.x - c0.x);
		double tmp = (normal.x * (c2.x - c0.x) + normal.y * (c2.y - c0.y))
				* (normal.x * (c.x - c0.x) + normal.y * (c.y - c0.y));
		if (0 == tmp) {
			throw new RuntimeException("Unreachable code");
		}
		return (tmp < 0) ? true : false;
	}
}