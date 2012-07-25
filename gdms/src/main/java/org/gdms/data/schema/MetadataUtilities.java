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
package org.gdms.data.schema;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * An utility class to help the exploration of Metadata instances
 */
public final class MetadataUtilities {

        public static final int ANYGEOMETRY = Type.POINT | Type.MULTIPOINT | Type.LINESTRING | Type.MULTILINESTRING
                | Type.POLYGON | Type.MULTIPOLYGON | Type.GEOMETRYCOLLECTION | Type.GEOMETRY;

        /**
         * Gets the field names in the metadata instance that have the primary key
         * constraint
         *
         * @param metadata
         * @return
         * @throws DriverException if raised when reading metadata
         */
        public static String[] getPKNames(final Metadata metadata)
                throws DriverException {
                final int[] pKIndices = getPKIndices(metadata);
                final String[] pKNames = new String[pKIndices.length];

                for (int i = 0; i < pKNames.length; i++) {
                        pKNames[i] = metadata.getFieldName(pKIndices[i]);
                }

                return pKNames;
        }

        /**
         * Gets the indexes of the fields in the metadata instance that have the
         * primary constraint
         *
         * @param metadata
         * @return
         * @throws DriverException if raised when reading metadata
         */
        public static int[] getPKIndices(final Metadata metadata)
                throws DriverException {
                final int fc = metadata.getFieldCount();
                final List<Integer> tmpPKIndices = new ArrayList<Integer>();

                for (int i = 0; i < fc; i++) {
                        final Type type = metadata.getFieldType(i);
                        final Constraint[] constraints = type.getConstraints();
                        for (Constraint c : constraints) {
                                if (Constraint.PK == c.getConstraintCode()) {
                                        tmpPKIndices.add(i);
                                        break;
                                }
                        }
                }
                final int[] pkIndices = new int[tmpPKIndices.size()];
                for (int i = 0; i < pkIndices.length; i++) {
                        pkIndices[i] = tmpPKIndices.get(i);
                }
                
                return pkIndices;
        }

        /**
         * Returns true if the field at the specified index is read only
         *
         * @param metadata
         * @param fieldId
         * @return
         * @throws DriverException if raised when reading metadata
         */
        public static boolean isReadOnly(final Metadata metadata, final int fieldId)
                throws DriverException {
                final Constraint[] constraints = metadata.getFieldType(fieldId).getConstraints(Constraint.READONLY);
                return constraints.length != 0;
        }

        /**
         * Returns true if the field at the specified index is primary key
         *
         * @param metadata
         * @param fieldId
         * @return
         * @throws DriverException if raised when reading metadata
         */
        public static boolean isPrimaryKey(final Metadata metadata,
                final int fieldId) throws DriverException {
                return isPrimaryKey(metadata.getFieldType(fieldId));
        }

        /**
         * Returns true if the type has a primary key constraint
         *
         * @param fieldType
         * @return
         */
        public static boolean isPrimaryKey(final Type fieldType) {
                final Constraint[] constraints = fieldType.getConstraints(Constraint.PK);
                return constraints.length != 0;
        }

        /**
         * checks that the specified value fits all the constraints of the field at
         * the specified index in the specified Metadata instance
         *
         * @param metadata
         * @param fieldId
         * @param value
         * @return
         * @throws DriverException if raised when reading metadata
         */
        public static String check(final Metadata metadata, final int fieldId,
                Value value) throws DriverException {
                final Constraint[] constraints = metadata.getFieldType(fieldId).getConstraints();
                for (Constraint c : constraints) {
                        if (null != c.check(value)) {
                                return c.check(value);
                        }
                }
                return null;
        }

        /**
         * Checks that the input metadata is compatible with the output metadata.
         *
         * Compatible in this context means:
         * - same number of fields
         * - field types can be implicitly cast from one to the other
         *
         * @param input input metadata
         * @param target output metadata
         * @return null if there is no problem, else an error message
         * @throws DriverException
         */
        public static String check(Metadata input, Metadata target) throws DriverException {
                // check same field count
                if (input.getFieldCount() != target.getFieldCount()) {
                        return "The number of fields is different! Input: " + input.getFieldCount() + ", output: "
                                + target.getFieldCount();
                }

                for (int i = 0; i < input.getFieldCount(); i++) {
                        int it = input.getFieldType(i).getTypeCode();
                        int tt = target.getFieldType(i).getTypeCode();
                        if (!TypeFactory.canBeCastTo(it, tt)) {
                                return "Incompatible types for field " + target.getFieldName(i) + "! Input: "
                                        + TypeFactory.getTypeName(it) + ", ouput: "
                                        + TypeFactory.getTypeName(tt);
                        }

                }

                return null;
        }

