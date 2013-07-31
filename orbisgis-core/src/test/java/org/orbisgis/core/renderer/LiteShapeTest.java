/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer;

import org.junit.Before;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import org.orbisgis.core.map.MapTransform;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LiteShapeTest {

	private MultiLineString multiLineString;
	private Polygon polygon;
	private Geometry multiPolygon;
	private LineString lineString;
	private Geometry multiPoint;
	private Geometry point;

    @Before
	public void setUp() throws Exception {
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

    @Test
	public void testLinearRingLiteShape() throws Exception {
		doTest(polygon.getExteriorRing());
	}

    @Test
	public void testPointLiteShape() throws Exception {
		doTest(point);
	}

    @Test
	public void testMultiPointLiteShape() throws Exception {
		doTest(multiPoint);
	}

    @Test
	public void testLineStringLiteShape() throws Exception {
		doTest(lineString);
	}

    @Test
	public void testMultiLineStringShape() throws Exception {
		doTest(multiLineString);
	}

    @Test
	public void testPolygonLiteShape() throws Exception {
		doTest(polygon);
	}

    @Test
	public void testMultiPolygonLiteShape() throws Exception {
		doTest(multiPolygon);
	}

    @Test
	public void testEmptyPolygonLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		Polygon polygon = gf.createPolygon(gf
				.createLinearRing(new Coordinate[0]), null);
		doTest(polygon);
	}

    @Test
	public void testEmptyMultiPolygonLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		Polygon emptyPolygon = gf.createPolygon(gf
				.createLinearRing(new Coordinate[0]), null);
		MultiPolygon mp = gf.createMultiPolygon(new Polygon[] { polygon,
				emptyPolygon });
		doTest(mp);
	}

    @Test
	public void testEmptyLineStringLiteShape() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		LineString g = gf.createLineString(new Coordinate[0]);
		doTest(g);
	}

	private void doTest(Geometry geometry) {
		MapTransform mt = new MapTransform();
		Shape ls = mt.getShape(geometry,false);
		PathIterator pi = ls.getPathIterator(null);

		iterate(pi);
        assertTrue(true);
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
}
