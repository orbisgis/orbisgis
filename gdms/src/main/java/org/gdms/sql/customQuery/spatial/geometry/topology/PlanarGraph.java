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
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Adelin PIAU
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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.gdms.data.DataSourceCreationException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import java.io.File;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.gdms.GdmsWriter;

public class PlanarGraph {

        GeometryFactory gf = new GeometryFactory();
        public Collection edges;
        private DataSourceFactory dsf;
        private IProgressMonitor pm;
        private static String ID = "id";
        private static String LEFT_FACE = "left_polygon";
        private static String RIGHT_FACE = "right_polygon";
        private static String INITIAL_NODE = "start_node";
        private static String FINAL_NODE = "end_node";
        public final static Integer MINUS_ONE = new Integer(-1);
        private String ds_edges_name = "_edges";
        private String ds_nodes_name = "_nodes";
        private String ds_polygons_name = "_polygons";
        private String src_sds_Name;

        /**
         * This class is used to computed a planar graph where spatial entities are represented in 3 datasources
         * : points, lines and polygons.
         * A topological data structures is used to represent  topological spatial relationships between points, lines and polygons.
         * 
         * @param dsf
         * @param pm
         */
        public PlanarGraph(DataSourceFactory dsf, IProgressMonitor pm) {
                this.pm = pm;
                this.dsf = dsf;
        }

