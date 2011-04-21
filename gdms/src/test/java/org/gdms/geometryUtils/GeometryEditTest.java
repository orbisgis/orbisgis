/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *
 * Copyright (C) 2007-2008 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author ebocher
 */
public class GeometryEditTest extends TestCase {

        public WKTReader wKTReader = new WKTReader();

        /**
         * Test to split a linestring according a point
         * @throws Exception
         */
        public void testSplitLineString() throws Exception {
                LineString line = (LineString) wKTReader.read("LINESTRING(0 8, 1 8 , 3 8,  8  8, 10 8, 20 8, 25 8, 30 8, 50 8, 100 8)");
                Point point = (Point) wKTReader.read("POINT(1.5 4 )");
                LineString[] results = GeometryEdit.splitLineString(line, point, 4);
                assertTrue(results[0].equals(wKTReader.read("LINESTRING(0 8, 1 8 , 1.5 8)")));
                assertTrue(results[1].equals(wKTReader.read("LINESTRING(1.5 8 , 3 8,  8  8, 10 8, 20 8, 25 8, 30 8, 50 8, 100 8)")));

        }

        /**
         * Find the closet point to a linestring based on distance
         * @throws Exception
         */
        public void testSnapedPoint() throws Exception {
                LineString line = (LineString) wKTReader.read("LINESTRING(0 8, 1 8 , 3 8,  8  8, 10 8, 20 8, 25 8, 30 8, 50 8, 100 8)");
                Point point = (Point) wKTReader.read("POINT(1.5 4 )");
                //Test a point in a segment
                GeometryLocation geomLocation = GeometryEdit.getVertexToSnap(line, point, 4);
                assertTrue(geomLocation.getSegmentIndex() == 1);
                assertTrue(geomLocation.getCoordinate().equals2D(new Coordinate(1.5, 8)));
                //Test a point on an existing coordinate
                point = (Point) wKTReader.read("POINT(1 4 )");
                geomLocation = GeometryEdit.getVertexToSnap(line, point, 4);
                assertTrue(geomLocation.getSegmentIndex() == 0);
                assertTrue(geomLocation.getCoordinate().equals2D(new Coordinate(1, 8)));
                //Test a point on an existing coordinate
                point = (Point) wKTReader.read("POINT(1 4 )");
                geomLocation = GeometryEdit.getVertexToSnap(line, point, 1);
                assertTrue(geomLocation == null);
        }

        /**
         * Insert a vertex into a lineString
         * @throws Exception
         */
        public void testInsertVertexInLineString() throws Exception {
                LineString lineString = (LineString) wKTReader.read("LINESTRING(0 8, 1 8 , 3 8,  8  8, 10 8, 20 8)");
                Point point = (Point) wKTReader.read("POINT(1.5 4 )");
                //Test a point in a segment
                LineString result = GeometryEdit.insertVertexInLineString(lineString, point, 4);
                assertTrue(result.equals(wKTReader.read("LINESTRING(0 8, 1 8 , 1.5 8, 3 8,  8  8, 10 8, 20 8)")));
                //Test a point on an existing coordinate
                point = (Point) wKTReader.read("POINT(1 4 )");
                result = GeometryEdit.insertVertexInLineString(lineString, point, 4);
                //Because the geometry is not modified
                assertTrue(result == null);
        }

        /**
         * Insert a vertex into a linearring
         * @throws Exception
         */
        public void testInsertVertexInLinearRing() throws Exception {
                LinearRing linearRing = (LinearRing) wKTReader.read("LINEARRING(0 8, 1 8 , 3 8,  8  8, 10 8, 20 8, 0 8)");
                Point point = (Point) wKTReader.read("POINT(1.5 4 )");
                //Test a point in a segment
                LinearRing result = GeometryEdit.insertVertexInLinearRing(linearRing, point, 4);
                assertTrue(result.equals(wKTReader.read("LINEARRING(0 8, 1 8 , 1.5 8, 3 8,  8  8, 10 8, 20 8, 0 8)")));
                //Test a point on an existing coordinate
                point = (Point) wKTReader.read("POINT(1 4 )");
                result = GeometryEdit.insertVertexInLinearRing(linearRing, point, 4);
                //Because the geometry is not modified
                assertTrue(result == null);
        }

        /**
         * Insert a vertex into a linearring
         * @throws Exception
         */
        public void testInsertVertexInPolygon() throws Exception {
                Polygon polygon = (Polygon) wKTReader.read("POLYGON ((118 134, 118 278, 266 278, 266 134, 118 134 ))");
                Point point = (Point) wKTReader.read("POINT(196 278 )");
                //Test a point in a segment
                Polygon result = GeometryEdit.insertVertexInPolygon(polygon, point, 4);
                assertTrue(result.equals(wKTReader.read("POLYGON ((118 134, 118 278,196 278, 266 278, 266 134, 118 134 ))")));
                //Test a point on an existing coordinate
                point = (Point) wKTReader.read("POINT(196 300 )");
                result = GeometryEdit.insertVertexInPolygon(polygon, point, 4);
                //Because the geometry is not modified
                assertTrue(result == null);
        }

