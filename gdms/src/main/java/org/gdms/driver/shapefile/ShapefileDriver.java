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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.WarningListener;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;
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
import org.gdms.data.types.SRIDConstraint;
import org.orbisgis.wkt.parser.PRJUtils;
import org.orbisgis.wkt.parser.ParseException;

public class ShapefileDriver implements FileReadWriteDriver {

        public static final String DRIVER_NAME = "Shapefile driver";
        private static GeometryFactory gf = new GeometryFactory();
        private File fileShp;
        private Envelope envelope;
        private ShapeType type;
        private DBFDriver dbfDriver;
        private ShapefileReader reader;
        private IndexFile shxFile;
        private DataSourceFactory dataSourceFactory;
        private int srid = -1;

        public void close() throws DriverException {
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

        public void open(File f) throws DriverException {
                try {
                        FileInputStream shpFis = new FileInputStream(f);
                        WarningListener warningListener = dataSourceFactory.getWarningListener();
                        reader = new ShapefileReader(shpFis.getChannel(), warningListener);
                        FileInputStream shxFis = new FileInputStream(FileUtils.getFileWithExtension(f, "shx"));
                        shxFile = new IndexFile(shxFis.getChannel(), warningListener);
                        fileShp = f;

                        ShapefileHeader header = reader.getHeader();
                        envelope = new Envelope(
                                new Coordinate(header.minX(), header.minY()),
                                new Coordinate(header.maxX(), header.maxY()));

                        type = header.getShapeType();

                        dbfDriver = new DBFDriver();
                        dbfDriver.setDataSourceFactory(dataSourceFactory);
                        dbfDriver.open(FileUtils.getFileWithExtension(fileShp, "dbf"));


                        // Check prjFile File prjFile =
                        File prj = FileUtils.getFileWithExtension(fileShp, "prj");

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
                        reader.setSrid(srid);

                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (ShapefileException e) {
                        throw new DriverException(e);
                }
        }

        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                try {
                        if (fieldId == 0) {
                                int offset = shxFile.getOffset((int) rowIndex);
                                Geometry shape = reader.geomAt(offset);
                                return (null == shape) ? null : ValueFactory.createValue(shape);
                        } else {
                                return dbfDriver.getFieldValue(rowIndex, fieldId - 1);
                        }
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        public Metadata getMetadata() throws DriverException {
                DefaultMetadata metadata = new DefaultMetadata(dbfDriver.getMetadata());
                try {
                        DimensionConstraint dc;
                        GeometryConstraint gc;
                        // In case of a geometric type, the GeometryConstraint is mandatory
                        if (type.id == ShapeType.POINT.id) {
                                gc = new GeometryConstraint(GeometryConstraint.POINT);
                                dc = new DimensionConstraint(2);
                        } else if (type.id == ShapeType.ARC.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_LINESTRING);
                                dc = new DimensionConstraint(2);
                        } else if (type.id == ShapeType.POLYGON.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_POLYGON);
                                dc = new DimensionConstraint(2);
                        } else if (type.id == ShapeType.MULTIPOINT.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_POINT);
                                dc = new DimensionConstraint(2);
                        } else if (type.id == ShapeType.POINTZ.id) {
                                gc = new GeometryConstraint(GeometryConstraint.POINT);
                                dc = new DimensionConstraint(3);
                        } else if (type.id == ShapeType.ARCZ.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_LINESTRING);
                                dc = new DimensionConstraint(3);
                        } else if (type.id == ShapeType.POLYGONZ.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_POLYGON);
                                dc = new DimensionConstraint(3);
                        } else if (type.id == ShapeType.MULTIPOINTZ.id) {
                                gc = new GeometryConstraint(GeometryConstraint.MULTI_POINT);
                                dc = new DimensionConstraint(3);
                        } else {
                                throw new DriverException("Unknown geometric type !");
                        }

                        // if there is a SRID present, we add the corresponding constraint
                        Constraint[] constraints;
                        if (srid != -1) {
                                constraints = new Constraint[]{gc, dc, new SRIDConstraint(srid)};
                        } else {
                                constraints = new Constraint[]{gc, dc};
                        }

