/*
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

package org.gdms.sql.function.spatial.geometry.properties;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValuesTest;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for XMin, XMax, YMin, YMax
 * @author Nicolas Fortin
 */
public class ST_XYMinMaxTest extends FunctionTest {
    private static final double epsilon = 1e-16;
    @Test
    public void testMinMaxValid() throws FunctionException {
        GeometryFactory gf = new GeometryFactory();
        // Functions
        Function xMin = new ST_XMin();
        Function xMax = new ST_XMax();
        Function yMin = new ST_YMin();
        Function yMax = new ST_YMax();
        Function zMin = new ST_ZMin();
        Function zMax = new ST_ZMax();
        // Geometries
        Value point = ValueFactory.createValue(gf.createPoint(new Coordinate(15,15,15)));
        Value multiPoints = ValueFactory.createValue(gf.createMultiPoint(
                new Coordinate[]{new Coordinate(60, 6, 7), new Coordinate(50, 6, 7), new Coordinate(90, 6, 7), new Coordinate(15, 6, 7)}));
        LinearRing linearRing = gf.createLinearRing (
                new Coordinate[]{new Coordinate(50, 60, 70),new Coordinate(5, 6, 7),new Coordinate(80, 90, 100),new Coordinate(50, 60, 70),});
        Value polygon = ValueFactory.createValue(gf.createPolygon(linearRing,null));
        Value nullValue = ValueFactory.createNullValue();
        Value nanValue =  ValueFactory.createValue(gf.createPoint(new Coordinate(Double.NaN, Double.NaN, Double.NaN)));
        // Evaluations
        // XMin
        assertEquals(15, evaluate(xMin, point).getAsDouble(), epsilon);
        assertEquals(15, evaluate(xMin, multiPoints).getAsDouble(), epsilon);
        assertEquals(5, evaluate(xMin, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(xMin, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(xMin, nanValue).getAsDouble(), epsilon);
        // XMax
        assertEquals(15, evaluate(xMax, point).getAsDouble(), epsilon);
        assertEquals(90, evaluate(xMax, multiPoints).getAsDouble(), epsilon);
        assertEquals(80, evaluate(xMax, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(xMax, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(xMax, nanValue).getAsDouble(), epsilon);
        // YMin
        assertEquals(15, evaluate(yMin, point).getAsDouble(), epsilon);
        assertEquals(6, evaluate(yMin, multiPoints).getAsDouble(), epsilon);
        assertEquals(6, evaluate(yMin, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(yMin, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(yMin, nanValue).getAsDouble(), epsilon);
        // YMax
        assertEquals(15, evaluate(yMax, point).getAsDouble(), epsilon);
        assertEquals(6, evaluate(yMax, multiPoints).getAsDouble(), epsilon);
        assertEquals(90, evaluate(yMax, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(yMax, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(yMax, nanValue).getAsDouble(), epsilon);
        // ZMin
        assertEquals(15, evaluate(zMin, point).getAsDouble(), epsilon);
        assertEquals(7, evaluate(zMin, multiPoints).getAsDouble(), epsilon);
        assertEquals(7, evaluate(zMin, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(zMin, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(zMin, nanValue).getAsDouble(), epsilon);
        // ZMax
        assertEquals(15, evaluate(zMax, point).getAsDouble(), epsilon);
        assertEquals(7, evaluate(zMax, multiPoints).getAsDouble(), epsilon);
        assertEquals(100, evaluate(zMax, polygon).getAsDouble(), epsilon);
        assertTrue(evaluate(zMax, nullValue).isNull());
        assertEquals(Double.NaN, evaluate(xMax, nanValue).getAsDouble(), epsilon);
    }
}
