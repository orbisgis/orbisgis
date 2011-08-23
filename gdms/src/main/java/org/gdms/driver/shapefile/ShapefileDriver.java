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
package org.gdms.driver.shapefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import fr.cts.crs.CoordinateReferenceSystem;
import org.apache.log4j.Logger;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.driverManager.DriverManager;
import org.orbisgis.wkt.parser.PRJUtils;
import org.orbisgis.wkt.parser.ParseException;

public final class ShapefileDriver implements FileReadWriteDriver, DataSet {

        public static final String DRIVER_NAME = "Shapefile driver";
        private static final GeometryFactory GF = new GeometryFactory();
        private Envelope envelope;
        private DBFDriver dbfDriver;
        private DataSet driver;
        private ShapefileReader reader;
        private IndexFile shxFile;
        private DataSourceFactory dataSourceFactory;
        private Schema schema;
        private DefaultMetadata metadata;
        private static final Logger LOG = Logger.getLogger(ShapefileDriver.class);
        private int srid = -1;
        private File file;

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                try {
                        if (reader != null) {
                                reader.close();
                        }
                        reader = null;
                        if (shxFile != null) {
                                shxFile.close();
                        }
                        shxFile = null;
                        if (dbfDriver != null) {
                                dbfDriver.close();
                        }
                        dbfDriver = null;
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                try {
                        FileInputStream shpFis = new FileInputStream(file);
                        WarningListener warningListener = dataSourceFactory.getWarningListener();
                        reader = new ShapefileReader(shpFis.getChannel(), warningListener);
                        FileInputStream shxFis = new FileInputStream(FileUtils.getFileWithExtension(file, "shx"));
                        shxFile = new IndexFile(shxFis.getChannel(), warningListener);

                        ShapefileHeader header = reader.getHeader();
                        envelope = new Envelope(
                                new Coordinate(header.minX(), header.minY()),
                                new Coordinate(header.maxX(), header.maxY()));

                        ShapeType type = header.getShapeType();

                        dbfDriver = new DBFDriver();
                        dbfDriver.setDataSourceFactory(dataSourceFactory);
                        File dbf = FileUtils.getFileWithExtension(file, "dbf");
                        if (dbf == null || !dbf.exists()) {
                                throw new DriverException("The file " + file.getAbsolutePath() + " has no corresponding .dbf file");
                        }
                        dbfDriver.setFile(dbf);
                        dbfDriver.open();

                        // registering DataSet
                        driver = dbfDriver.getTable("main");

                        Constraint dc;
                        Constraint gc;
                        // In case of a geometric type, the GeometryConstraint is mandatory
                        if (type.id == ShapeType.POINT.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POINT);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 2);
                        } else if (type.id == ShapeType.ARC.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_LINESTRING);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 2);
                        } else if (type.id == ShapeType.POLYGON.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_POLYGON);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 2);
                        } else if (type.id == ShapeType.MULTIPOINT.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_POINT);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 2);
                        } else if (type.id == ShapeType.POINTZ.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POINT);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3);
                        } else if (type.id == ShapeType.ARCZ.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_LINESTRING);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3);
                        } else if (type.id == ShapeType.POLYGONZ.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_POLYGON);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3);
                        } else if (type.id == ShapeType.MULTIPOINTZ.id) {
                                gc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.MULTI_POINT);
                                dc = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3);
                        } else {
                                throw new DriverException("Unknown geometric type !");
                        }

                        // Check prjFile File prjFile =
                        File prj = FileUtils.getFileWithExtension(file, "prj");

                        if (prj != null && prj.exists()) {
                                try {
                                        // we have a prj!!
                                        CoordinateReferenceSystem c = PRJUtils.getCRSFromPRJ(prj);
                                        if (c.getAuthority() != null) {
                                                // let's set the SRID of this source
                                                srid = c.getAuthority().getCode();
                                        }
                                } catch (ParseException ex) {
                                }

                        }

                        // Constraint crsConstraint = new CRSConstraint(crs);
                        Constraint[] constraints = new Constraint[]{gc, dc};
                        metadata.clear();
                        metadata.addField(0, "the_geom", Type.GEOMETRY, constraints);
                        metadata.addAll(driver.getMetadata());

                        reader.setSrid(srid);

                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (ShapefileException e) {
                        throw new DriverException(e);
                } catch (InvalidTypeException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dataSourceFactory = dsf;
        }

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                List<TypeDefinition> result = new LinkedList<TypeDefinition>(Arrays.asList(new DBFDriver().getTypesDefinitions()));
                result.add(new DefaultTypeDefinition("Geometry", Type.GEOMETRY,
                        new int[]{Constraint.GEOMETRY_TYPE,
                                Constraint.GEOMETRY_DIMENSION}));
                return result.toArray(new TypeDefinition[result.size()]);
        }

        @Override
        public void copy(File in, File out) throws IOException {
                File inDBF = FileUtils.getFileWithExtension(in, "dbf");
                File inSHX = FileUtils.getFileWithExtension(in, "shx");
                File outDBF = FileUtils.getFileWithExtension(out, "dbf");
                File outSHX = FileUtils.getFileWithExtension(out, "shx");
                FileUtils.copy(inDBF, outDBF);
                FileUtils.copy(inSHX, outSHX);
                FileUtils.copy(in, out);
        }

        private static Geometry convertGeometry(Geometry geom, ShapeType type)
                throws DriverException {

                Geometry retVal = null;

                if ((geom == null) || geom.isEmpty()) {
                        if ((geom instanceof Point) || (geom instanceof MultiPoint)) {
                                retVal = new GeometryFactory().createMultiPoint((Point[]) null);
                        } else if ((geom instanceof LineString)
                                || (geom instanceof MultiLineString)) {
                                retVal = new GeometryFactory().createMultiLineString((LineString[]) null);
                        } else if ((geom instanceof Polygon)
                                || (geom instanceof MultiPolygon)) {
                                retVal = new GeometryFactory().createMultiPolygon((Polygon[]) null);
                        } else {
                                retVal = new GeometryFactory().createMultiPoint((Point[]) null);
                        }
                } else {
                        if (type == ShapeType.NULL) {
                                return geom;
                        }

                        if ((type == ShapeType.POINT) || (type == ShapeType.POINTZ)) {
                                if ((geom instanceof Point)) {
                                        retVal = geom;
                                } else if (geom instanceof MultiPoint) {
                                        MultiPoint mp = (MultiPoint) geom;
                                        if (mp.getNumGeometries() == 1) {
                                                retVal = mp.getGeometryN(0);
                                        }
                                }
                        } else if ((type == ShapeType.MULTIPOINT)
                                || (type == ShapeType.MULTIPOINTZ)) {
                                if ((geom instanceof Point)) {
                                        retVal = GF.createMultiPoint(new Point[]{(Point) geom});
                                } else if (geom instanceof MultiPoint) {
                                        retVal = geom;
                                }
                        } else if ((type == ShapeType.POLYGON)
                                || (type == ShapeType.POLYGONZ)) {
                                if (geom instanceof Polygon) {
                                        Polygon p = JTSUtilities.makeGoodShapePolygon((Polygon) geom);
                                        retVal = GF.createMultiPolygon(new Polygon[]{p});
                                } else if (geom instanceof MultiPolygon) {
                                        retVal = JTSUtilities.makeGoodShapeMultiPolygon((MultiPolygon) geom);
                                }
                        } else if ((type == ShapeType.ARC) || (type == ShapeType.ARCZ)) {
                                if ((geom instanceof LineString)) {
                                        retVal = GF.createMultiLineString(new LineString[]{(LineString) geom});
                                } else if (geom instanceof MultiLineString) {
                                        retVal = geom;
                                }
                        }
                }
                if (retVal == null) {
                        throw new DriverException(
                                "Cannot mix geometry types in a shapefile. "
                                + "ShapeType: " + type.name + " -> Geometry: "
                                + geom.toText());
                }

                return retVal;
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creating source file");
                // write dbf
                String dbfFile = replaceExtension(new File(path), ".dbf").getAbsolutePath();
                DBFDriver tempDbfDriver = new DBFDriver();
                tempDbfDriver.setDataSourceFactory(dataSourceFactory);
                tempDbfDriver.createSource(dbfFile, new DBFMetadata(metadata),
                        dataSourceFactory);

                // write shapefile and shx
                try {
                        FileOutputStream shpFis = new FileOutputStream(new File(path));
                        final FileOutputStream shxFis = new FileOutputStream(
                                replaceExtension(new File(path), ".shx"));

                        ShapefileWriter writer = new ShapefileWriter(shpFis.getChannel(),
                                shxFis.getChannel());
                        GeometryConstraint gc = getGeometryType(metadata);
                        int dimension = getGeometryDimension(null, metadata);
                        if (gc == null) {
                                throw new DriverException("Shapefiles need a "
                                        + "specific geometry type");
                        }
                        ShapeType shapeType = getShapeType(gc.getGeometryType(), dimension);
                        writer.writeHeaders(new Envelope(0, 0, 0, 0), shapeType, 0, 100);
                        writer.close();

                        // writeprj(replaceExtension(new File(path), ".prj"), metadata);
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

//        private void writeprj(File path, Metadata metadata) throws
//                DriverException {
//
//
//
//        }
        private GeometryConstraint getGeometryType(Metadata metadata)
                throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
                                return (GeometryConstraint) metadata.getFieldType(i).getConstraint(Constraint.GEOMETRY_TYPE);
                        }
                }

                throw new IllegalArgumentException("The data "
                        + "source doesn't contain any spatial field");
        }

        private int getGeometryDimension(DataSet dataSource, Metadata metadata)
                throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
                                DimensionConstraint c = (DimensionConstraint) metadata.getFieldType(i).getConstraint(
                                        Constraint.GEOMETRY_DIMENSION);
                                if (c == null) {
                                        if (dataSource != null) {
                                                for (int j = 0; j < dataSource.getRowCount(); j++) {
                                                        Geometry g = dataSource.getFieldValue(j, i).getAsGeometry();
                                                        if (g != null) {
                                                                if (Double.isNaN(g.getCoordinate().z)) {
                                                                        return 2;
                                                                } else {
                                                                        return 3;
                                                                }
                                                        }
                                                }
                                        }

                                        // 2d by default
                                        return 2;
                                } else {
                                        return c.getDimension();
                                }
                        }
                }

                throw new IllegalArgumentException("The data "
                        + "source doesn't contain any spatial field");
        }

        private ShapeType getShapeType(int geometryType, int dimension)
                throws DriverException {
                switch (geometryType) {
                        case GeometryConstraint.POINT:
                                if (dimension == 2) {
                                        return ShapeType.POINT;
                                } else {
                                        return ShapeType.POINTZ;
                                }
                        case GeometryConstraint.MULTI_POINT:
                                if (dimension == 2) {
                                        return ShapeType.MULTIPOINT;
                                } else {
                                        return ShapeType.MULTIPOINTZ;
                                }
                        case GeometryConstraint.LINESTRING:
                        case GeometryConstraint.MULTI_LINESTRING:
                                if (dimension == 2) {
                                        return ShapeType.ARC;
                                } else {
                                        return ShapeType.ARCZ;
                                }
                        case GeometryConstraint.POLYGON:
                        case GeometryConstraint.MULTI_POLYGON:
                                if (dimension == 2) {
                                        return ShapeType.POLYGON;
                                } else {
                                        return ShapeType.POLYGONZ;
                                }
                }

                return null;

        }

        @Override
        public void writeFile(final File file, final DataSet dataSource,
                ProgressMonitor pm) throws DriverException {
                LOG.trace("Writing file " + file.getAbsolutePath());
                WarningListener warningListener = dataSourceFactory.getWarningListener();
                // write dbf
                DBFDriver tempDbfDriver = new DBFDriver();
                tempDbfDriver.setDataSourceFactory(dataSourceFactory);
                tempDbfDriver.writeFile(replaceExtension(file, ".dbf"), new DBFRowProvider(
                        dataSource), warningListener, pm);

                // write shapefile and shx
                try {
                        final long rowCount = dataSource.getRowCount();
                        pm.startTask("Writing geometries", rowCount);
                        FileOutputStream shpFis = new FileOutputStream(file);
                        final FileOutputStream shxFis = new FileOutputStream(
                                replaceExtension(file, ".shx"));

                        ShapefileWriter writer = new ShapefileWriter(shpFis.getChannel(),
                                shxFis.getChannel());
                        Envelope fullExtent = DriverUtilities.getFullExtent(dataSource);
                        Metadata outMetadata = dataSource.getMetadata();
                        GeometryConstraint gc = getGeometryType(outMetadata);
                        int dimension = getGeometryDimension(dataSource, outMetadata);
                        ShapeType shapeType;
                        if (gc == null) {
                                warningListener.throwWarning("No geometry type in the "
                                        + "metadata. Will take the type of the first geometry");
                                shapeType = getFirstShapeType(dataSource, dimension);
                                if (shapeType == null) {
                                        throw new IllegalArgumentException("A "
                                                + "geometry type have to be specified");
                                }
                        } else {
                                shapeType = getShapeType(gc.getGeometryType(), dimension);
                        }
                        int fileLength = computeSize(dataSource, shapeType);
                        writer.writeHeaders(fullExtent, shapeType, (int) rowCount,
                                fileLength);
                        final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata());
                        for (int i = 0; i < rowCount; i++) {
                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }

                                Value v = dataSource.getFieldValue(i, spatialFieldIndex);

                                if (!v.isNull()) {
                                        writer.writeGeometry(convertGeometry(v.getAsGeometry(), shapeType));
                                } else {
                                        writer.writeGeometry(null);
                                }
                        }
                        pm.progressTo(rowCount);
                        writer.close();
