/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
package org.gdms.sql.function.spatial.distance;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.gdms.sql.function.spatial.geometry.distance.ST_LocateAlong;
import org.junit.Test;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;

import static org.junit.Assert.*;

public class DistanceTest extends FunctionTest {

        /**
         * Test the ST_PointAlongLine function
         * @throws Exception
         */
        @Test
        public void testST_PointAlongLine() throws Exception {
                ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
                Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300))");
                Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
                Value result = evaluate(sT_PointAlongLine, values);
                System.out.println(result.getAsGeometry());
                Geometry input = wktReader.read("MULTIPOINT ((310 200), (90 200), (200 310), (200 90))");
                assertTrue(result.getAsGeometry().equals(input));
        }

        /**
         * Test the ST_PointAlongLine function
         * @throws Exception
         */
        @Test
        public void testNegativeOffSetST_PointAlongLine() throws Exception {
                ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
                Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300))");
                Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(-10)};
                Value result = evaluate(sT_PointAlongLine, values);
                System.out.println(result.getAsGeometry());
                Geometry input = wktReader.read("MULTIPOINT ((200 110), (290 200), (200 290), (110 200))");
                assertTrue(result.getAsGeometry().equals(input));
        }

        @Test
        public void testST_PointAlongLineWithHole() throws Exception {
                ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
                Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300), (150 240, 250 240, 250 160, 150 160, 150 240))");
                Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
                Value result = evaluate(sT_PointAlongLine, values);
                System.out.println(result.getAsGeometry());
                Geometry input = wktReader.read("MULTIPOINT ((310 200), (90 200), (200 310), (200 90))");
                assertTrue(result.getAsGeometry().equals(input));
        }

        @Test
        public void testST_PointAlongLineWithLine() throws Exception {
                ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
                LineString geom = (LineString) wktReader.read("LINESTRING (100 300, 300 300, 300 100)");
                Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
                Value result = evaluate(sT_PointAlongLine, values);
                System.out.println(result.getAsGeometry());
                Geometry input = wktReader.read("MULTIPOINT ((310 200), (200 310))");
                assertTrue(result.getAsGeometry().equals(input));
        }

        @Test
        public void testST_PointAlongLineCollection() throws Exception {
                ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
                Geometry geom = wktReader.read("GEOMETRYCOLLECTION (POLYGON ((100 300, 350 300, 350 100, 100 100, 100 300)), LINESTRING (100 350, 350 350), LINESTRING (50 300, 50 100))");
                Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
                Value result = evaluate(sT_PointAlongLine, values);
                System.out.println(result.getAsGeometry());
                Geometry input = wktReader.read("MULTIPOINT ((225 310), (225 360), (90 200), (360 200), (60 200), (225 90))");
                assertTrue(result.getAsGeometry().equals(input));
        }
}