                        metadata.addField(0, "the_geom", Type.GEOMETRY, constraints);

                } catch (InvalidTypeException e) {
                        throw new RuntimeException("Bug in the driver", e);
                }
                return metadata;
        }

        public long getRowCount() throws DriverException {
                return shxFile.getRecordCount();
        }

        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dataSourceFactory = dsf;
        }

        public String getDriverId() {
                return DRIVER_NAME;
        }

        public Number[] getScope(int dimension) throws DriverException {
                if (dimension == X) {
                        return new Number[]{envelope.getMinX(), envelope.getMaxX()};
                } else if (dimension == Y) {
                        return new Number[]{envelope.getMinY(), envelope.getMaxY()};
                } else {
                        return null;
                }
        }

        public TypeDefinition[] getTypesDefinitions() {
                List<TypeDefinition> result = new LinkedList<TypeDefinition>(Arrays.asList(new DBFDriver().getTypesDefinitions()));
                result.add(new DefaultTypeDefinition("Geometry", Type.GEOMETRY,
                        new int[]{Constraint.GEOMETRY_TYPE,
                                Constraint.GEOMETRY_DIMENSION}));
                return result.toArray(new TypeDefinition[result.size()]);
        }

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
                                        retVal = gf.createMultiPoint(new Point[]{(Point) geom});
                                } else if (geom instanceof MultiPoint) {
                                        retVal = geom;
                                }
                        } else if ((type == ShapeType.POLYGON)
                                || (type == ShapeType.POLYGONZ)) {
                                if (geom instanceof Polygon) {
                                        Polygon p = JTSUtilities.makeGoodShapePolygon((Polygon) geom);
                                        retVal = gf.createMultiPolygon(new Polygon[]{p});
                                } else if (geom instanceof MultiPolygon) {
                                        retVal = JTSUtilities.makeGoodShapeMultiPolygon((MultiPolygon) geom);
                                }
                        } else if ((type == ShapeType.ARC) || (type == ShapeType.ARCZ)) {
                                if ((geom instanceof LineString)) {
                                        retVal = gf.createMultiLineString(new LineString[]{(LineString) geom});
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

        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                // write dbf
                String dbfFile = replaceExtension(new File(path), ".dbf").getAbsolutePath();
                DBFDriver dbfDriver = new DBFDriver();
                dbfDriver.setDataSourceFactory(dataSourceFactory);
                dbfDriver.createSource(dbfFile, new DBFMetadata(metadata),
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
                                GeometryConstraint gc = (GeometryConstraint) metadata.getFieldType(i).getConstraint(Constraint.GEOMETRY_TYPE);
                                return gc;
                        }
                }

                throw new IllegalArgumentException("The data "
                        + "source doesn't contain any spatial field");
        }

        private int getGeometryDimension(DataSource dataSource, Metadata metadata)
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

        public void writeFile(final File file, final DataSource dataSource,
                IProgressMonitor pm) throws DriverException {
                WarningListener warningListener = dataSourceFactory.getWarningListener();
                // write dbf
                DBFDriver dbfDriver = new DBFDriver();
                dbfDriver.setDataSourceFactory(dataSourceFactory);
                dbfDriver.writeFile(replaceExtension(file, ".dbf"), new DBFRowProvider(
                        dataSource), warningListener, pm);

                // write shapefile and shx
                try {
                        SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
                                dataSource);
                        FileOutputStream shpFis = new FileOutputStream(file);
                        final FileOutputStream shxFis = new FileOutputStream(
                                replaceExtension(file, ".shx"));

                        ShapefileWriter writer = new ShapefileWriter(shpFis.getChannel(),
                                shxFis.getChannel());
                        Envelope fullExtent = sds.getFullExtent();
                        Metadata metadata = dataSource.getMetadata();
                        GeometryConstraint gc = getGeometryType(metadata);
                        int dimension = getGeometryDimension(dataSource, metadata);
                        ShapeType shapeType;
                        if (gc == null) {
                                warningListener.throwWarning("No geometry type in the "
                                        + "metadata. Will take the type of the first geometry");
                                shapeType = getFirstShapeType(sds, dimension);
                                if (shapeType == null) {
                                        throw new IllegalArgumentException("A "
                                                + "geometry type have to be specified");
                                }
                        } else {
                                shapeType = getShapeType(gc.getGeometryType(), dimension);
                        }
                        int fileLength = computeSize(sds, shapeType);
                        writer.writeHeaders(fullExtent, shapeType, (int) sds.getRowCount(),
                                fileLength);
                        for (int i = 0; i < sds.getRowCount(); i++) {
                                if (i / 100 == i / 100.0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo((int) (100 * i / sds.getRowCount()));
                                        }
                                }

                                Geometry geometry = sds.getGeometry(i);
                                if (geometry != null) {
                                        writer.writeGeometry(convertGeometry(geometry, shapeType));
                                } else {
                                        writer.writeGeometry(null);
                                }
                        }
                        writer.close();
//                        writeprj(replaceExtension(file, ".prj"), metadata);
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (ShapefileException e) {
                        throw new DriverException(e);
                }
        }

        private File replaceExtension(File file, String suffix) {
                String prefix = file.getAbsolutePath();
                prefix = prefix.substring(0, prefix.lastIndexOf('.'));
                return new File(prefix + suffix);
        }

        private ShapeType getFirstShapeType(SpatialDataSourceDecorator sds,
                int dimension) throws DriverException {
                for (int i = 0; i < sds.getRowCount(); i++) {
                        Geometry geom = sds.getGeometry(i);
                        if (geom != null) {
                                return getShapeType(geom, dimension);
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
                        if (geom.getCoordinate().z == Double.NaN) {
                                return ShapeType.MULTIPOINT;
                        } else {
                                return ShapeType.MULTIPOINTZ;
                        }
                } else {
                        throw new IllegalArgumentException("Unrecognized geometry type : "
                                + geom.getClass());
                }
        }

        private int computeSize(SpatialDataSourceDecorator dataSource,
                ShapeType type) throws DriverException, ShapefileException {
                int fileLength = 100;
                for (int i = (int) (dataSource.getRowCount() - 1); i >= 0; i--) {
                        Geometry geometry = dataSource.getGeometry(i);
                        if (geometry != null) {
                                // shape length + record (2 ints)
                                int size = type.getShapeHandler().getLength(
                                        convertGeometry(geometry, type)) + 8;
                                fileLength += size;
                        } else {
                                // null byte + record (2 ints)
                                fileLength += 4 + 8;
                        }
                }

                return fileLength;
        }

        public boolean isCommitable() {
                return true;
        }

        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

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
        public boolean isOpen() {
                return reader != null;
        }
}
