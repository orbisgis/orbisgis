package org.orbisgis.geoview.renderer;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import junit.framework.TestCase;

import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class LiteShapeTest extends TestCase {

	private MultiLineString multiLineString;
	private Polygon polygon;
	private Geometry multiPolygon;
	private LineString lineString;
	private Geometry multiPoint;
	private Geometry point;

	public void testLinearRingLiteShape() throws Exception {
		doTest(polygon.getExteriorRing());
	}

	public void testPointLiteShape() throws Exception {
		doTest(point);
	}

	public void testMultiPointLiteShape() throws Exception {
		doTest(multiPoint);
	}

	public void testLineStringLiteShape() throws Exception {
		doTest(lineString);
	}

	public void testMultiLineStringShape() throws Exception {
		doTest(multiLineString);
	}

	public void testPolygonLiteShape() throws Exception {
		doTest(polygon);
	}

	public void testMultiPolygonLiteShape() throws Exception {
		doTest(multiPolygon);
	}

	public void testEmptyPolygonLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		Polygon polygon = gf.createPolygon(gf
				.createLinearRing(new Coordinate[0]), null);
		doTest(polygon);
	}

	public void testEmptyMultiPolygonLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		Polygon emptyPolygon = gf.createPolygon(gf
				.createLinearRing(new Coordinate[0]), null);
		MultiPolygon mp = gf.createMultiPolygon(new Polygon[] { polygon,
				emptyPolygon });
		doTest(mp);
	}

	public void testEmptyLineStringLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		LineString g = gf.createLineString(new Coordinate[0]);
		doTest(g);
	}

	private void doTest(Geometry geometry) {
		LiteShape ls = new LiteShape(geometry, new AffineTransform(), true);
		PathIterator pi = ls.getPathIterator(null);

		iterate(pi);
	}

	private void iterate(PathIterator pi) {
		float coords[] = new float[6];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				break;
			case PathIterator.SEG_LINETO:
				break;
			case PathIterator.SEG_QUADTO:
				break;
			case PathIterator.SEG_CUBICTO:
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
			pi.next();
		}
	}

	@Override
	protected void setUp() throws Exception {
		GeometryFactory gf = new GeometryFactory();

		point = gf.createPoint(new Coordinate(1238, 3844));

		multiPoint = gf.createMultiPoint(new Coordinate[] {
				new Coordinate(239587, 23453), new Coordinate(239587, 23453),
				new Coordinate(239587, 23453), });

		lineString = gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(10, 0),
				new Coordinate(110, 0), new Coordinate(10, 240),
				new Coordinate(0, 0) });

		multiLineString = gf.createMultiLineString(new LineString[] { gf
				.createLineString(new Coordinate[] { new Coordinate(0, 0),
						new Coordinate(10, 0), new Coordinate(110, 0),
						new Coordinate(10, 240), new Coordinate(0, 0) }) });

		polygon = gf.createPolygon((LinearRing) lineString, null);

		multiPolygon = gf.createMultiPolygon(new Polygon[] { polygon });

	}
}
