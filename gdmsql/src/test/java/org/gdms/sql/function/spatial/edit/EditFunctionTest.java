/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.function.spatial.edit;

import org.gdms.sql.function.spatial.geometry.edit.ST_SetZToExtremities;
import com.vividsolutions.jts.geom.Coordinate;
import org.gdms.sql.function.spatial.geometry.edit.ST_3DReverse;
import org.gdms.sql.function.spatial.geometry.edit.ST_Reverse;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.edit.ST_AddZ;
import org.gdms.sql.function.spatial.geometry.edit.ST_LinearInterpolation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class EditFunctionTest extends FunctionTest {

        /**
         * A test to valid the ST_SetZToExtremities function
         * @throws Exception
         */
        @Test
        public void testSetZToExtremities() throws Exception {
                Geometry inputGeom = wktReader.read("LINESTRING(0 0 0, 5 0 , 10 0 10)");
                Value[] values = new Value[]{ValueFactory.createValue(inputGeom), ValueFactory.createValue(2), ValueFactory.createValue(1)};
                ST_SetZToExtremities function2 = new ST_SetZToExtremities();
                Geometry geom = function2.evaluate(null, values).getAsGeometry();
                Coordinate[] coords = geom.getCoordinates();
                assertEquals(coords[0].z, 2, 0);
                assertEquals(coords[2].z, 1, 0);

                values = new Value[]{ValueFactory.createValue(JTS3DCollection), ValueFactory.createValue(2), ValueFactory.createValue(1)};
                geom = function2.evaluate(null, values).getAsGeometry();
                assertTrue(geom.equalsExact(JTS3DCollection));
        }

        /**
         * A test to valid the ST_LINEARINTERPOLATION function
         * @throws Exception
         */
        @Test
        public void testST_LinearInterpolation() throws Exception {
                Geometry inputGeom = wktReader.read("LINESTRING(0 0 0, 5 0 , 10 0 10)");
                Value[] values = new Value[]{ValueFactory.createValue(inputGeom)};
                ST_LinearInterpolation function2 = new ST_LinearInterpolation();
                Geometry geom = function2.evaluate(null, values).getAsGeometry();
                Coordinate[] coords = geom.getCoordinates();
                assertTrue(coords[0].equals3D(inputGeom.getCoordinates()[0]));
                assertEquals(coords[1].z, 5, 0);

                values = new Value[]{ValueFactory.createValue(JTS3DCollection)};
                geom = function2.evaluate(null, values).getAsGeometry();
                assertTrue(geom.equalsExact(JTS3DCollection));
        }

        /**
         * A test for st_addz function
         * @throws Exception
         */
        @Test
        public void testST_AddZ() throws Exception {

                ST_AddZ st_AddZ = new ST_AddZ();
                Geometry v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue(JTSPoint2D), ValueFactory.createValue(10.2)}).getAsGeometry();
                assertTrue(v.getCoordinate().z == 10.2);

                v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue((Geometry) JTSPoint3D.clone()), ValueFactory.createValue(10.2)}).getAsGeometry();
                assertTrue(v.getCoordinate().z == 30.2);

                v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue((Geometry) JTSPoint3D.clone()), ValueFactory.createValue(-10)}).getAsGeometry();
                assertTrue(v.getCoordinate().z == 10);

        }

        public void testST_2DReverse() throws Exception {
                ST_Reverse sT_2DReverse = new ST_Reverse();
                Value[] values = new Value[]{ValueFactory.createValue(JTSLineString3D)};
                Value res = sT_2DReverse.evaluate(dsf, values);
                assertTrue(res.getAsGeometry().equalsExact(wktReader.read("LINESTRING (1 1 1, 2 1 2, 2 2 3, 1 2 4, 1 1 5)")));
        }

        public void testST_3DReverse() throws Exception {
                ST_3DReverse sT_3DReverse = new ST_3DReverse();
                Value[] values = new Value[]{ValueFactory.createValue(JTSLineString3D)};
                Value res = sT_3DReverse.evaluate(dsf, values);
                assertTrue(res.getAsGeometry().equalsExact(wktReader.read("LINESTRING (1 1 5, 1 2 4, 2 2 3, 2 1 2, 1 1 1)")));
        }
}
