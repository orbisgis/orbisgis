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
package org.gdms.sql.function.spatial.tin.analysis;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class ST_TriangleSlopeTest extends FunctionTest {

    @Test
    public void testST_TriangleSlope() throws Exception {
        ST_TriangleSlope fun = new ST_TriangleSlope();
        assertTrue(Type.DOUBLE == fun.getType(null));
        Geometry geom = gf.createLinearRing(new Coordinate[]{
            new Coordinate(0, 0, 0),
            new Coordinate(4, 0, 0),
            new Coordinate(2, 2, 10),
            new Coordinate(0, 0, 0)
        });
        double slope = 100 * 10 / 2;
        Value out = fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});
        assertTrue(slope == out.getAsDouble());
        geom = gf.createPoint(new Coordinate(42, 42, 42));
        try {
            fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testST_TriangleFlatSlope() throws Exception {
        ST_TriangleSlope fun = new ST_TriangleSlope();
        Geometry geom = gf.createLinearRing(new Coordinate[]{
            new Coordinate(0, 0, 0),
            new Coordinate(4, 0, 0),
            new Coordinate(2, 3, 0),
            new Coordinate(0, 0, 0)
        });
        Value out = fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});        
        assertTrue(out.getAsDouble()==0);
    }

    @Test
    public void testST_TINSlopeDirection() throws Exception {
        ST_TriangleDirection fun = new ST_TriangleDirection();
        assertTrue(Type.LINESTRING == fun.getType(null));
        Geometry geom = gf.createLinearRing(new Coordinate[]{
            new Coordinate(0, 0, 0),
            new Coordinate(4, 0, 0),
            new Coordinate(2, 3, 9),
            new Coordinate(0, 0, 0)
        });
        Value out = fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});
        LineString ls = gf.createLineString(new Coordinate[]{
            new Coordinate(2, 1, 3),
            new Coordinate(2, 0, 0)
        });
        assertTrue(ls.equals(out.getAsGeometry()));
    }

    @Test
    public void testST_TINSlopeFlatDirection() throws Exception {
        ST_TriangleDirection fun = new ST_TriangleDirection();
        assertTrue(Type.LINESTRING == fun.getType(null));
        Geometry geom = gf.createLinearRing(new Coordinate[]{
            new Coordinate(0, 0, 0),
            new Coordinate(4, 0, 0),
            new Coordinate(2, 3, 0),
            new Coordinate(0, 0, 0)
        });
        Value out = fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});        
        assertTrue(out.isNull());
    }
    
    @Test
    public void testST_TriangleFlatAspect() throws Exception {
        ST_TriangleAspect fun = new ST_TriangleAspect();
        Geometry geom = gf.createLinearRing(new Coordinate[]{
            new Coordinate(0, 0, 0),
            new Coordinate(4, 0, 0),
            new Coordinate(2, 3, 0),
            new Coordinate(0, 0, 0)
        });
        Value out = fun.evaluate(dsf, new Value[]{ValueFactory.createValue(geom)});        
        assertTrue(out.getAsDouble()==0);
    }
}
