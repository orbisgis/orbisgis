package org.orbisgis.geoview.renderer;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
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
		multiLineString = Geometries.getMultilineString();

		polygon = Geometries.getPolygon3D();

		multiPolygon = Geometries.getMultiPolygon3D();

		lineString = Geometries.getLinestring();

		point = Geometries.getPoint();

		multiPoint = Geometries.getMultiPoint3D();
	}
}
