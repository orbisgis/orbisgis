/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.solene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Dimension3DConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.DataSet;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.log4j.Logger;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.schema.SchemaMetadata;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.driver.AbstractDataSet;

public final class CirDriver extends AbstractDataSet implements FileReadWriteDriver {

        public static final String DRIVER_NAME = "Solene Cir driver";
        private static final String EXTENSION = "cir";
        private Scanner in;
        private List<Value[]> rows;
        private Envelope envelope;
        private PrintWriter out;
        private Schema schema;
        private File file;
        private static final Logger LOG = Logger.getLogger(CirDriver.class);
        // final static String COORD3D_WRITTING_FORMAT = "\t%g\t%g\t%g\r\n";
        private static final String COORD3D_WRITTING_FORMAT = "\t%10.5f\t%10.5f\t%10.5f\r\n";

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                in.close();
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening " + file.getAbsolutePath());
                try {
                        rows = new ArrayList<Value[]>();

                        in = new Scanner(file);
                        in.useLocale(Locale.US); // essential to read float values

                        final int nbFacesCir = in.nextInt();
                        in.next(); // useless "supNumFaces"
                        for (int i = 0; i < 10; i++) {
                                in.next(); // 5 rows of 2 useless values
                        }

                        final GeometryFactory geometryFactory = new GeometryFactory();
                        for (int i = 0; i < nbFacesCir; i++) {
                                readFace(geometryFactory);
                        }

                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                } catch (InvalidTypeException e) {
                        throw new DriverException(e);
                }
        }

        private void readFace(final GeometryFactory geometryFactory)
                throws DriverException {
                final String faceIdx = in.next();
                if (!faceIdx.startsWith("f")) {
                        throw new DriverException("Bad CIR file format (f) !");
                }
                final int nbContours = in.nextInt();
                final Coordinate normal = readCoordinate();
                for (int boundIdx = 0; boundIdx < nbContours; boundIdx++) {
                        readBound(geometryFactory, faceIdx, boundIdx, normal);
                }
        }

        private void readBound(final GeometryFactory geometryFactory,
                final String faceIdx, final int boundIdx, final Coordinate normal)
                throws DriverException {
                final String tmpNbHoles = in.next();
                if (!tmpNbHoles.startsWith("c")) {
                        throw new DriverException("Bad CIR file format (c) !");
                }
                final int nbHoles = Integer.parseInt(tmpNbHoles.substring(1));

                final LinearRing shell = readLinearRing(geometryFactory);
                final LinearRing[] holes = readHoles(geometryFactory, nbHoles);

                Geometry geom = geometryFactory.createPolygon(shell, holes);
                if (Geometry3DUtilities.scalarProduct(normal, Geometry3DUtilities.computeNormal((Polygon) geom)) < 0) {
                        geom = Geometry3DUtilities.reverse((Polygon) geom);
                }

                if (null == envelope) {
                        envelope = geom.getEnvelopeInternal();
                } else {
                        envelope.expandToInclude(geom.getEnvelopeInternal());
                }

                rows.add(new Value[]{
                                ValueFactory.createValue(faceIdx + "_" + boundIdx),
                                ValueFactory.createValue(geom)});
        }

        private LinearRing readLinearRing(
                final GeometryFactory geometryFactory) {
                Coordinate[] points = null;
                final int nbPoints = in.nextInt();
                if (1 < nbPoints) {
                        points = new Coordinate[nbPoints];
                        for (int i = 0; i < nbPoints; i++) {
                                points[i] = readCoordinate();
                        }
                }
                return geometryFactory.createLinearRing(points);
        }

        private LinearRing[] readHoles(
                final GeometryFactory geometryFactory, final int nbHoles)
                throws DriverException {
                LinearRing[] holes = null;
                if (0 < nbHoles) {
                        holes = new LinearRing[nbHoles];
                        for (int i = 0; i < nbHoles; i++) {
                                if (!in.next().equals("t")) {
                                        throw new DriverException("Bad CIR file format (t) !");
                                }
                                holes[i] = readLinearRing(geometryFactory);
                        }
                }
                return holes;
        }

        private Coordinate readCoordinate() {
                return new Coordinate(in.nextDouble(), in.nextDouble(), in.nextDouble());
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                final TypeDefinition[] result = new TypeDefinition[2];
                result[0] = new DefaultTypeDefinition("STRING", Type.STRING, new int[]{
                                Constraint.UNIQUE, Constraint.NOT_NULL});
                result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY,
                        new int[]{Constraint.DIMENSION_3D_GEOMETRY});
                return result;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creating source file in " + path);
                try {
                        int spatialFieldIndex = MetadataUtilities.getGeometryFieldIndex(metadata);
                        checkGeometryConstraint(metadata, spatialFieldIndex);

                        final File outFile = new File(path);
                        outFile.getParentFile().mkdirs();
                        outFile.createNewFile();
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void writeFile(final File file, final DataSet dataSource,
                ProgressMonitor pm) throws DriverException {
                LOG.trace("Writing to file " + file.getAbsolutePath());

                final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata());
                checkGeometryConstraint(dataSource.getMetadata(), spatialFieldIndex);
                try {
                        out = new PrintWriter(new FileOutputStream(file));
                        final long rowCount = dataSource.getRowCount();
                        pm.startTask("Writing file", rowCount);

                        // write header part...
                        out.printf("%d %d\r\n", rowCount, rowCount);
                        for (int i = 0; i < 5; i++) {
                                out.printf("\t\t%d %d\r\n", 99999, 99999);
                        }

                        // write body part...
                        for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                                if (rowIndex >= 100 && rowIndex % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(rowIndex);
                                        }
                                }
                                Value v = dataSource.getFieldValue(rowIndex, spatialFieldIndex);
                                if (!v.isNull()) {
                                        Geometry g = v.getAsGeometry();
                                        if (g instanceof Polygon) {
                                                writeAPolygon((Polygon) g, rowIndex);
                                        } else if (g instanceof MultiPolygon) {
                                                writeAMultiPolygon((MultiPolygon) g, rowIndex);
                                        } else {
                                                throw new DriverException("Geometric field (row "
                                                        + rowIndex + ") is not a (multi-)polygon !");
                                        }
                                }
                        }
                        pm.progressTo(rowCount);
                        pm.endTask();
                        out.close();
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * We want to manage only polygons and multi-polygons. Check that the
         * metadata force that behaviour.
         * @param metadata
         * @param spatialFieldIndex
         * @throws DriverException 
         */
        private void checkGeometryConstraint(final Metadata metadata,final int spatialFieldIndex) 
                                throws DriverException {
                Type fieldType = metadata.getFieldType(spatialFieldIndex);
                Dimension3DConstraint dc = (Dimension3DConstraint) fieldType.getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                GeometryDimensionConstraint gdc = 
                        (GeometryDimensionConstraint) fieldType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                final int geometryType = fieldType.getTypeCode();
                boolean nullT = geometryType == Type.NULL;
                //notPolyT set to true if we don't have a polygonal type.
                boolean notPolyT = geometryType != Type.POLYGON && geometryType != Type.MULTIPOLYGON;
                //!constGeom set to true if we have a generic geometry of planar dimension 2.
                boolean constGeom = 
                        (geometryType == Type.GEOMETRY  || geometryType == Type.GEOMETRYCOLLECTION)
                        && gdc != null 
                        && gdc.getDimension() == GeometryDimensionConstraint.DIMENSION_POLYGON;
                if (    //We must deal with a polygonal type.
                        nullT || (notPolyT && !constGeom)) {
                        throw new DriverException(
                                "Geometric field must be a (multi-)polygon !");
                }
                //And it must be a 3D type.
                if ((dc != null) && (dc.getDimension() == 2)) {
                        throw new DriverException("Only 3d can be stored in this format !");
                }
        }

        private void writeAMultiPolygon(final MultiPolygon multiPolygon,
                final long rowIndex) {
                final int nbOfCtrs = multiPolygon.getNumGeometries();
                out.printf("f%d %d\r\n", rowIndex + 1, nbOfCtrs);
                // the normal of the multi-polygon is set to the normal of its 1st
                // component (ie polygon)...
                writeANode(Geometry3DUtilities.computeNormal((Polygon) multiPolygon.getGeometryN(0)));
                for (int i = 0; i < nbOfCtrs; i++) {
                        writeAContour((Polygon) multiPolygon.getGeometryN(i));
                }
        }

        private void writeAPolygon(final Polygon polygon, final long rowIndex) {
                out.printf("f%d 1\r\n", rowIndex + 1);
                writeANode(Geometry3DUtilities.computeNormal(polygon));
                writeAContour(polygon);
        }

        private void writeAContour(final Polygon polygon) {
                final LineString shell = polygon.getExteriorRing();
                final int nbOfHoles = polygon.getNumInteriorRing();
                out.printf("c%d\r\n", nbOfHoles);
                writeALinearRing(shell);
                for (int i = 0; i < nbOfHoles; i++) {
                        out.printf("t\r\n");
                        writeALinearRing(polygon.getInteriorRingN(i));
                }
        }

        private void writeALinearRing(final LineString shell) {
                final Coordinate[] nodes = shell.getCoordinates();
                out.printf("%d\r\n", nodes.length);
                for (Coordinate node : nodes) {
                        writeANode(node);
                }
        }

        private void writeANode(final Coordinate node) {
                if (Double.isNaN(node.z)) {
                        out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, 0d);
                } else {
                        out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, node.z);
                }
        }

        @Override
        public boolean isCommitable() {
                return true;
        }
        
       @Override
        public int getSupportedType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{EXTENSION};
        }

        @Override
        public String getTypeDescription() {
                return "Solene file";
        }

        @Override
        public String getTypeName() {
                return "CIR";
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                final Value[] fields = rows.get((int) rowIndex);
                if ((fieldId < 0) || (fieldId > 1)) {
                        return ValueFactory.createNullValue();
                } else {
                        return fields[fieldId];
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return rows.size();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                if (dimension == X) {
                        return new Number[]{envelope.getMinX(), envelope.getMaxX()};
                } else if (dimension == Y) {
                        return new Number[]{envelope.getMinY(), envelope.getMaxY()};
                } else {
                        return null;
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }

        @Override
        public void setFile(File file) throws DriverException {
                this.file = file;
                schema = new DefaultSchema("Cir" + file.getAbsolutePath().hashCode());

                // building schema and metadata
                final SchemaMetadata metadata = new SchemaMetadata(schema);
                metadata.addField("id", Type.STRING, new Constraint[]{
                                new UniqueConstraint(),
                                new NotNullConstraint()});
                metadata.addField("the_geom", Type.POLYGON, new Constraint[]{
                                new Dimension3DConstraint(3)});
                schema.addTable("main", metadata);
                // finished building schema
        }

        @Override
        public boolean isOpen() {
                // once .open() is called, the content of rows
                // is always  accessible.
                return rows != null;
        }
}