        /**
         * Gets an array with the field types
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static Type[] getFieldTypes(Metadata metadata)
                throws DriverException {
                Type[] fieldTypes = new Type[metadata.getFieldCount()];
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        fieldTypes[i] = metadata.getFieldType(i);
                }
                return fieldTypes;
        }

        /**
         * True if the field is writable
         *
         * @param fieldType
         * @return
         */
        public static boolean isWritable(Type fieldType) {
                return (fieldType.getConstraint(Constraint.READONLY) == null)
                        && (fieldType.getConstraint(Constraint.AUTO_INCREMENT) == null);
        }

        /**
         * Returns true if there is some geometry type in the metadata
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static boolean isGeometry(Metadata metadata) throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if ((metadata.getFieldType(i).getTypeCode() & Type.GEOMETRY) != 0) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Returns true if the metadata corresponds to the one of a raster source
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static boolean isRaster(Metadata metadata) throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (metadata.getFieldType(i).getTypeCode() == Type.RASTER) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Returns the Metadata objects for the specified DataSource objects
         *
         * @param tables
         * @return
         * @throws DriverException
         */
        public static Metadata[] fromTablesToMetadatas(final DataSource[] tables)
                throws DriverException {
                final Metadata[] metadatas = new Metadata[tables.length];
                for (int i = 0; i < tables.length; i++) {
                        metadatas[i] = tables[i].getMetadata();
                }
                return metadatas;
        }

        /**
         * Gets the dimension of the Geometry at the specified fieldIndex
         *
         * @param metadata the Metadata to search
         * @param spatialField the fieldIndex to look for
         * @return an int dimension 0 = POINT, 1 = CURVE, 2 = SURFACE and -1 = UNKNOWN
         * @throws DriverException
         */
        public static int getGeometryDimension(Metadata metadata, int spatialField) throws DriverException {
                Type fieldType = metadata.getFieldType(spatialField);
                return getGeometryTypeDimension(fieldType);
        }

        /**
         * Gets the dimension of the Geometry at the specified fieldIndex
         *
         * @param metadata the Metadata to search
         * @param spatialField the fieldIndex to look for
         * @return a human dimension name POINT, CURVE, SURFACE or UNKNOWN
         * @throws DriverException
         */
        public static String getHumanGeometryDimension(Metadata metadata, int spatialField) throws DriverException {
                Type fieldType = metadata.getFieldType(spatialField);
                return getHumanGeometryTypeDimension(fieldType);
        }

        /**
         * Returns the spatial field index.
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static int getSpatialFieldIndex(Metadata metadata)
                throws DriverException {
                int spatialFieldIndex = -1;
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        int typeCode = metadata.getFieldType(i).getTypeCode();
                        if ((TypeFactory.isVectorial(typeCode) && typeCode != Type.NULL)
                                || (typeCode == Type.RASTER) || (typeCode == Type.STREAM)) {
                                spatialFieldIndex = i;
                                break;
                        }
                }
                return spatialFieldIndex;


        }

        /**
         * Returns the geometry field index.
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static int getGeometryFieldIndex(Metadata metadata)
                throws DriverException {
                int spatialFieldIndex = -1;
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        int typeCode = metadata.getFieldType(i).getTypeCode();
                        if (((typeCode & ANYGEOMETRY) != 0)) {
                                spatialFieldIndex = i;
                                break;
                        }
                }
                return spatialFieldIndex;


        }

        /**
         * Returns the raster field index.
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static int getRasterFieldIndex(Metadata metadata)
                throws DriverException {
                int spatialFieldIndex = -1;
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        int typeCode = metadata.getFieldType(i).getTypeCode();
                        if ((typeCode == Type.RASTER)) {
                                spatialFieldIndex = i;
                                break;
                        }
                }
                return spatialFieldIndex;
        }

        /**
         * Returns true if there is a spatial field in the specified Metadata object.
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static boolean isSpatial(Metadata metadata) throws DriverException {
                return getSpatialFieldIndex(metadata) != -1;


        }

        /**
         * Gets the (planar) dimension of the geometry type given in argument. Note
         * that if you give an invalid (not vectorial) type as an input, this method will
         * throw a InvalidArgumentException
         *
         * @param type
         * @return
         * <ul><li>{@code GeometryDimensionConstraint.DIMENSION_POINT} : if we have a point or a collection of points</li>
         * <li>{@code GeometryDimensionConstraint.DIMENSION_CURVE} : if we have a line or a collection of lines</li>
         * <li>{@code GeometryDimensionConstraint.DIMENSION_SURFACE} : if we have a polygon or collection of polygons</li>
         * <li>DIMENSION_UNKNOWN otherwise (if the type is too generic)</li></ul>
         */
        public static int getGeometryTypeDimension(Type type) {
                if (type == null || (type.getTypeCode() & Type.GEOMETRY) == 0 || type.getTypeCode() == Type.NULL) {
                        throw new IllegalArgumentException("We can only treat geometry types here.");
                } else {
                        switch (type.getTypeCode()) {
                                case Type.POINT:
                                case Type.MULTIPOINT:
                                        return GeometryDimensionConstraint.DIMENSION_POINT;
                                case Type.LINESTRING:
                                case Type.MULTILINESTRING:
                                        return GeometryDimensionConstraint.DIMENSION_CURVE;
                                case Type.POLYGON:
                                case Type.MULTIPOLYGON:
                                        return GeometryDimensionConstraint.DIMENSION_SURFACE;
                                case Type.GEOMETRY:
                                case Type.GEOMETRYCOLLECTION:
                                        GeometryDimensionConstraint gdc =
                                                (GeometryDimensionConstraint) type.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                        if (gdc == null) {
                                                return GeometryDimensionConstraint.DIMENSION_UNKNOWN;
                                        } else {
                                                return gdc.getDimension();
                                        }
                                default:
                                        return GeometryDimensionConstraint.DIMENSION_UNKNOWN;
                        }
                }
        }

