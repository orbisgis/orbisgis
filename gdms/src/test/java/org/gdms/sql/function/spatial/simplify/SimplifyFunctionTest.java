/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.function.spatial.simplify;

import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.simplify.ST_PrecisionReducer;

import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class SimplifyFunctionTest extends FunctionTest {

        @Test
        public void testST_PrecisionReducer() throws Exception {
                int precision = 2;
                ST_PrecisionReducer sT_PrecisionReducer = new ST_PrecisionReducer();
                Value precisionValue = ValueFactory.createValue(precision);

                WKTReader wKTReader = new WKTReader();
                Geometry JTSPrecisePoint2D = wKTReader.read("POINT(102.531 220.41)");

                Value vg = ValueFactory.createValue(JTSPrecisePoint2D);
                Value value = evaluate(sT_PrecisionReducer, vg, precisionValue);
                Coordinate coord = value.getAsGeometry().getCoordinate();

                assertTrue((coord.x == 102.53) && (coord.y == 220.41));

                Geometry JTSPrecisePoint3D = wKTReader.read("POINT(102.531 220.41 8.002)");

                vg = ValueFactory.createValue(JTSPrecisePoint3D);
                value = evaluate(sT_PrecisionReducer, vg, precisionValue);
                coord = value.getAsGeometry().getCoordinate();

                assertTrue((coord.x == 102.53) && (coord.y == 220.41) && (coord.z == 8.002));
        }
}