        /**
         * Test to split a polygon with a linestring
         * @throws Exception
         */
        public void testSplitPolygon() throws Exception {
                //Line intersects polygon
                Polygon polygon = (Polygon) wKTReader.read("POLYGON (( 0 0, 10 0, 10 10 , 0 10, 0 0))");
                LineString line = (LineString) wKTReader.read("LINESTRING (5 0, 5 10)");
                ArrayList<Polygon> pols = GeometryEdit.splitPolygon(polygon, line);
                assertTrue(pols.size() == 2);
                Polygon pol1 = (Polygon) wKTReader.read("POLYGON (( 0 0, 5 0, 5 10 , 0 10, 0 0))");
                Polygon pol2 = (Polygon) wKTReader.read("POLYGON ((5 0, 10 0 , 10 10, 5 10, 5 0))");

                for (Polygon pol : pols) {
                        if (pol.getEnvelopeInternal().equals(pol1.getEnvelopeInternal())) {
                                assertTrue(true);
                        } else if (pol.getEnvelopeInternal().equals(pol2.getEnvelopeInternal())) {
                                assertTrue(true);
                        } else {
                                assertTrue(false);
                        }

                }

                //Line within the polygon
                line = (LineString) wKTReader.read("LINESTRING (5 1, 5 8)");
                pols = GeometryEdit.splitPolygon(polygon, line);
                assertTrue(pols == null);

                //Line with one point intersection
                line = (LineString) wKTReader.read("LINESTRING (5 1, 5 12)");
                pols = GeometryEdit.splitPolygon(polygon, line);
                assertTrue(pols == null);

                //Line intersects a polygon with a hole
                polygon = (Polygon) wKTReader.read("POLYGON (( 0 0, 10 0, 10 10 , 0 10, 0 0), (2 2, 7 2, 7 7, 2 7, 2 2))");
                line = (LineString) wKTReader.read("LINESTRING (5 0, 5 10)");
                pols = GeometryEdit.splitPolygon(polygon, line);

                pol1 = (Polygon) wKTReader.read("POLYGON (( 0 0, 5 0, 5 2 ,2 2, 2 7, 5 7,  5 10, 0 10, 0 0))");
                pol2 = (Polygon) wKTReader.read("POLYGON ((5 0, 5 2, 7 2, 7 7 , 5 7, 5 10, 10 10, 10 0, 5 0))");
                for (Polygon pol : pols) {
                        if (pol.getEnvelopeInternal().equals(pol1.getEnvelopeInternal())) {
                                assertTrue(true);
                        } else if (pol.getEnvelopeInternal().equals(pol2.getEnvelopeInternal())) {
                                assertTrue(true);
                        } else {
                                assertTrue(false);
                        }

                }

                //Line intersects 2,5 polygon
                //Test if z values already exist
                polygon = (Polygon) wKTReader.read("POLYGON (( 0 0 1, 10 0 5, 10 10 8 , 0 10 12, 0 0 12))");
                line = (LineString) wKTReader.read("LINESTRING (5 0, 5 10)");
                pols = GeometryEdit.splitPolygon(polygon, line);
                for (Polygon pol : pols) {
                        assertTrue(GeometryTypeUtil.is2_5Geometry(pol));
                }

        }

        /**
         * Move a geometry to a new coordinate
         * @throws Exception
         */
        public void testMoveGeometry() throws Exception {
                Geometry geom = (Polygon) wKTReader.read("POLYGON (( 0 0 ,10 0, 10 10, 0 10, 0 0 ))");
                Point point = (Point) wKTReader.read("POINT (20 10)");
                //Test move a polygon
                Geometry result = GeometryEdit.moveGeometry(geom, new Coordinate(0, 0), point.getCoordinate());
                assertTrue(result.getCoordinates()[0].equals2D(point.getCoordinate()));

        }

        /**
         * Test cut a polygon
         * @throws Exception
         */
        public void testCutPolygon() throws Exception {
                Polygon polygon = (Polygon) wKTReader.read("POLYGON (( 0 0 ,10 0, 10 10, 0 10, 0 0 ))");
                Polygon cutter = (Polygon) wKTReader.read("POLYGON (( 2 2  ,7 2, 7 7, 2 7, 2 2))");
                //Test cut a polygon inside
                ArrayList<Polygon> result = GeometryEdit.cutPolygon(polygon, cutter);
                assertTrue(result.get(0).getNumInteriorRing() == 1);
                assertTrue(result.get(0).getInteriorRingN(0).getEnvelopeInternal().equals(cutter.getEnvelopeInternal()));

                //Test cut a polygon outside
                cutter = (Polygon) wKTReader.read("POLYGON (( 2 -1.8153735632183903, 7.177873563218391 -1.8153735632183903, 7.177873563218391 7, 2 7, 2 -1.8153735632183903 ))");
                result = GeometryEdit.cutPolygon(polygon, cutter);
                assertTrue(result.get(0).equals(wKTReader.read("POLYGON (( 2 0, 0 0, 0 10, 10 10, 10 0, 7.177873563218391 0, 7.177873563218391 7, 2 7, 2 0 ))")));


        }
}
