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
package org.gdms.driver.ply;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.btree.DiskBTree;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;

/**
 *
 * @author Erwan Bocher
 */
public class PlyExporter {

        private static final Logger LOG = Logger.getLogger(PlyExporter.class);
        private final DataSet dataSet;
        private final File file;
        private boolean binary = false;
        private final DataSourceFactory dsf;
        private double X_REFERENCE = 0;
        private double Y_REFERENCE = 0;
        private final Coordinate coordRef;
        private int coordSize = 0;
        private int numFaces = 0;

        public PlyExporter(DataSourceFactory dsf, DataSet dataSet, File file) {
                this.dataSet = dataSet;
                this.file = file;
                this.dsf = dsf;
                coordRef = new Coordinate(X_REFERENCE, Y_REFERENCE);
        }

        public void setBinary(boolean binary) {
                this.binary = binary;
        }

        /**
         *
         * @param comment
         * @throws FileNotFoundException
         * @throws UnsupportedEncodingException
         * @throws IOException
         * @throws DriverException
         */
        public void write(String comment) throws FileNotFoundException, UnsupportedEncodingException, IOException, DriverException {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                try {
                        Writer writer = new OutputStreamWriter(bos, "UTF-8");
                        writer.write("ply\n");
                        writer.write("format ");
                        writer.write(binary ? "binary_big_endian" : "ascii");
                        writer.write(" 1.0\n");

                        //Write comment
                        if (comment != null) {
                                BufferedReader r = new BufferedReader(new StringReader(comment));
                                String commentLine;
                                while ((commentLine = r.readLine()) != null) {
                                        writer.write("comment ");
                                        writer.write(commentLine);
                                        writer.write('\n');
                                }
                        }

                        writeData(writer);



                } finally {
                        try {
                                bos.close();
                        } catch (IOException e) {
                                LOG.error("Failed to close output stream", e);
                        }
                }

        }

        private void writeData(Writer writer) throws DriverException, IOException {

                long count = dataSet.getRowCount();
                int fieldIndex = dataSet.getSpatialFieldIndex();
                String diskTreePath = dsf.getTempFile();
                DiskBTree diskBTree = new DiskBTree();
                diskBTree.newIndex(new File(diskTreePath));

                DefaultMetadata nodesMetaData = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.POINT),
                                TypeFactory.createType(Type.INT)}, new String[]{"the_geom",
                                "id"});
                DiskBufferDriver nodes = new DiskBufferDriver(dsf, nodesMetaData);

                DefaultMetadata polygonMetaData = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.STRING)}, new String[]{"num_vert",
                                "poly_ind"});
                DiskBufferDriver faces = new DiskBufferDriver(dsf, polygonMetaData);


                //TODO: Improve the management of line and points
               /*
                 * DefaultMetadata edgeMetaData = new DefaultMetadata(new
                 * Type[]{ TypeFactory.createType(Type.INT),
                 * TypeFactory.createType(Type.STRING)}, new
                 * String[]{"num_vert", "edge_ind"}); DiskBufferDriver edges =
                 * new DiskBufferDriver(dsf, edgeMetaData);
                 */



                for (int i = 0; i < count; i++) {
                        Geometry geom = dataSet.getGeometry(i, fieldIndex);
                        int numGeom = geom.getNumGeometries();
                        for (int j = 0; j < numGeom; j++) {
                                Geometry subGeom = geom.getGeometryN(j);
                                if (subGeom.getDimension() == 2) {
                                        processPolygon(geom, nodes, faces, diskBTree);
                                        numFaces++;
                                }
                        }
                }
                nodes.writingFinished();
                nodes.close();
                faces.writingFinished();
                faces.close();

                //Write header
                if (coordSize > 0) {
                        writer.write("element vertex " + coordSize + "\n");
                        writer.write("property " + "float" + " x\n");
                        writer.write("property " + "float" + " y\n");
                        writer.write("property " + "float" + " z\n");
                } else {
                        throw new DriverException("The dataset doesn't contain any vertexes.");
                }
                if (numFaces > 0) {
                        writer.write("element face " + count + "\n");
                        writer.write("property list uchar int vertex_indices\n");
                }
                writer.write("end_header\n");
                writer.flush();

                //Write data

                //Write nodes
                nodes.open();
                for (int i = 0; i < coordSize; i++) {
                        Coordinate coord = nodes.getGeometry(i, 0).getCoordinate();
                        writer.write(new Float(coord.x).toString());
                        writer.write(' ');
                        writer.write(new Float(coord.y).toString());
                        writer.write(' ');
                        double z = coord.z;
                        if (Double.isNaN(z)) {
                                z = 0;
                        }
                        writer.write(new Float(z).toString());
                        writer.write('\n');
                }
                nodes.close();

                faces.open();
                for (int i = 0; i < count; i++) {
                        String numPts = "" + faces.getInt(i, 0);
                        writer.write(numPts);
                        writer.write(' ');
                        writer.write(faces.getString(i, 1));
                        writer.write('\n');
                }
                faces.close();
                writer.flush();
        }

        private void processPolygon(Geometry geom, DiskBufferDriver nodes, DiskBufferDriver faces, DiskBTree diskBTree) throws IOException, DriverException {

                Coordinate[] coords = geom.getCoordinates();
                GeometryFactory gf = geom.getFactory();
                StringBuilder sb = new StringBuilder();
                int numCoord = 0;
                for (int j = 0; j < coords.length - 1; j++) {
                        Coordinate coordinate = coords[j];
                        double distance = coordinate.distance(coordRef);
                        int[] it = diskBTree.query(ValueFactory.createValue(distance));
                        if (it.length == 0) {
                                diskBTree.insert(ValueFactory.createValue(distance), coordSize);
                                Value valueCoordIndex = ValueFactory.createValue(coordSize);
                                nodes.addValues(new Value[]{ValueFactory.createValue(gf.createPoint(coordinate)), valueCoordIndex});
                                sb.append(coordSize);
                                coordSize++;
                        } else {
                                sb.append(it[0]);
                        }
                        sb.append(' ');
                        numCoord++;
                }
                faces.addValues(new Value[]{ValueFactory.createValue(numCoord), ValueFactory.createValue(sb.toString())});

        }
}
