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
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver.shapefile;

import com.vividsolutions.jts.geom.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.*;
import org.gdms.data.types.*;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.*;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.geometryUtils.GeometryClean;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

public final class ShapefileDriver extends AbstractDataSet implements FileReadWriteDriver {

        public static final String DRIVER_NAME = "Shapefile driver";
        private static final GeometryFactory GF = new GeometryFactory();
        private Envelope envelope;
        private DBFDriver dbfDriver;
        private DataSet dataSet;
        private ShapefileReader reader;
        private IndexFile shxFile;
        private DataSourceFactory dataSourceFactory;
        private Schema schema;
        private DefaultMetadata metadata;
        private static final Logger LOG = Logger.getLogger(ShapefileDriver.class);
        private File file;
        private CoordinateReferenceSystem crs;

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
                        reader = new ShapefileReader(shpFis.getChannel());
                        File shx = FileUtils.getFileWithExtension(file, "shx");
                        if (shx == null || !shx.exists()) {
                                throw new DriverException("The file " + file.getAbsolutePath() + " has no corresponding .shx file");
                        }
                        FileInputStream shxFis = new FileInputStream(shx);
                        shxFile = new IndexFile(shxFis.getChannel());

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
                        dataSet = dbfDriver.getTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME);

                        Constraint dc;
                        //We can force the type of the data in the GDMS table according to the type
                        //given in the ShapeFile. This variable is here for this task.
                        int gtype;
                        // In case of a geometric type, the GeometryConstraint is mandatory
                        if (type.id == ShapeType.POINT.id) {
                                gtype = Type.POINT;
                                dc = new Dimension3DConstraint(2);
                        } else if (type.id == ShapeType.ARC.id) {
                                gtype = Type.MULTILINESTRING;
                                dc = new Dimension3DConstraint(2);
                        } else if (type.id == ShapeType.POLYGON.id) {
                                gtype = Type.MULTIPOLYGON;
                                dc = new Dimension3DConstraint(2);
                        } else if (type.id == ShapeType.MULTIPOINT.id) {
                                gtype = Type.MULTIPOINT;
                                dc = new Dimension3DConstraint(2);
                        } else if (type.id == ShapeType.POINTZ.id) {
                                gtype = Type.POINT;
                                dc = new Dimension3DConstraint(3);
                        } else if (type.id == ShapeType.ARCZ.id) {
                                gtype = Type.MULTILINESTRING;
                                dc = new Dimension3DConstraint(3);
                        } else if (type.id == ShapeType.POLYGONZ.id) {
                                gtype = Type.MULTIPOLYGON;
                                dc = new Dimension3DConstraint(3);
                        } else if (type.id == ShapeType.MULTIPOINTZ.id) {
                                gtype = Type.MULTIPOINT;
                                dc = new Dimension3DConstraint(3);
                        } else {
                                throw new DriverException("Unknown geometric type !");
                        }

                        Constraint[] constraints;

                        File prj = FileUtils.getFileWithExtension(file, "prj");
                        if (prj != null && prj.exists()) {
                                crs = DataSourceFactory.getCRSFactory().createFromPrj(prj);
                                if (crs != null) {
                                        CRSConstraint cc = new CRSConstraint(crs);
                                        constraints = new Constraint[]{dc, cc};
                                } else {
                                        constraints = new Constraint[]{dc};
                                }
                        } else {
                                constraints = new Constraint[]{dc};
                        }

                        metadata.clear();
                        metadata.addField(0, "the_geom", gtype, constraints);
                        metadata.addAll(dataSet.getMetadata());

                }  catch (IOException e) {
                        throw new DriverException(e);
                } catch (ShapefileException e) {
                        throw new DriverException(e);
                } catch (InvalidTypeException e) {
                        throw new DriverException(e);
                } catch (CRSException e) {
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
                result.add(new DefaultTypeDefinition("Point", Type.POINT, Constraint.DIMENSION_3D_GEOMETRY));
                result.add(new DefaultTypeDefinition("MultiPoint", Type.MULTIPOINT, Constraint.DIMENSION_3D_GEOMETRY));
                result.add(new DefaultTypeDefinition("MultiPolygon", Type.MULTIPOLYGON, Constraint.DIMENSION_3D_GEOMETRY));
                result.add(new DefaultTypeDefinition("MultiLineString", Type.MULTILINESTRING, Constraint.DIMENSION_3D_GEOMETRY));
                return result.toArray(new TypeDefinition[result.size()]);
        }

        @Override
        public void copy(File in, File out) throws IOException {
                File inDBF = FileUtils.getFileWithExtension(in, "dbf");
                File inSHX = FileUtils.getFileWithExtension(in, "shx");
                File outDBF = FileUtils.getFileWithExtension(out, "dbf");
                File outSHX = FileUtils.getFileWithExtension(out, "shx");
                org.apache.commons.io.FileUtils.copyFile(inDBF, outDBF);
                org.apache.commons.io.FileUtils.copyFile(inSHX, outSHX);
                org.apache.commons.io.FileUtils.copyFile(in, out);
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
                                        Polygon p = GeometryClean.makeGoodShapePolygon((Polygon) geom);
                                        retVal = GF.createMultiPolygon(new Polygon[]{p});
                                } else if (geom instanceof MultiPolygon) {
                                        retVal = GeometryClean.makeGoodShapeMultiPolygon((MultiPolygon) geom);
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
                        Type geomType = getGeometryType(metadata);
                        int dimension = getGeometryDimension(null, metadata);
                        int typeCode = geomType.getTypeCode();
                        if (typeCode == Type.NULL || (typeCode & Type.GEOMETRY) == 0) {
                                throw new DriverException("Shapefiles need a "
                                        + "specific geometry type");
                        }
                        ShapeType shapeType = getShapeType(geomType, dimension);
                        writer.writeHeaders(new Envelope(0, 0, 0, 0), shapeType, 0, 100);
                        writer.close();
                        writeprj(replaceExtension(new File(path), ".prj"), metadata);
                } catch (FileNotFoundException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }
        
        
     /**
     * Write a CRS as a WKT representation in a file
     *
     * @param path
     * @param crs
     * @throws DriverException
     */
    private void writeprj(File file, Metadata metadata) throws
            DriverException {
        int fieldIndex = MetadataUtilities.getSpatialFieldIndex(metadata);
        if(fieldIndex!=-1){
        Constraint[] c = metadata.getFieldType(fieldIndex).getConstraints(Constraint.CRS);
        if (c.length != 0) {
            crs = ((CRSConstraint) c[0]).getCRS();
        }
        if (crs != null) {
            String prj = crs.toWKT();
            try {
                org.apache.commons.io.FileUtils.write(file, prj);
            } catch (IOException ex) {
                throw new DriverException("Cannot write the prj", ex);
            }
        }
        }
    }
	  
	 
	

        /**
         * We try to retrieve the type of the geometry recorded in metadata.
         * @param metadata
         * @return
         * @throws DriverException 
         */
        private Type getGeometryType(Metadata metadata)
                throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (TypeFactory.isVectorial(metadata.getFieldType(i).getTypeCode())) {
                                return metadata.getFieldType(i);
                        }
                }

                throw new IllegalArgumentException("The data "
                        + "source doesn't contain any spatial field");
        }

        private int getGeometryDimension(DataSet dataSource, Metadata metadata)
                throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        //We search for the vectorial type.
                        if (TypeFactory.isVectorial(metadata.getFieldType(i).getTypeCode())) {
                                //Found it ! Let's check if there is a Dimension3DConstraint somewhere...
                                Dimension3DConstraint c = (Dimension3DConstraint) metadata.getFieldType(i).getConstraint(
                                        Constraint.DIMENSION_3D_GEOMETRY);
                                if (c == null) {
                                        //There is not. We search for the first not null geometry,
                                        //and consider its dimension to be the same as for all the 
                                        //other ones...
                                        if (dataSource != null) {
                                                for (int j = 0; j < dataSource.getRowCount(); j++) {
                                                        Geometry g = dataSource.getFieldValue(j, i).getAsGeometry();
                                                        if (g != null && g.getCoordinate() != null) {
                                                                if (Double.isNaN(g.getCoordinate().z)) {
                                                                        return 2;
                                                                } else {
                                                                        return 3;
                                                                }
                                                        }
                                                }
                                        }

                                        // ...and if there isn't any geometry in the DataSet,
                                        //we are in 2D.
                                        return 2;
                                } else {
                                        //There is a Dimension3DConstraint. Let's use it !
                                        return c.getDimension();
                                }
                        }
                }

                throw new IllegalArgumentException("The data "
                        + "source doesn't contain any spatial field");
        }

        /**
         * 
         * @param geometryType
         * @param dimension
         * @return
         * @throws DriverException 
         */
        private ShapeType getShapeType(Type geometryType, int dimension) throws DriverException {
                int typeCode = geometryType.getTypeCode();
                switch (typeCode) {
                        case Type.POINT:
                                if (dimension == 2) {
                                        return ShapeType.POINT;
                                } else {
                                        return ShapeType.POINTZ;
                                }
                        case Type.MULTIPOINT:
                                if (dimension == 2) {
                                        return ShapeType.MULTIPOINT;
                                } else {
                                        return ShapeType.MULTIPOINTZ;
                                }
                        case Type.LINESTRING:
                        case Type.MULTILINESTRING:
                                if (dimension == 2) {
                                        return ShapeType.ARC;
                                } else {
                                        return ShapeType.ARCZ;
                                }
                        case Type.POLYGON:
                        case Type.MULTIPOLYGON:
                                if (dimension == 2) {
                                        return ShapeType.POLYGON;
                                } else {
                                        return ShapeType.POLYGONZ;
                                }
                        case Type.GEOMETRY:
                        case Type.GEOMETRYCOLLECTION:
                                Constraint cons =
                                        geometryType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                if (cons == null) {
                                        throw new DriverException("Shapefiles need a specific geometry type");
                                } else {
                                        switch (Integer.valueOf(cons.getConstraintValue())) {
                                                case 0:
                                                        if (dimension == 2) {
                                                                return ShapeType.MULTIPOINT;
                                                        } else {
                                                                return ShapeType.MULTIPOINTZ;
                                                        }
                                                case 1:
                                                        if (dimension == 2) {
                                                                return ShapeType.ARC;
                                                        } else {
                                                                return ShapeType.ARCZ;
                                                        }
                                                case 2:
                                                        if (dimension == 2) {
                                                                return ShapeType.POLYGON;
                                                        } else {
                                                                return ShapeType.POLYGONZ;
                                                        }
                                                default:
                                                        throw new DriverException("Not a valid geometry constraint code.");
                                        }
                                }

                }
                //Geometry and GeometryCollection are not valid shape types.
                return null;

        }

        @Override
        public void writeFile(final File file, final DataSet dataSource,
                ProgressMonitor pm) throws DriverException {
                LOG.trace("Writing file " + file.getAbsolutePath());
                // write dbf
                DBFDriver tempDbfDriver = new DBFDriver();
                tempDbfDriver.setDataSourceFactory(dataSourceFactory);
                tempDbfDriver.writeFile(replaceExtension(file, ".dbf"), new DBFRowProvider(
                        dataSource), pm);

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
                        //What geometries are we about to process ?
                        Type geomType = getGeometryType(outMetadata);
                        //We have a look at the dimension. Are we in 3D ?
                        int dimension = getGeometryDimension(dataSource, outMetadata);
                        ShapeType shapeType;
                        //We shall analyse the type code.
                        int typeCode = geomType.getTypeCode();
                        //Are we dealing with a general vectorial type ?
                        boolean isGeneral = typeCode == Type.GEOMETRY || typeCode == Type.GEOMETRYCOLLECTION;
                        //Do we have a constraint upon dimension ?
                        boolean noDimCons = geomType.getConstraint(Constraint.DIMENSION_3D_GEOMETRY) == null;
                        if (isGeneral && typeCode != Type.NULL && noDimCons) {
                                //We're in a case that is too general for a shape... let's
                                //try to improve this as far as we can.
                                LOG.warn("No geometry type in the "
                                        + "metadata. Will take the type of the first geometry");
                                shapeType = getFirstShapeType(dataSource, dimension);
                                if (shapeType == null) {
                                        throw new IllegalArgumentException("A "
                                                + "geometry type have to be specified");
                                }
                        } else {
                                shapeType = getShapeType(geomType, dimension);
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
                        writeprj(replaceExtension(file, ".prj"), dataSource.getMetadata());
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

        private ShapeType getFirstShapeType(DataSet ds, int dimension) throws DriverException {
                final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(ds.getMetadata());
                for (int i = 0; i < ds.getRowCount(); i++) {
                        Value v = ds.getFieldValue(i, spatialFieldIndex);
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
        public int getSupportedType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
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
                        if (TypeFactory.isVectorial(typeCode) && typeCode != Type.NULL) {
                                //At this point, we're sure we have a geometry type that is not Type.NULL
                                if (spatialIndex != -1) {
                                        return "Cannot store sources with several geometries on a shapefile: "
                                                + m.getFieldName(spatialIndex)
                                                + " and "
                                                + m.getFieldName(i) + " found";
                                } else {
                                        if (typeCode == Type.GEOMETRY) {
                                                return "A generic geometry column is not allowed. It must be of a concrete geometry type "
                                                        + "other than LineString of Polygon.";
                                        } else if (typeCode == Type.LINESTRING) {
                                                return "Linestrings are not allowed. Use Multilinestrings instead";
                                        } else if (typeCode == Type.POLYGON) {
                                                return "Polygons are not allowed. Use Multipolygons instead";
                                        }
                                        Dimension3DConstraint dc = (Dimension3DConstraint) fieldType.getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                                        if (dc == null) {
                                                return "A geometry dimension has to be specified";
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

                return new DBFDriver().validateMetadata(dbfMeta);
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
                if (!name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
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
                                return (null == shape) ? null : ValueFactory.createValue(shape, crs);
                        } else {
                                return dataSet.getFieldValue(rowIndex, fieldId - 1);
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
                return schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
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
