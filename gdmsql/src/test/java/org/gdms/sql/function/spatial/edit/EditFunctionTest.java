/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.function.spatial.edit;

import org.orbisgis.progress.NullProgressMonitor;
import org.gdms.driver.DataSet;
import com.vividsolutions.jts.io.ParseException;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.types.Type;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.spatial.geometry.edit.ST_SetZToExtremities;
import com.vividsolutions.jts.geom.Coordinate;
import org.gdms.sql.function.spatial.geometry.edit.ST_3DReverse;
import org.gdms.sql.function.spatial.geometry.edit.ST_Reverse;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.edit.ST_AddZ;
import org.gdms.sql.function.spatial.geometry.edit.ST_LinearInterpolation;
import org.gdms.sql.function.spatial.geometry.edit.ST_SplitLine;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class EditFunctionTest extends FunctionTest {

        @Test
        public void testST_SplitLineString() throws Exception {
                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY, new Constraint[]{new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_LINE)})});

                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(wktReader.read("LINESTRING ( 1 1, 6 1 )"))
                        });
                driver1.addValues(new Value[]{ValueFactory.createValue(wktReader.read("LINESTRING ( 2 0, 2 2, 4 4, 4 0 )"))
                        });
                driver1.addValues(new Value[]{ValueFactory.createValue(wktReader.read("LINESTRING ( 5 0, 5 5)"))
                        });

                ST_SplitLine sT_SplitLine = new ST_SplitLine();
                DataSet[] tables = new DataSet[]{driver1};
                DataSet evaluate = sT_SplitLine.evaluate(dsf, tables, null, new NullProgressMonitor());
                assertTrue(evaluate.getRowCount() == 3);
                assertTrue(evaluate.getGeometry(0, 0).equals(wktReader.read("MULTILINESTRING ((1 1, 2 1), (2 1, 4 1), (4 1, 5 1), (5 1, 6 1))")));
                assertTrue(evaluate.getGeometry(1, 0).equals(wktReader.read("MULTILINESTRING ((2 0, 2 1), (2 1, 2 2, 4 4, 4 1), (4 1, 4 0))")));
                assertTrue(evaluate.getGeometry(2, 0).equals(wktReader.read("MULTILINESTRING ((5 0, 5 1), (5 1, 5 5))")));

        }

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
