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
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.driver.vrml;

import com.vividsolutions.jts.awt.PointShapeFactory.X;
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
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
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
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.log4j.Logger;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.schema.SchemaMetadata;
import org.gdms.driver.AbstractDataSet;

/**
 * @author Thomas LEDUC
 * 
 */
public final class VrmlDriver extends AbstractDataSet implements FileReadWriteDriver {

        private Scanner in;
        private PrintWriter out;
        private List<Value[]> rows;
        private Envelope envelope;
        private Schema schema;
        private File file;
        private static final String EOL = "\r\n";
        private static final String VRML_LINE_FMT = "Shape {" + EOL
                + "\tappearance Appearance {" + EOL + "\t\tmaterial Material {"
                + EOL + "\t\t\tdiffuseColor 1 1 0" + EOL + "\t\t}" + EOL + "\t}"
                + EOL + "\tgeometry IndexedLineSet {" + EOL
                + "\t\tcoord Coordinate {" + EOL + "\t\t\tpoint [" + EOL + "%s"
                + "\t\t\t]" + EOL + "\t\t}" + EOL + "\t\tcoordIndex [" + EOL + "%s"
                + EOL + "\t\t]" + EOL + "\t}" + EOL + "}" + EOL + EOL;
        private static final String VRML_FACE_FMT = "Shape {" + EOL
                + "\tappearance Appearance {" + EOL + "\t\tmaterial Material {"
                + EOL + "\t\t\tdiffuseColor 1 1 0" + EOL + "\t\t}" + EOL + "\t}"
                + EOL + "\tgeometry IndexedFaceSet {" + EOL
                + "\t\tcoord Coordinate {" + EOL + "\t\t\tpoint [" + EOL + "%s"
                + "\t\t\t]" + EOL + "\t\t}" + EOL + "\t\tcoordIndex [" + EOL
                + "\t\t\t%s" + EOL + "\t\t]" + EOL + "\t}" + EOL + "}" + EOL + EOL;
        private static final Logger LOG = Logger.getLogger(VrmlDriver.class);

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                in.close();
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                try {
                        rows = new ArrayList<Value[]>();
                        in = new Scanner(file);
                        in.useLocale(Locale.US); // essential to read float values
                        // TODO needs to be written
                        // 12/09/2010 - why??

                        // building schema
                        schema = new DefaultSchema("Vrml" + file.getAbsolutePath().hashCode());
                        schema.addTable("main", new SchemaMetadata(schema, new Type[]{
                                        TypeFactory.createType(Type.INT),
                                        TypeFactory.createType(Type.GEOMETRY)}, new String[]{"gid",
                                        "the_geom"}));
                        // finished building schema

                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                final TypeDefinition[] result = new TypeDefinition[2];
                result[0] = new DefaultTypeDefinition("STRING", Type.STRING);
                result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY);
                return result;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "VRML driver";
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creting source file at " + path);
                try {
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
                LOG.trace("Writing file at " + file.getAbsolutePath());

                try {
                        final long rowCount = dataSource.getRowCount();
                        pm.startTask("Writing file", rowCount);

                        out = new PrintWriter(new FileOutputStream(file));

                        // write header part...
                        out.printf("#VRML V2.0 utf8%s", EOL);

                        // write body part...

                        final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata());

                        for (long i = 0; i < rowCount; i++) {
                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }

                                final Geometry g = dataSource.getFieldValue(i, spatialFieldIndex).getAsGeometry();
                                write(g);
                        }
                        pm.progressTo(rowCount);
                        out.close();
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                }
                pm.endTask();
        }

        private void write(final Geometry geometry) {
                LOG.trace("Writing geometry");
                if (geometry instanceof GeometryCollection) {
                        final GeometryCollection gc = (GeometryCollection) geometry;
                        final int nbOfGeometries = geometry.getNumGeometries();
                        for (int i = 0; i < nbOfGeometries; i++) {
                                write(gc.getGeometryN(i));
                        }
                } else if (geometry instanceof LineString) {
                        write((LineString) geometry);
                } else if (geometry instanceof Polygon) {
                        write((Polygon) geometry);
                } else {
                        throw new IllegalArgumentException("Unrecognized geometry type.");
                }
        }

        private void write(final LineString lineString) {
                LOG.trace("Writing lineString");
                final StringBuffer sbCoords = new StringBuffer();
                final StringBuffer sbIdx = new StringBuffer();
                final Coordinate[] coordinates = lineString.getCoordinates();
                for (int i = 0; i < coordinates.length; i++) {
                        sbCoords.append("\t\t\t\t").append(coordinates[i].x).append(" ").append(coordinates[i].y).append(" ").append(
                                coordinates[i].z).append(",").append(EOL);
                        sbIdx.append(i).append(", ");
                }
                out.printf(VRML_LINE_FMT, sbCoords.toString(), sbIdx.toString());
        }

        private void write(final Polygon polygon) {
                LOG.trace("Writing polygon");
                final StringBuffer sbCoords = new StringBuffer();
                final StringBuffer sbIdx = new StringBuffer();
                final Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();
                for (int i = 0; i < coordinates.length; i++) {
                        sbCoords.append("\t\t\t\t").append(coordinates[i].x).append(" ").append(coordinates[i].y).append(" ").append(
                                coordinates[i].z).append(",").append(EOL);
                        sbIdx.append(i).append(", ");
                }
                out.printf(VRML_FACE_FMT, sbCoords.toString(), sbIdx.toString());
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
                throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"wrl"};
        }

        @Override
        public String getTypeDescription() {
                return "VRML file";
        }

        @Override
        public String getTypeName() {
                return "VRML";
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                return this;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                LOG.trace("Getting field at " + rowIndex);
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
        public void setFile(File file) {
                this.file = file;
        }

        @Override
        public boolean isOpen() {
                // once .open() is called, the content of rows
                // is always  accessible.
                return rows != null;
        }
}