//                        writeprj(replaceExtension(file, ".prj"), metadata);
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (ShapefileException e) {
                        throw new DriverException(e);
                }
                pm.endTask();
        }

        private File replaceExtension(File file, String suffix) {
                String prefix = file.getAbsolutePath();
                prefix = prefix.substring(0, prefix.lastIndexOf('.'));
                return new File(prefix + suffix);
        }

        private ShapeType getFirstShapeType(DataSet sds,
                int dimension) throws DriverException {
                final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(sds.getMetadata());
                for (int i = 0; i < sds.getRowCount(); i++) {
                        Value v = sds.getFieldValue(i, spatialFieldIndex);
                        if (!v.isNull()) {
                                return getShapeType(v.getAsGeometry(), dimension);
                        }
                }

                return null;
        }

        private ShapeType getShapeType(Geometry geom, int dimension) {
                if (geom instanceof Point) {
                        if (dimension == 2) {
                                return ShapeType.POINT;
                        } else {
                                return ShapeType.POINTZ;
                        }
                } else if ((geom instanceof LineString)
                        || (geom instanceof MultiLineString)) {
                        if (dimension == 2) {
                                return ShapeType.ARC;
                        } else {
                                return ShapeType.ARCZ;
                        }
                } else if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
                        if (dimension == 2) {
                                return ShapeType.POLYGON;
                        } else {
                                return ShapeType.POLYGONZ;
                        }
                } else if (geom instanceof MultiPoint) {
                        if (Double.isNaN(geom.getCoordinate().z)) {
                                return ShapeType.MULTIPOINT;
                        } else {
                                return ShapeType.MULTIPOINTZ;
                        }
                } else {
                        throw new IllegalArgumentException("Unrecognized geometry type : "
                                + geom.getClass());
                }
        }

        private int computeSize(DataSet dataSource,
                ShapeType type) throws DriverException, ShapefileException {
                final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata());
                int fileLength = 100;
                for (int i = (int) (dataSource.getRowCount() - 1); i >= 0; i--) {
                        Value v = dataSource.getFieldValue(i, spatialFieldIndex);
                        if (!v.isNull()) {
                                // shape length + record (2 ints)
                                int size = type.getShapeHandler().getLength(
                                        convertGeometry(v.getAsGeometry(), type)) + 8;
                                fileLength += size;
                        } else {
                                // null byte + record (2 ints)
                                fileLength += 4 + 8;
                        }
                }

                return fileLength;
        }

        @Override
        public boolean isCommitable() {
                return true;
        }

        @Override
        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public String validateMetadata(Metadata m) throws DriverException {
                int spatialIndex = -1;
                DefaultMetadata dbfMeta = new DefaultMetadata();
                for (int i = 0; i < m.getFieldCount(); i++) {
                        Type fieldType = m.getFieldType(i);
                        int typeCode = fieldType.getTypeCode();
                        if (typeCode == Type.GEOMETRY) {
                                if (spatialIndex != -1) {
                                        return "Cannot store sources with several geometries on a shapefile: "
                                                + m.getFieldName(spatialIndex)
                                                + " and "
                                                + m.getFieldName(i) + " found";
                                } else {
                                        GeometryConstraint gc = (GeometryConstraint) fieldType.getConstraint(Constraint.GEOMETRY_TYPE);
                                        if (gc == null) {
                                                return "A geometry type have to be specified";
                                        } else if (gc.getGeometryType() == GeometryConstraint.LINESTRING) {
                                                return "Linestrings are not allowed. Use Multilinestrings instead";
                                        } else if (gc.getGeometryType() == GeometryConstraint.POLYGON) {
                                                return "Polygons are not allowed. Use Multipolygons instead";
                                        }
                                        DimensionConstraint dc = (DimensionConstraint) fieldType.getConstraint(Constraint.GEOMETRY_DIMENSION);
                                        if (dc == null) {
                                                return "A geometry dimension have to be specified";
                                        }
                                        spatialIndex = i;
                                }
                        } else {
                                dbfMeta.addField(m.getFieldName(i), fieldType);
                        }
                }

                if (spatialIndex == -1) {
                        return "Missing spatial field";
                }

                String dbfError = new DBFDriver().validateMetadata(dbfMeta);
                if (dbfError != null) {
                        return dbfError;
                } else {
                        return null;
                }
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"shp"};
        }

        @Override
        public String getTypeDescription() {
                return "Esri shapefile";
        }

        @Override
        public String getTypeName() {
                return "SHP";
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
                try {
                        if (fieldId == 0) {
                                int offset = shxFile.getOffset((int) rowIndex);
                                Geometry shape = reader.geomAt(offset);
                                return (null == shape) ? null : ValueFactory.createValue(shape);
                        } else {
                                return driver.getFieldValue(rowIndex, fieldId - 1);
                        }
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return shxFile.getRecordCount();
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
                schema = new DefaultSchema("SHP" + file.getAbsolutePath().hashCode());
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public boolean isOpen() {
                return reader != null;
        }
}
