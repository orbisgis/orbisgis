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
package org.gdms.sql.function.spatial.geometryProperties;

import com.vividsolutions.jts.geom.Geometry;
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
import org.gdms.sql.function.spatial.geometry.properties.ST_NumInteriorRing;
import org.gdms.sql.function.spatial.geometry.properties.ST_NumPoints;
import org.gdms.sql.function.spatial.geometry.properties.ST_X;
import org.gdms.sql.function.spatial.geometry.properties.ST_Y;
import org.gdms.sql.function.spatial.geometry.properties.ST_Z;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.spatial.geometry.properties.ST_InteriorRingN;

public class PropertiesFunctionTest extends FunctionTest {

        public void testArea() throws Exception {
                double d = testSpatialFunction(new ST_Area(), JTSMultiPolygon2D, 1).getAsDouble();
                assertTrue(JTSMultiPolygon2D.getArea() == d);
        }

        public void testDimension() throws Exception {
                int d = testSpatialFunction(new ST_Dimension(), JTSMultiPolygon2D, 1).getAsInt();
                assertTrue(JTSMultiPolygon2D.getDimension() == d);
                d = testSpatialFunction(new ST_Dimension(), JTSMultiLineString2D, 1).getAsInt();
                assertTrue(JTSMultiLineString2D.getDimension() == d);
                d = testSpatialFunction(new ST_Dimension(), JTSMultiPoint2D, 1).getAsInt();
                assertTrue(JTSMultiPoint2D.getDimension() == d);
        }

        public void testGeometryN() throws Exception {
                Geometry d = evaluate(new ST_GeometryN(), new Value[]{ValueFactory.createValue(JTSGeometryCollection), ValueFactory.createValue(1)}).getAsGeometry();
                assertTrue(JTSGeometryCollection.getGeometryN(1).equals(d));
        }

        public void testGeometryType() throws Exception {
                String v = testSpatialFunction(new ST_GeometryType(), JTSMultiPolygon2D, 1).getAsString();
                assertTrue(JTSMultiPolygon2D.getGeometryType().equals(v));
        }

        public void testGetZValue() throws Exception {
                Value v = testSpatialFunction(new ST_Z(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertTrue(v.isNull());
                double d = testSpatialFunction(new ST_Z(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertTrue(d == 23);
        }

        public void testGetX() throws Exception {
                Value v = testSpatialFunction(new ST_X(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertTrue(!v.isNull());
                double d = testSpatialFunction(new ST_X(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertTrue(d == 0);
        }

        public void testGetY() throws Exception {
                Value v = testSpatialFunction(new ST_Y(), new GeometryFactory().createPoint(new Coordinate(0, 50)), 1);
                assertTrue(!v.isNull());
                double d = testSpatialFunction(new ST_Y(),
                        new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1).getAsDouble();
                assertTrue(d == 50);
        }

        public void testIsEmpty() throws Exception {
                boolean v = testSpatialFunction(new ST_IsEmpty(),
                        new GeometryFactory().createLinearRing(new Coordinate[0]), 1).getAsBoolean();
                assertTrue(v);
        }

        public void testIsSimple() throws Exception {
                boolean v = testSpatialFunction(new ST_IsSimple(), JTSMultiLineString2D, 1).getAsBoolean();
                assertTrue(v == JTSMultiLineString2D.isSimple());
        }

        public void testIsValid() throws Exception {
                boolean v = testSpatialFunction(new ST_IsValid(), JTSMultiLineString2D, 1).getAsBoolean();
                assertTrue(v == JTSMultiLineString2D.isValid());
        }

        public void testLength() throws Exception {
                double v = testSpatialFunction(new ST_Length(), JTSMultiLineString2D, 1).getAsDouble();
                assertTrue(v == JTSMultiLineString2D.getLength());
        }

        public void testNumPoints() throws Exception {
                int v = testSpatialFunction(new ST_NumPoints(), JTSMultiLineString2D, 1).getAsInt();
                assertTrue(v == JTSMultiLineString2D.getNumPoints());
        }

        public void testNumInteriorRing() throws Exception {
                int v = evaluate(new ST_NumInteriorRing(), new Value[]{ValueFactory.createValue(JTSPolygonWith2Holes)}).getAsInt();
                Polygon p = (Polygon) JTSPolygonWith2Holes;
                assertTrue(p.getNumInteriorRing() == v);
        }

        /**
         * Test interiorRingN function
         * @throws Exception
         */
        public void testInteriorRingN() throws Exception {
                Polygon p = (Polygon) JTSPolygon2D;
                Geometry v = evaluate(new ST_InteriorRingN(), new Value[]{ValueFactory.createValue(JTSPolygon2D), ValueFactory.createValue(1)}).getAsGeometry();
                assertTrue(v.equals(p.getInteriorRingN(1)));
                p = (Polygon) JTSPolygonWith2Holes;
                v = evaluate(new ST_InteriorRingN(), new Value[]{ValueFactory.createValue(JTSPolygonWith2Holes), ValueFactory.createValue(1)}).getAsGeometry();
                assertTrue(v.equals(p.getInteriorRingN(1)));
        }
}
