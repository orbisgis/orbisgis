/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly: info@orbisgis.org
 */
package org.gdms.sql.function;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.gdms.sql.function.spatial.geometry.distance.ST_LocateAlong;
import org.gdms.sql.function.spatial.geometry.distance.ST_NearestPoints;
import org.junit.Test;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;

import org.gdms.sql.function.spatial.geometry.distance.ST_FurthestPoint;
import org.gdms.sql.function.spatial.geometry.distance.ST_ProjectTo;
import static org.junit.Assert.*;

public class DistanceTest extends FunctionTest {

    /**
     * Test the ST_PointAlongLine function
     *
     * @throws Exception
     */
    @Test
    public void testST_PointAlongLine() throws Exception {
        ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
        Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300))");
        Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
        Value result = evaluate(sT_PointAlongLine, values);
        Geometry input = wktReader.read("MULTIPOINT ((310 200), (90 200), (200 310), (200 90))");
        assertTrue(result.getAsGeometry().equals(input));
    }

    /**
     * Test the ST_PointAlongLine function
     *
     * @throws Exception
     */
    @Test
    public void testNegativeOffSetST_PointAlongLine() throws Exception {
        ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
        Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300))");
        Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(-10)};
        Value result = evaluate(sT_PointAlongLine, values);
        Geometry input = wktReader.read("MULTIPOINT ((200 110), (290 200), (200 290), (110 200))");
        assertTrue(result.getAsGeometry().equals(input));
    }

    @Test
    public void testST_PointAlongLineWithHole() throws Exception {
        ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
        Polygon geom = (Polygon) wktReader.read("POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300), (150 240, 250 240, 250 160, 150 160, 150 240))");
        Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
        Value result = evaluate(sT_PointAlongLine, values);
        Geometry input = wktReader.read("MULTIPOINT ((310 200), (90 200), (200 310), (200 90))");
        assertTrue(result.getAsGeometry().equals(input));
    }

    @Test
    public void testST_PointAlongLineWithLine() throws Exception {
        ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
        LineString geom = (LineString) wktReader.read("LINESTRING (100 300, 300 300, 300 100)");
        Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
        Value result = evaluate(sT_PointAlongLine, values);
        Geometry input = wktReader.read("MULTIPOINT ((310 200), (200 310))");
        assertTrue(result.getAsGeometry().equals(input));
    }

    @Test
    public void testST_PointAlongLineCollection() throws Exception {
        ST_LocateAlong sT_PointAlongLine = new ST_LocateAlong();
        Geometry geom = wktReader.read("GEOMETRYCOLLECTION (POLYGON ((100 300, 350 300, 350 100, 100 100, 100 300)), LINESTRING (100 350, 350 350), LINESTRING (50 300, 50 100))");
        Value[] values = new Value[]{ValueFactory.createValue(geom), ValueFactory.createValue(0.5), ValueFactory.createValue(10)};
        Value result = evaluate(sT_PointAlongLine, values);
        Geometry input = wktReader.read("MULTIPOINT ((225 310), (225 360), (90 200), (360 200), (60 200), (225 90))");
        assertTrue(result.getAsGeometry().equals(input));
    }

    @Test
    public void testST_FurthestPoint() throws Exception {
        ST_FurthestPoint sT_FurthestPoint = new ST_FurthestPoint();
        Geometry geom = wktReader.read("LINESTRING(0 1, 20 8, 20 0)");
        Geometry base = wktReader.read("POINT(0 0)");
        Geometry expectedGeom = wktReader.read("POINT(20 8)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_FurthestPoint, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_NearestPoints() throws Exception {
        ST_NearestPoints sT_NearestPoints = new ST_NearestPoints();
        Geometry geom = wktReader.read("POLYGON ((150 420, 110 150, 305 148, 300 360, 150 420))");
        Geometry base = wktReader.read("POINT (40 270)");
        Geometry expectedGeom = wktReader.read("POINT (125.89261744966443 257.2751677852349)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_NearestPoints, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_ProjectTo() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("LINESTRING (-5 5, 11 5)");
        Geometry base = wktReader.read("POINT(0 0)");
        Geometry expectedGeom = wktReader.read("POINT (0 5)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_ProjectTo1() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("MULTILINESTRING ((-5 5, 11 5), (-5 -2, 20 -2))");
        Geometry base = wktReader.read("POINT(0 0)");
        Geometry expectedGeom = wktReader.read("POINT (0 -2)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_ProjectTo2() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("LINESTRING (0 0, 10 0)");
        Geometry base = wktReader.read("POINT(5 5)");
        Geometry expectedGeom = wktReader.read("POINT (5 0)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_Project3() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("MULTILINESTRING ((357635 6789300, 358259 "
                + "6789277, 359425 6789433, 359994 6789299))");
        Geometry base = wktReader.read("POINT (357904 6789139)");
        Geometry expectedGeom = wktReader.read("POINT (357909.56128031184 6789289.879952809)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_Project4() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("LINESTRING(10 0 , 20 0)");
        Geometry base = wktReader.read("POINT( 0 5)");
        Geometry expectedGeom = wktReader.read("POINT (10 0)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }

    @Test
    public void testST_Project5() throws Exception {
        ST_ProjectTo sT_ProjectTo = new ST_ProjectTo();
        Geometry geom = wktReader.read("LINESTRING(10 0 , 20 0)");
        Geometry base = wktReader.read("POINT(22 5)");
        Geometry expectedGeom = wktReader.read("POINT (20 0)");
        Value[] values = new Value[]{ValueFactory.createValue(base),
            ValueFactory.createValue(geom)};
        Value result = evaluate(sT_ProjectTo, values);
        assertTrue(result.getAsGeometry().equals(expectedGeom));
    }
}
