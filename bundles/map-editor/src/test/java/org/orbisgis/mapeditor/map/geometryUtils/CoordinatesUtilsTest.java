/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mapeditor.map.geometryUtils;

import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;

import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class CoordinatesUtilsTest {

        private WKTReader wKTReader = new WKTReader();

        /**
         * Test remove duplicate coordinates
         * @throws Exception
         */
        @Test
        public void testRemoveDuplicateCoordinates() throws Exception {
                //Test remove with a linestring
                Geometry geom = wKTReader.read("LINESTRING(0 8 10, 1 8 ,1 8, 3 8,  8  8, 10 8, 10 8, 20 8, 0 8)");
                Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(geom.getCoordinates());
                assertEquals(coords.length, 7);
        }

        @Test
        public void testFindFurthestPoint() throws Exception {
                Geometry geom = wKTReader.read("LINESTRING(0 1, 20 8, 20 0)");
                Coordinate expectedCoord = new Coordinate(20, 8);
                Coordinate[] coords = CoordinatesUtils.getFurthestCoordinate(new Coordinate(0, 0), geom.getCoordinates());
                double expectedDistance = new Coordinate(0, 0).distance(expectedCoord);
                assertEquals(expectedDistance, coords[0].distance(coords[1]), 10E-9);
        }

        @Test
        public void testLength3D() throws Exception {
                LineString geom = (LineString) wKTReader.read("LINESTRING(0 1 0, 5 1 0, 10 1 0)");
                double length = CoordinatesUtils.length3D(geom.getCoordinateSequence());
                double expectedLength = geom.getLength();
                assertEquals(expectedLength, length, 10E-11);
        }

        @Test
        public void testLength3DNaNZ() throws Exception {
                LineString geom = (LineString) wKTReader.read("LINESTRING(0 1, 5 1 0, 10 1 0)");
                double length = CoordinatesUtils.length3D(geom.getCoordinateSequence());
                assertEquals(0.0, length, 10E-11);
        }

        @Test
        public void testLength3D1() throws Exception {
                LineString geom = (LineString) wKTReader.read("LINESTRING(0 1 10, 10 1 0)");
                double length = CoordinatesUtils.length3D(geom.getCoordinateSequence());
                double expectedLength = 14.1421356237309;
                assertEquals(expectedLength, length, 10E-11);
        }

        @Test
        public void testLength3D2() throws Exception {
                Geometry geom = wKTReader.read("LINESTRING(0 1 10, 10 1 0)");
                double length = CoordinatesUtils.length3D(geom);
                double expectedLength = 14.1421356237309;
                assertEquals(expectedLength, length, 10E-11);
        }

        @Test
        public void testLength3D3() throws Exception {
                Geometry geom = wKTReader.read("MULTILINESTRING((0 1 10, 10 1 0),(0 1 10, 10 1 0))");
                double length = CoordinatesUtils.length3D(geom);
                double expectedLength = 14.1421356237309;
                assertEquals(expectedLength * 2, length, 10E-11);
        }

        @Test
        public void testLength3D4() throws Exception {
                Geometry geom = wKTReader.read("MULTILINESTRING((0 1 10, 10 1 0),(0 1 , 10 1 0))");
                double length = CoordinatesUtils.length3D(geom);
                double expectedLength = 14.1421356237309;
                assertEquals(expectedLength, length, 10E-11);
        }
        
        @Test
        public void testLength3D5() throws Exception {
                Geometry geom = wKTReader.read("GEOMETRYCOLLECTION(LINESTRING(0 1 10, 10 1 0),POINT(0 1))");
                double length = CoordinatesUtils.length3D(geom);
                double expectedLength = 14.1421356237309;
                assertEquals(expectedLength, length, 10E-11);
        }
        
        @Test
        public void testLength3D6() throws Exception {
                Geometry geom = wKTReader.read("LINESTRING(0 0 10, 0 10 10)");
                double length = CoordinatesUtils.length3D(geom);
                double expectedLength = geom.getLength();
                assertEquals(expectedLength, length, 10E-11);
        }
}