        /**
         * Create the datasources that contains edges without self-intersection and
         * all nodes of the graph with a primary ID
         * @param sds
         * @throws DriverException, IOException
         */
        public void buildGraph(SpatialDataSourceDecorator sds) throws DriverException, IOException {
                sds.open();
                src_sds_Name = sds.getName();
                pm.startTask("Create edges graph");

                DefaultMetadata edgeMedata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY,
                                new Constraint[]{new GeometryConstraint(
                                        GeometryConstraint.LINESTRING)}),
                                TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT)}, new String[]{
                                "the_geom", ID, INITIAL_NODE, FINAL_NODE, RIGHT_FACE, LEFT_FACE});
                int edgesFieldsCount = edgeMedata.getFieldCount();
                // Get linear elements from all geometries in the layer
                Collection<Geometry> geomColl = getLines(sds);

                // Create the edge layer by merging lines between 3+ order nodes
                // (Merged lines are multilines)
                LineMerger lineMerger = new LineMerger();
                for (Geometry geometry : geomColl) {
                        lineMerger.add(geometry);
                }
                sds.close();


                DefaultMetadata nodeMedata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                ID});

                DiskBufferDriver nodeDriver = new DiskBufferDriver(dsf, nodeMedata);


                DiskRTree diskRTree = new DiskRTree();
                diskRTree.newIndex(new File(dsf.getTempFile()));


                edges = lineMerger.getMergedLineStrings();


                ds_edges_name = dsf.getSourceManager().getUniqueName(src_sds_Name + ds_edges_name);

                //Write the result
                File out = new File(ds_edges_name + ".gdms");

                if (out.exists()) {
                        out.delete();
                }
                GdmsWriter edgesDriver = new GdmsWriter(out);
                edgesDriver.writeMetadata(edges.size(), edgeMedata);

                int gidNode = 1;
                int i = 1;

                Value[] values = new Value[edgesFieldsCount];
                for (Iterator it = edges.iterator(); it.hasNext();) {
                        Geometry geom = (Geometry) it.next();
                        values[0] = ValueFactory.createValue(geom);
                        values[1] = ValueFactory.createValue(i);
                        i++;
                        Coordinate[] cc = geom.getCoordinates();
                        Coordinate start = cc[0];
                        Coordinate end = cc[cc.length - 1];
                        int[] gidsStart = diskRTree.getRow(new Envelope(start));
                        if (gidsStart.length == 0) {
                                values[2] = ValueFactory.createValue(gidNode);
                                nodeDriver.addValues(new Value[]{ValueFactory.createValue(gf.createPoint(start)),
                                                ValueFactory.createValue(gidNode)});
                                diskRTree.insert(new Envelope(start), gidNode);
                                gidNode++;
                        } else {
                                values[2] = ValueFactory.createValue(gidsStart[0]);
                        }
                        int[] gidsEnd = diskRTree.getRow(new Envelope(end));
                        if (gidsEnd.length == 0) {
                                values[3] = ValueFactory.createValue(gidNode);
                                nodeDriver.addValues(new Value[]{ValueFactory.createValue(gf.createPoint(end)),
                                                ValueFactory.createValue(gidNode)});
                                diskRTree.insert(new Envelope(end), gidNode);
                                gidNode++;
                        } else {
                                values[3] = ValueFactory.createValue(gidsEnd[0]);
                        }
                        values[4] = ValueFactory.createValue(-1);
                        values[5] = ValueFactory.createValue(-1);
                        edgesDriver.addValues(values);

                }

                // write the row indexes
                edgesDriver.writeRowIndexes();
                // write envelope
                edgesDriver.writeExtent();
                edgesDriver.close();

                nodeDriver.writingFinished();

                ds_nodes_name = dsf.getSourceManager().getUniqueName(src_sds_Name + ds_nodes_name);
                dsf.getSourceManager().register(ds_nodes_name, nodeDriver);
                dsf.getSourceManager().register(ds_edges_name, out);
        }

        /**
         * Extract all lines as a set of connected and splitted lines.
         * Self-intersection is not allowed.
         * This method uses the union operator. It musts be changed in the futur to limit memory overhead.
         * @param sds
         * @return
         * @throws DriverException
         */
        public Collection<Geometry> getLines(SpatialDataSourceDecorator sds)
                throws DriverException {

                LineNoder linenoder = new LineNoder(sds);
                Collection lines = linenoder.getLines();

                final Geometry nodedGeom = linenoder.getNodeLines((List) lines);

                return linenoder.toLines(nodedGeom);

        }

        /**
         * Create the datasource that contains polygons.
         * @param inputSDS
         * @param omEdges
         * @throws DriverException
         * @throws NonEditableDataSourceException
         * @throws NoSuchTableException
         * @throws IndexException
         */
        public void createPolygonAndTopology() throws DriverException,
                NonEditableDataSourceException, NoSuchTableException,
                IndexException,
                IOException,
                DriverLoadException,
                DataSourceCreationException {
                // Create the face layer

                DefaultMetadata faceMedata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                ID});

                ds_polygons_name = dsf.getSourceManager().getUniqueName(src_sds_Name + ds_polygons_name);

                //Write the result
                File out = new File(ds_polygons_name + ".gdms");

                if (out.exists()) {
                        out.delete();
                }
                GdmsWriter faceDriver = new GdmsWriter(out);

                Polygonizer polygonizer = new Polygonizer();
                polygonizer.add(edges);
                Collection polygons = polygonizer.getPolygons();

                faceDriver.writeMetadata(polygons.size(), faceMedata);

                int no = 1;
                for (Iterator it = polygons.iterator(); it.hasNext();) {
                        Geometry face = (Geometry) it.next();
                        face.normalize(); // add on 2007-08-11
                        faceDriver.addValues(new Value[]{ValueFactory.createValue(face),
                                        ValueFactory.createValue(new Integer(no++))});

                }


                // write the row indexes
                faceDriver.writeRowIndexes();
                // write envelope
                faceDriver.writeExtent();
                faceDriver.close();

                dsf.getSourceManager().register(ds_polygons_name, out);

                SpatialDataSourceDecorator sdsFaces = new SpatialDataSourceDecorator(
                        dsf.getDataSource(ds_polygons_name));

                SpatialDataSourceDecorator sdsEdges = new SpatialDataSourceDecorator(
                        dsf.getDataSource(ds_edges_name));

                sdsFaces.open();
                sdsEdges.open();

                // inscrit les numéros de face dans les arcs
                // Les arcs qui sont en bords de face sont codés à -1.

                int leftFaceIndex = sdsEdges.getFieldIndexByName(LEFT_FACE);
                int rigthFaceIndex = sdsEdges.getFieldIndexByName(RIGHT_FACE);


                long rowCount = sdsEdges.getRowCount();
                for (int i = 0; i < rowCount; i++) {

                        Geometry g1 = sdsEdges.getGeometry(i);

                        sdsEdges.setInt(i, leftFaceIndex,
                                MINUS_ONE);
                        sdsEdges.setInt(i, rigthFaceIndex,
                                MINUS_ONE);
                        Iterator<Integer> iterator = query(sdsFaces, g1.getEnvelopeInternal());

                        while (iterator.hasNext()) {
                                Integer index = iterator.next();
                                Geometry g = sdsFaces.getGeometry(index);
                                Value val = ValueFactory.createValue(index + 1);
                                IntersectionMatrix im = g1.relate(g);
                                // intersection between boundaries has dimension 1
                                if (im.matches("*1*******")) {
                                        int edgeC0 = getIndex(g1.getCoordinates()[0], g);
                                        int edgeC1 = getIndex(g1.getCoordinates()[1], g);

                                        // The Math.abs(edgeC1-edgeC0) test inverse the rule when
                                        // the two
                                        // consecutive
                                        // points are the last point and the first point of a
                                        // ring...

                                        if ((edgeC1 > edgeC0 && Math.abs(edgeC1 - edgeC0) == 1)
                                                || (edgeC1 < edgeC0 && Math.abs(edgeC1 - edgeC0) > 1)) {
                                                sdsEdges.setFieldValue(i, rigthFaceIndex, val);
                                        } else {
                                                sdsEdges.setFieldValue(i, leftFaceIndex, val);
                                        }
                                } // intersection between the line and the polygon interior has
                                // dimension
                                // 1
                                else if (im.matches("1********")) {
                                        sdsEdges.setFieldValue(i, rigthFaceIndex, val);
                                        sdsEdges.setFieldValue(i, leftFaceIndex, val);

                                } // intersection between the line and the polygon exterior has
                                // dimension
                                // 1
                                // else if (im.matches("F********")) {}
                                else;

                        }

                }

                sdsEdges.commit();
                sdsEdges.close();
                sdsFaces.close();

        }

        // Returns the index of c in the geometry g or -1 if c is not a vertex of g
        private int getIndex(Coordinate c, Geometry g) {
                Coordinate[] cc = g.getCoordinates();
                for (int i = 0; i < cc.length; i++) {
                        if (cc[i].equals(c)) {
                                return i;
                        }
                }
                return -1;
        }

        /**
         * A quick search for geometries, using an envelope comparison.
         *
         * @param envelope
         *            the envelope to query against
         * @return geometries whose envelopes intersect the given envelope
         */
        public Iterator<Integer> query(SpatialDataSourceDecorator sdsFaces, Envelope envelope)
                throws DriverException, NoSuchTableException, IndexException {

                if (!dsf.getIndexManager().isIndexed(ds_polygons_name, "the_geom")) {
                        dsf.getIndexManager().buildIndex(ds_polygons_name, "the_geom", pm);
                }

                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(envelope, "the_geom");
                Iterator<Integer> it = sdsFaces.queryIndex(query);
                return it;
        }
}
