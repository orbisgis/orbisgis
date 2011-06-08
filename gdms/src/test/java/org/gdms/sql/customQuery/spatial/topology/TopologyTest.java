/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.customQuery.spatial.topology;

import com.vividsolutions.jts.io.WKTReader;
import java.util.Arrays;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.spatial.geometry.topology.ST_Graph;
import org.gdms.sql.customQuery.spatial.geometry.topology.ST_PlanarGraph;
import org.orbisgis.progress.NullProgressMonitor;

/**
 *
 * @author ebocher
 */
public class TopologyTest extends TestCase {

        private static DataSourceFactory dsf = new DataSourceFactory();

        /**
         * A test to validate the planar graph method with two polygons.
         *
         * @throws Exception
         */
        public void testST_PlanarGraph() throws Exception {
                WKTReader wktr = new WKTReader();

                //Input datasource
                final GenericObjectDriver driver_src = new GenericObjectDriver(
                        new String[]{"the_geom", "id"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)
                        });

                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON( ( 69 152, 69 293, 221 293, 221 152, 69 152 ))")),
                                ValueFactory.createValue(1)});
                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON( ( 221 152, 221 293, 390 293, 390 152, 221 152 ))")),
                                ValueFactory.createValue(2)});


                DataSource srcDS = dsf.getDataSource(driver_src);

                ST_PlanarGraph st_PlanarGraph = new ST_PlanarGraph();
                DataSource[] tables = new DataSource[]{srcDS};
                st_PlanarGraph.evaluate(dsf, tables, new Value[]{ValueFactory.createValue("the_geom")}, new NullProgressMonitor());


                DataSource dsResult_polygons = dsf.getDataSource(srcDS.getName() + "_polygons");


                dsResult_polygons.open();
                //The planar graph returns 2 polygons
                assertTrue(dsResult_polygons.getRowCount() == 2);

                dsResult_polygons.close();
                DataSource dsResult_edges = dsf.getDataSource(srcDS.getName() + "_edges");

                dsResult_edges.open();
                //The planar graph returns 3 lines
                assertTrue(dsResult_edges.getRowCount() == 3);

                //Test the gid values for the 3 lines                
                Value[] row0 = dsResult_edges.getRow(0);
                //This row corresponds to the shared segment between geometry from row0 and row1
                Value[] row1 = dsResult_edges.getRow(1);
                Value[] row2 = dsResult_edges.getRow(2);

                int gidRight = row1[4].getAsInt();
                int gidLeft = row1[5].getAsInt();

                //Check the left and right gids
                //if the gids are not equal do a flip test, otherwise the topology is not valid.
                if (gidLeft == 1 && gidRight == 2) {
                        //Left gid
                        assertTrue(row0[5].getAsInt() == -1);
                        assertTrue(row2[5].getAsInt() == -1);
                        //Right gid
                        assertTrue(row0[4].getAsInt() == 1);
                        assertTrue(row2[4].getAsInt() == 2);
                } else if (gidLeft == 2 && gidRight == 1) {
                        //Left gid
                        assertTrue(row0[5].getAsInt() == -1);
                        assertTrue(row2[5].getAsInt() == -1);
                        //Right gid
                        assertTrue(row0[4].getAsInt() == 2);
                        assertTrue(row2[4].getAsInt() == 1);
                } else {
                        assertTrue("The topology is not valid", false);
                }

                //Check the start and end gids
                assertTrue(row0[2].getAsInt() == 1);
                assertTrue(row0[3].getAsInt() == 2);
                assertTrue(row1[2].getAsInt() == 1);
                assertTrue(row1[3].getAsInt() == 2);
                assertTrue(row2[2].getAsInt() == 2);
                assertTrue(row2[3].getAsInt() == 1);

                dsResult_edges.close();
        }

        /**
         * A test to validate the network graph method
         * @throws Exception
         */
        public void testST_Graph() throws Exception {

                WKTReader wktr = new WKTReader();

                //Input datasource
                final GenericObjectDriver driver_src = new GenericObjectDriver(
                        new String[]{"the_geom", "gid"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)
                        });

                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 86 191, 214 228, 340 306 )")),
                                ValueFactory.createValue(1)});
                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 214 228, 329 127 )")),
                                ValueFactory.createValue(2)});
                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 82 74, 179 106, 342 61 )")),
                                ValueFactory.createValue(3)});
                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 329 127, 170.88433908045977 148.3647988505747 )")),
                                ValueFactory.createValue(4)});


                // Expected result
                final GenericObjectDriver driver_out_src = new GenericObjectDriver(
                        new String[]{"the_geom", "gid", "id", "start_node", "end_node"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT)
                        });

                // insert all filled rows...
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 86 191, 214 228, 340 306 )")),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(2)});
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 214 228, 329 127 )")),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(3),
                                ValueFactory.createValue(4)});
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 82 74, 179 106, 342 61 )")),
                                ValueFactory.createValue(3),
                                ValueFactory.createValue(3),
                                ValueFactory.createValue(5),
                                ValueFactory.createValue(6)});
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 329 127, 170.88433908045977 148.3647988505747 )")),
                                ValueFactory.createValue(4),
                                ValueFactory.createValue(4),
                                ValueFactory.createValue(4),
                                ValueFactory.createValue(7)});

                DataSource srcDS = dsf.getDataSource(driver_src);

                ST_Graph st_Graph = new ST_Graph();
                DataSource[] tables = new DataSource[]{srcDS};
                st_Graph.evaluate(dsf, tables, new Value[]{ValueFactory.createValue("the_geom")}, new NullProgressMonitor());


                DataSource dsResult_nodes = dsf.getDataSource(srcDS.getName() + "_nodes");


                dsResult_nodes.open();
                //The planar graph returns 7 nodes
                assertTrue(
                        dsResult_nodes.getRowCount() == 7);
                dsResult_nodes.close();

                DataSource dsResult_edges = dsf.getDataSource(srcDS.getName() + "_edges");
                DataSource expectedDS = dsf.getDataSource(driver_out_src);

                dsResult_edges.open();
                //The planar graph returns 3 lines. The same as input.
                assertTrue(
                        dsResult_edges.getRowCount() == 4);

                expectedDS.open();
                //Check if all values are equals between input datasource and excepted datasource


                for (int i = 0; i
                        < expectedDS.getRowCount(); i++) {
                        assertTrue(checkIsPresent(expectedDS.getRow(i), dsResult_edges));


                }
                expectedDS.close();
                dsResult_edges.close();



        }

        /**
         * Check if the line expected is present in the table out.
         * @param expected
         * @param out
         * @return
         * @throws Exception 
         */
        private boolean checkIsPresent(Value[] expected, DataSource out) throws Exception {
                for (long i = 0; i
                        < out.getRowCount(); i++) {
                        Value[] vals = out.getRow(i);


                        if (expected[0].getAsGeometry().equals(vals[0].getAsGeometry())) {
                                return Arrays.equals(expected, out.getRow(i));


                        }
                }
                return false;

        }
}
