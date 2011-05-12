/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Adelin PIAU
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.sql.customQuery.spatial.geometry.topology;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.io.IOException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;

/**
 *
 * @author ebocher
 */
public class GraphNetwork {

        private DataSourceFactory dsf;
        private IProgressMonitor pm;
        private static String ID = "id";
        private static String INITIAL_NODE = "start_node";
        private static String FINAL_NODE = "end_node";
        GeometryFactory gf = new GeometryFactory();

        /**
         * This class is used to order edges and create requiered nodes to build a network graph
         * @param dsf
         * @param pm
         */
        public GraphNetwork(DataSourceFactory dsf, IProgressMonitor pm) {
                this.dsf = dsf;
                this.pm = pm;
        }

        /**
         * Create the two data structure nodes and edges using a RTree disk.
         * This method limits the overhead when the all nodes are ordered.
         * @param sds
         * @throws DriverException
         * @throws IOException
         * @throws NonEditableDataSourceException
         */
        public void buildGraph(SpatialDataSourceDecorator sds) throws DriverException, IOException, NonEditableDataSourceException {
                String src_sds_Name = sds.getName();
                pm.startTask("Create edges graph");
                sds.open();

                DefaultMetadata nodeMedata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                ID});

                DiskBufferDriver nodesDriver = new DiskBufferDriver(dsf, nodeMedata);
                String diskTreePath = dsf.getTempFile();
                DiskRTree diskRTree = new DiskRTree();
                diskRTree.newIndex(new File(diskTreePath));

                DefaultMetadata edgeMedata = new DefaultMetadata(sds.getMetadata());
                int srcFieldsCount = edgeMedata.getFieldCount();

                edgeMedata.addField(ID, TypeFactory.createType(Type.INT));
                edgeMedata.addField(INITIAL_NODE, TypeFactory.createType(Type.INT));
                edgeMedata.addField(FINAL_NODE, TypeFactory.createType(Type.INT));
                int fieldsCount = edgeMedata.getFieldCount();

                int idIndex = srcFieldsCount;
                int initialIndex = srcFieldsCount + 1;
                int finalIndex = srcFieldsCount + 2;

                DiskBufferDriver edgesDriver = new DiskBufferDriver(dsf, edgeMedata);

                long rowCount = sds.getRowCount();
                int gidNode = 1;

                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                        if (rowIndex / 100 == rowIndex / 100.0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo((int) (100 * rowIndex / rowCount));
                                }
                        }

                        final Value[] fieldsValues = sds.getRow(rowIndex);
                        final Value[] newValues = new Value[fieldsCount];
                        System.arraycopy(fieldsValues, 0, newValues, 0,
                                srcFieldsCount);
                        newValues[idIndex] = ValueFactory.createValue(rowIndex + 1);
                        Geometry geom = sds.getGeometry(rowIndex);
                        Coordinate[] cc = geom.getCoordinates();
                        Coordinate start = cc[0];
                        Coordinate end = cc[cc.length - 1];
                        int[] gidsStart = diskRTree.getRow(new Envelope(start));
                        if (gidsStart.length == 0) {
                                newValues[initialIndex] =
                                        ValueFactory.createValue(gidNode);
                                nodesDriver.addValues(new Value[]{ValueFactory.createValue(gf.createPoint(start)),
                                                ValueFactory.createValue(gidNode)});
                                diskRTree.insert(new Envelope(start), gidNode);
                                gidNode++;
                        } else {
                                newValues[initialIndex] =
                                        ValueFactory.createValue(gidsStart[0]);
                        }
                        int[] gidsEnd = diskRTree.getRow(new Envelope(end));
                        if (gidsEnd.length == 0) {
                                newValues[finalIndex] =
                                        ValueFactory.createValue(gidNode);
                                nodesDriver.addValues(new Value[]{ValueFactory.createValue(gf.createPoint(end)),
                                                ValueFactory.createValue(gidNode)});
                                diskRTree.insert(new Envelope(end), gidNode);
                                gidNode++;
                        } else {
                                newValues[finalIndex] =
                                        ValueFactory.createValue(gidsEnd[0]);
                        }
                        edgesDriver.addValues(newValues);

                }
                sds.close();
                nodesDriver.writingFinished();
                edgesDriver.writingFinished();
                String ds_nodes_name = dsf.getSourceManager().getUniqueName(src_sds_Name + "_nodes");
                dsf.getSourceManager().register(ds_nodes_name, nodesDriver);

                String ds_edges_name = dsf.getSourceManager().getUniqueName(src_sds_Name + "_edges");
                dsf.getSourceManager().register(ds_edges_name, edgesDriver);

                //Remove the Rtree on disk
                new File(diskTreePath).delete();

        }
}
