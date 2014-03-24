/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver.tin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import org.gdms.driver.DriverException;

/**
 * This class is used to write a Delaunay triangulation into a ply format.
 * http://local.wasp.uwa.edu.au/~pbourke/dataformats/ply/
 * The TIN file extension is used to qualify the TIN.
 *
 * A basic file description is used :
 *
 * ply
 * format ascii 1.0 { ascii/binary, format version number }
 * made by GDMS { comments keyword specified, like all lines }
 * element vertex 8 { define "vertex" element, 8 of them in file }
 * property float x { vertex contains float "x" coordinate }
 * property float y { y coordinate is also a vertex property }
 * property float z { z coordinate, too }
 * element face 6 { there are 6 "triangles" elements in the file }
 * property list uchar int vertex_index { "vertex_indices" is a list of ints }
 * end_header { delimits the end of the header }
 *
 * @author Erwan Bocher
 */
public final class TINWriter {

        private final File file;
        private static final Logger LOG = Logger.getLogger(TINWriter.class);
        private boolean binary = false;
        private int dataType = 1;
        private static final int DOUBLE_DATA_TYPE = 2;

        /**
         * Creates a new writer with a path to specify the output file.
         *
         * @param path path the to file
         * @throws IOException
         */
        public TINWriter(String path) throws IOException {
                this(new File(path));
        }

        public TINWriter(File file) throws IOException {
                if (checkFileExtension(file)) {
                        this.file = file;
                } else {
                        throw new IOException("The file name extension must be .tin");
                }
        }

        public void setBinary(boolean binary) {
                this.binary = binary;
        }

        public void setDataType(int dataType) {
                this.dataType = dataType;
        }

