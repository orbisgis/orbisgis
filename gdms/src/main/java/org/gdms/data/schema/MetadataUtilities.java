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
package org.gdms.data.schema;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * An utility class to help the exploration of Metadata instances
 */
public final class MetadataUtilities {

        /**
         * Gets the field names in the metadata instance that have the primary key
         * constraint
         *
         * @param metadata
         * @return
         * @throws DriverException
         *             if raised when reading metadata
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
         * @throws DriverException
         *             if raised when reading metadata
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
                int i = 0;
                for (Integer idx : tmpPKIndices) {
                        pkIndices[i++] = idx.intValue();
                }

                return pkIndices;
        }

        /**
         * Returns true if the field at the specified index is read only
         *
         * @param metadata
         * @param fieldId
         * @return
         * @throws DriverException
         *             if raised when reading metadata
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
         * @throws DriverException
         *             if raised when reading metadata
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
         * @throws DriverException
         *             if raised when reading metadata
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
                        if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
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
         * @param metadata the Metadata to search
         * @param spatialField the fieldIndex to look for
         * @return a dimension between 0 and 2, or -1 if unknown
         * @throws DriverException
         */
        public static int getGeometryDimension(Metadata metadata, int spatialField) throws DriverException {

                Type fieldType = metadata.getFieldType(spatialField);
                if (fieldType.getTypeCode() == Type.GEOMETRY) {
                        GeometryTypeConstraint geomTypeConstraint = (GeometryTypeConstraint) fieldType.getConstraint(Constraint.GEOMETRY_TYPE);
                        if (geomTypeConstraint == null) {
                                return -1;
                        } else {
                                int geomType = geomTypeConstraint.getGeometryType();

                                if ((geomType == GeometryTypeConstraint.POLYGON)
                                        || (geomType == GeometryTypeConstraint.MULTI_POLYGON)) {
                                        return 2;
                                } else if ((geomType == GeometryTypeConstraint.LINESTRING)
                                        || (geomType == GeometryTypeConstraint.MULTI_LINESTRING)) {
                                        return 1;
                                } else if ((geomType == GeometryTypeConstraint.POINT)
                                        || (geomType == GeometryTypeConstraint.MULTI_POINT)) {
                                        return 0;
                                } else {
                                        throw new UnsupportedOperationException("Unknown geometry type: " + geomType);
                                }
                        }
                }
                return -1;

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
                        if ((typeCode == Type.GEOMETRY) || (typeCode == Type.RASTER)) {
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
                        if ((typeCode == Type.GEOMETRY)) {
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
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static boolean isSpatial(Metadata metadata) throws DriverException {
                return getSpatialFieldIndex(metadata) != -1;
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

        /*
         * public static Constraint getCRSConstraint(int srid) {
         *
         * if (srid == -1) { return new
         * CRSConstraint(CRSUtil.getCRSFromEPSG("4326").toWkt()); } else { return
         * new CRSConstraint(CRSUtil.getCRSFromEPSG(
         * Integer.toString(srid)).toWkt()); } }
         *
         *
         * public static CoordinateReferenceSystem getCRS(Metadata metadata) throws
         * DriverException { CoordinateReferenceSystem crs = NullCRS.singleton; for
         * (int i = 0; i < metadata.getFieldCount(); i++) { Type fieldType =
         * metadata.getFieldType(i); if (fieldType.getTypeCode() == Type.GEOMETRY) {
         * CRSConstraint crsConstraint = (CRSConstraint) fieldType
         * .getConstraint(Constraint.CRS); if ((crsConstraint != null) &&
         * (crsConstraint.getConstraintCode() != -1)) { crs =
         * crsConstraint.getCrs(); break; } } } return crs; }
         *
         * public static Metadata addCRSConstraint(Metadata metadata, String
         * geomField, GeodeticCRS targetCRS) throws DriverException {
         * DefaultMetadata defaultMetadata = new DefaultMetadata(); for (int i = 0;
         * i < metadata.getFieldCount(); i++) { String fieldName =
         * metadata.getFieldName(i); Type fieldType = metadata.getFieldType(i); if
         * (fieldName.equals(geomField)) { Constraint[] constrs =
         * fieldType.getConstraints(Constraint.ALL &
         * ~Constraint.GEOMETRY_DIMENSION); ArrayList<Constraint> constTarget = new
         * ArrayList<Constraint>(); for (Constraint constraint : constrs) {
         *
         * if (!(constraint instanceof CRSConstraint)) {
         * constTarget.add(constraint); }
         *
         * } constTarget.add(new CRSConstraint(targetCRS.toWkt()));
         * defaultMetadata.addField(fieldName, TypeFactory.createType(
         * Type.GEOMETRY, constTarget.toArray(new Constraint[0])));
         *
         * } else { defaultMetadata.addField(fieldName, fieldType); } } return
         * defaultMetadata; }
         */
}
