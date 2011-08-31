/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.properties;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.gdms.data.values.Value;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.properties.ST_Area;
import org.gdms.sql.function.spatial.geometry.properties.ST_Dimension;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryN;
import org.gdms.sql.function.spatial.geometry.properties.ST_GeometryType;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsEmpty;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsSimple;
import org.gdms.sql.function.spatial.geometry.properties.ST_IsValid;
import org.gdms.sql.function.spatial.geometry.properties.ST_Length;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumInteriorRings;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumPoints;
import org.gdms.sql.function.spatial.geometry.properties.ST_X;
import org.gdms.sql.function.spatial.geometry.properties.ST_Y;
import org.gdms.sql.function.spatial.geometry.properties.ST_Z;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryTypeUtil;
import org.gdms.sql.function.spatial.geometry.properties.ST_CircleCompacity;
import org.gdms.sql.function.spatial.geometry.properties.ST_CoordDim;
import org.gdms.sql.function.spatial.geometry.properties.ST_InteriorRingN;

import static org.junit.Assert.*;

public class PropertiesFunctionTest extends FunctionTest {

        @Test
        public void testCoordDim() throws Exception {
                int d = testSpatialFunction(new ST_CoordDim(), JTSMultiPolygon2D, 1).getAsInt();
                assertTrue(d == 2);
                d = testSpatialFunction(new ST_CoordDim(), JTSLineString3D, 1).getAsInt();
                assertTrue(d == 3);
                d = testSpatialFunction(new ST_CoordDim(), wktReader.read("LINESTRING(0 0, 1 1 2)"), 1).getAsInt();
                assertTrue(d == 3);
        }

        @Test
        public void testArea() throws Exception {
                double d = testSpatialFunction(new ST_Area(), JTSMultiPolygon2D, 1).getAsDouble();
                assertEquals(JTSMultiPolygon2D.getArea(), d, 0);
        }

        @Test
        public void testDimension() throws Exception {
                int d = testSpatialFunction(new ST_Dimension(), JTSMultiPolygon2D, 1).getAsInt();
                assertEquals(JTSMultiPolygon2D.getDimension(), d);
                d = testSpatialFunction(new ST_Dimension(), JTSMultiLineString2D, 1).getAsInt();
                assertEquals(JTSMultiLineString2D.getDimension(), d);
                d = testSpatialFunction(new ST_Dimension(), JTSMultiPoint2D, 1).getAsInt();
                assertEquals(JTSMultiPoint2D.getDimension(), d);
        }

        @Test
        public void testGeometryN() throws Exception {
                Geometry d = evaluate(new ST_GeometryN(), new Value[]{ValueFactory.createValue(JTSGeometryCollection),
                                ValueFactory.createValue(1)}).getAsGeometry();
                assertEquals(JTSGeometryCollection.getGeometryN(1), d);
        }

        @Test
        public void testGeometryType() throws Exception {
                String v = testSpatialFunction(new ST_GeometryType(), JTSMultiPolygon2D, 1).getAsString();
                assertEquals(JTSMultiPolygon2D.getGeometryType(), v);
        }

        @Test
        public void testGetZValue() throws Exception {
                Value v = testSpatialFunction(new ST_Z(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertTrue(v.isNull());
                double d = testSpatialFunction(new ST_Z(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertEquals(d, 23, 0);
        }

        @Test
        public void testGetX() throws Exception {
                Value v = testSpatialFunction(new ST_X(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertFalse(v.isNull());
                double d = testSpatialFunction(new ST_X(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertEquals(d, 0, 0);
        }

        @Test
        public void testGetY() throws Exception {
                Value v = testSpatialFunction(new ST_Y(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertFalse(v.isNull());
                double d = testSpatialFunction(new ST_Y(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertEquals(d, 50, 0);
        }

        @Test
        public void testIsEmpty() throws Exception {
                boolean v = testSpatialFunction(new ST_IsEmpty(),
                        new GeometryFactory().createLinearRing(new Coordinate[0]), 1).getAsBoolean();
                assertTrue(v);
        }

        @Test
        public void testIsSimple() throws Exception {
                boolean v = testSpatialFunction(new ST_IsSimple(), JTSMultiLineString2D, 1).getAsBoolean();
                assertEquals(v, JTSMultiLineString2D.isSimple());
        }

        @Test
        public void testIsValid() throws Exception {
                boolean v = testSpatialFunction(new ST_IsValid(), JTSMultiLineString2D, 1).getAsBoolean();
                assertEquals(v, JTSMultiLineString2D.isValid());
        }

        @Test
        public void testLength() throws Exception {
                double v = testSpatialFunction(new ST_Length(), JTSMultiLineString2D, 1).getAsDouble();
                assertEquals(v, JTSMultiLineString2D.getLength(), 0);
        }

        @Test
        public void testNumPoints() throws Exception {
                int v = testSpatialFunction(new ST_NumPoints(), JTSMultiLineString2D, 1).getAsInt();
                assertEquals(v, JTSMultiLineString2D.getNumPoints());
        }

        @Test
        public void testNumInteriorRing() throws Exception {
                int v1 = testSpatialFunction(new ST_NumInteriorRings(), JTSPolygon2D, 1).getAsInt();
                Polygon p1 = (Polygon) JTSPolygon2D;
                assertEquals(p1.getNumInteriorRing(), v1);

                int v2 = testSpatialFunction(new ST_NumInteriorRings(), JTSMultiPolygon2D, 1).getAsInt();

                int v3 = testSpatialFunction(new ST_NumInteriorRings(), JTSMultiLineString2D, 1).getAsInt();
        }

        /**  
         * Test interiorRingN function  
         * @throws Exception  
         */
        @Test
        public void testInteriorRingN() throws Exception {
                Polygon p = (Polygon) JTSPolygon2D;
                Geometry v = evaluate(new ST_InteriorRingN(), new Value[]{ValueFactory.createValue(JTSPolygon2D), ValueFactory.createValue(1)}).getAsGeometry();
                assertEquals(v, p.getInteriorRingN(1));
                p = (Polygon) JTSPolygonWith2Holes;
                v = evaluate(new ST_InteriorRingN(), new Value[]{ValueFactory.createValue(JTSPolygonWith2Holes), ValueFactory.createValue(1)}).getAsGeometry();
                assertEquals(v, p.getInteriorRingN(1));
        }

        /* 
         * Test the circle compacity function 
         */
        @Test
        public void testCircleCompacity() throws Exception {
                //Test with a circle 
                Geometry circle = JTSPoint2D.buffer(20);
                double v = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(circle)}).getAsDouble();
                assertEquals(v, 1, 0.01);
                //Test with a polygon 
                v = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(JTSPolygon2D)}).getAsDouble();
                assertTrue(v < 1);
                //More precise test with a polygon (a square, in fact). 
                GeometryFactory gf = new GeometryFactory();
                Polygon pl = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                                new Coordinate(0, 0),
                                new Coordinate(1, 0),
                                new Coordinate(1, 1),
                                new Coordinate(0, 1),
                                new Coordinate(0, 0),}), new LinearRing[]{});
                v = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(pl)}).getAsDouble();
                assertEquals(Math.sqrt(Math.PI) / 2, v, 0.01);
                //Test with a multipolygon 
                Value r = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(JTSMultiPolygon2D)});
                assertTrue(r.isNull());
                //Test with a point 
                r = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(JTSPoint2D)});
                assertTrue(r.isNull());
                //Test with a linestring 
                r = evaluate(new ST_CircleCompacity(), new Value[]{ValueFactory.createValue(JTSLineString2D)});
                assertTrue(r.isNull());

        }
}