        /**
         * Writes the triangles and their points id into a ASCII ply format.
         *
         *
         * @param mesh
         * @param comment
         * @throws IOException
         * @throws DriverException
         */
        public void writeFile(ConstrainedMesh mesh, String comment) throws IOException, DriverException {
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

                        List<DTriangle> triangles = mesh.getTriangleList();
                        List<DPoint> points = mesh.getPoints();

                        //Write header only if the triangle list is not empty
                        if (!triangles.isEmpty()) {
                                writer.write("element vertex " + points.size() + "\n");
                                if (dataType == DOUBLE_DATA_TYPE) {
                                        writer.write("property " + "double" + " x\n");
                                        writer.write("property " + "double" + " y\n");
                                        writer.write("property " + "double" + " z\n");

                                } else {
                                        writer.write("property " + "float" + " x\n");
                                        writer.write("property " + "float" + " y\n");
                                        writer.write("property " + "float" + " z\n");
                                }

                                writer.write("element face " + triangles.size() + "\n");
                                writer.write("property list uchar int vertex_indices\n");
                                writer.write("end_header\n");
                                writer.flush();
                        } else {
                                throw new DriverException("The triangulation doesn't contain any triangles.");
                        }
                        // Write triangles and points
                        if (binary) {
                                writeBinary(triangles, points, bos, dataType);
                        } else {
                                writeAscii(triangles, points, bos, dataType);
                        }
                } finally {
                        try {
                                bos.close();
                        } catch (IOException e) {
                                LOG.error("Failed to close output stream", e);
                        }
                }
        }

        /**
         * Writes the triangles and their points id into a binary ply format.
         *
         * Data is stored in double representation.
         *
         * @param triangles
         * @param points
         * @param bos
         * @throws IOException
         */
        private void writeAsDoubleBinary(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos) throws IOException {
                DataOutputStream dos = new DataOutputStream(bos);
                for (DPoint dPoint : points) {
                        dos.writeDouble(dPoint.getX());
                        dos.writeDouble(dPoint.getY());
                        dos.writeDouble(dPoint.getZ());
                }
                for (DTriangle dTriangle : triangles) {
                        dos.writeByte(3);
                        List<DPoint> pts = dTriangle.getPoints();
                        for (DPoint dPoint : pts) {
                                dos.writeInt(dPoint.getGID() - 1);
                        }

                }
                dos.flush();
        }

        /**
         * This method writes the triangles and their points id into a binary ply format
         * Data are stored in float representation.
         *
         * @param triangles
         * @param points
         * @param bos
         * @throws IOException
         */
        private void writeAsFloatBinary(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos) throws IOException {
                DataOutputStream dos = new DataOutputStream(bos);
                for (DPoint dPoint : points) {
                        dos.writeFloat((float) dPoint.getX());
                        dos.writeFloat((float) dPoint.getY());
                        dos.writeFloat((float) dPoint.getZ());
                }
                for (DTriangle dTriangle : triangles) {
                        dos.writeByte(3);
                        List<DPoint> pts = dTriangle.getPoints();
                        for (DPoint dPoint : pts) {
                                dos.writeInt(dPoint.getGID() - 1);
                        }

                }
                dos.flush();
        }

        /**
         * Writes the triangles and their points id into a ASCII ply format.
         *
         * Data are stored in float representation.
         *
         * @param triangles
         * @param points
         * @param bos
         * @throws IOException
         */
        private void writeAsFloatAscii(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos) throws IOException {
                Writer writer = new OutputStreamWriter(bos, "UTF-8");
                for (DPoint dPoint : points) {
                        writer.write(new Float(dPoint.getX()).toString());
                        writer.write(' ');
                        writer.write(new Float(dPoint.getY()).toString());
                        writer.write(' ');
                        writer.write(new Float(dPoint.getZ()).toString());
                        writer.write('\n');
                }

                for (DTriangle dTriangle : triangles) {
                        writer.write("3 ");
                        List<DPoint> pts = dTriangle.getPoints();
                        for (DPoint dPoint : pts) {
                                writer.write(Integer.toString(dPoint.getGID() - 1));
                                writer.write(' ');
                        }
                        writer.write('\n');
                }
                writer.flush();
        }

        /**
         * Writes the triangles and their points id into a ASCII ply format.
         *
         * Data are stored in double representation.
         *
         * @param triangles
         * @param points
         * @param bos
         * @throws IOException
         */
        private void writeAsDoubleAscii(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos) throws IOException {
                Writer writer = new OutputStreamWriter(bos, "UTF-8");
                for (DPoint dPoint : points) {
                        writer.write(Double.toString(dPoint.getX()));
                        writer.write(' ');
                        writer.write(Double.toString(dPoint.getY()));
                        writer.write(' ');
                        writer.write(Double.toString(dPoint.getZ()));
                        writer.write('\n');
                }

                for (DTriangle dTriangle : triangles) {
                        writer.write("3 ");
                        List<DPoint> pts = dTriangle.getPoints();
                        for (DPoint dPoint : pts) {
                                writer.write(Integer.toString(dPoint.getGID() - 1));
                                writer.write(' ');
                        }
                        writer.write('\n');
                }
                writer.flush();

        }

        /**
         * Writes triangles into a ply binary format.
         *
         * @param triangles
         * @param points
         * @param bos
         * @param dataType
         * @throws IOException
         */
        private void writeBinary(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos, int dataType) throws IOException {

                if (dataType == DOUBLE_DATA_TYPE) {
                        writeAsDoubleBinary(triangles, points, bos);
                } else {
                        writeAsFloatBinary(triangles, points, bos);
                }
        }

        /**
         * Writes triangles into a ply ASCII format.
         *
         * @param triangles
         * @param points
         * @param bos
         * @param dataType
         * @throws IOException
         */
        private void writeAscii(List<DTriangle> triangles, List<DPoint> points, BufferedOutputStream bos, int dataType) throws IOException {
                if (dataType == DOUBLE_DATA_TYPE) {
                        writeAsDoubleAscii(triangles, points, bos);
                } else {
                        writeAsFloatAscii(triangles, points, bos);
                }
        }

        public boolean checkFileExtension(File file) {
                return file.getName().toLowerCase().endsWith(".tin");
        }
}