        /**
         * Gets the text representation (planar) dimension of the geometry type given in argument. Note
         * that if you give an invalid (not vectorial) type as an input, this method will
         * throw an InvalidArgumentException.
         *
         * @param type
         * @return
         * <ul><li>{@code GeometryDimensionConstraint.HUMAN_DIMENSION_POINT} : if we have a point or a collection of points</li>
         * <li>{@code GeometryDimensionConstraint.HUMAN_DIMENSION_CURVE} : if we have a line or a collection of lines</li>
         * <li>{@code GeometryDimensionConstraint.HUMAN_DIMENSION_SURFACE} : if we have a polygon or collection of polygons</li>
         * <li>HUMAN_DIMENSION_UNKNOWN otherwise (if the type is too generic)</li></ul>
         */
        public static String getHumanGeometryTypeDimension(Type type) {
                if (type == null || (type.getTypeCode() & Type.GEOMETRY) == 0 || type.getTypeCode() == Type.NULL) {
                        throw new IllegalArgumentException("We can only treat geometry types here.");
                } else {
                        int tc = type.getTypeCode();
                        switch (tc) {
                                case Type.GEOMETRY:
                                case Type.GEOMETRYCOLLECTION:
                                        GeometryDimensionConstraint gdc =
                                                (GeometryDimensionConstraint) type.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                        if (gdc == null) {
                                                return GeometryDimensionConstraint.HUMAN_DIMENSION_UNKNOWN;
                                        } else {
                                                return gdc.getConstraintHumanValue();
                                        }
                                case Type.POINT:
                                case Type.MULTIPOINT:
                                        return GeometryDimensionConstraint.HUMAN_DIMENSION_POINT;
                                case Type.LINESTRING:
                                case Type.MULTILINESTRING:
                                        return GeometryDimensionConstraint.HUMAN_DIMENSION_CURVE;
                                case Type.POLYGON:
                                case Type.MULTIPOLYGON:
                                        return GeometryDimensionConstraint.HUMAN_DIMENSION_SURFACE;
                                default:
                                        return GeometryDimensionConstraint.HUMAN_DIMENSION_UNKNOWN;
                        }
                }
        }

        /**
         * Returns a unique name based on the specified field name.
         * This method ensures that the name is not already used in the Metadata object
         * before returning it.
         *
         * @param metadata
         * @param fieldName
         * @return a name like 'fieldname_i' where i is an integer starting at 1
         * @throws DriverException
         */
        public static String getUniqueFieldName(Metadata metadata, String fieldName)
                throws DriverException {
                if (metadata.getFieldIndex(fieldName) == -1) {
                        return fieldName;


                }
                return getUniqueFieldName(metadata, fieldName + "_", 1);


        }

        private static String getUniqueFieldName(Metadata metadata, String fieldName, int suffix) throws DriverException {
                final String name = fieldName + suffix;


                if (metadata.getFieldIndex(name) == -1) {
                        return name;


                }
                return getUniqueFieldName(metadata, fieldName, suffix + 1);

        }

        private MetadataUtilities() {
        }
}
