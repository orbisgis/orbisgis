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
         * A test to validate the planar graph method
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

                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 69 152, 69 293, 221 293, 221 152, 69 152 )")),
                                ValueFactory.createValue(1)});
                driver_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 221 152, 221 293, 390 293, 390 152, 221 152 )")),
                                ValueFactory.createValue(2)});

                // Expected result
                final GenericObjectDriver driver_out_src = new GenericObjectDriver(
                        new String[]{"the_geom", "id", "start_node", "end_node", "right_polygon", "left_polygon"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT)
                        });

                // insert all filled rows...
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 221 293, 390 293, 390 152, 221 152 )")),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(-1)});
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 221 293, 221 152 )")),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(2)});
                driver_out_src.addValues(new Value[]{ValueFactory.createValue(wktr.read("LINESTRING ( 221 152, 69 152, 69 293, 221 293 )")),
                                ValueFactory.createValue(3),
                                ValueFactory.createValue(2),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(1),
                                ValueFactory.createValue(-1)});

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
                DataSource exceptedDS = dsf.getDataSource(driver_out_src);

                dsResult_edges.open();
                //The planar graph returns 3 lines
                assertTrue(dsResult_edges.getRowCount() == 3);

                exceptedDS.open();
                //Check if all values are equals between input datasource and excepted datasource
                for (int i = 0; i < dsResult_edges.getRowCount(); i++) {
                        assertTrue(Arrays.equals(dsResult_edges.getRow(i), exceptedDS.getRow(i)));
                }
                exceptedDS.close();
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
                assertTrue(dsResult_nodes.getRowCount() == 7);
                dsResult_nodes.close();

                DataSource dsResult_edges = dsf.getDataSource(srcDS.getName() + "_edges");
                DataSource exceptedDS = dsf.getDataSource(driver_out_src);

                dsResult_edges.open();
                //The planar graph returns 3 lines. The same as input.
                assertTrue(dsResult_edges.getRowCount() == 4);

                exceptedDS.open();
                //Check if all values are equals between input datasource and excepted datasource
                for (int i = 0; i < dsResult_edges.getRowCount(); i++) {
                        assertTrue(Arrays.equals(dsResult_edges.getRow(i), exceptedDS.getRow(i)));
                }
                exceptedDS.close();
                dsResult_edges.close();

        }
}
