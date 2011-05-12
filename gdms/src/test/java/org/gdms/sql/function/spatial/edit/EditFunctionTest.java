/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.function.spatial.edit;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.edit.ST_AddZ;

/**
 *
 * @author ebocher
 */
public class EditFunctionTest extends FunctionTest {

        /**
         * A test for st_addz function
         * @throws Exception
         */
        public void testST_AddZ() throws Exception {

                ST_AddZ st_AddZ = new ST_AddZ();
                Geometry v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue(JTSPoint2D), ValueFactory.createValue(10.2)}).getAsGeometry();
                assertTrue(v.getCoordinate().z==10.2);

                v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue((Geometry)JTSPoint3D.clone()), ValueFactory.createValue(10.2)}).getAsGeometry();
                assertTrue(v.getCoordinate().z==30.2);

                v = evaluate(st_AddZ, new Value[]{ValueFactory.createValue((Geometry)JTSPoint3D.clone()), ValueFactory.createValue(-10)}).getAsGeometry();
                assertTrue(v.getCoordinate().z==10);

        }
}
