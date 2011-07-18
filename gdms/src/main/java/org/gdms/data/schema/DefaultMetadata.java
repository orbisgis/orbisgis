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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
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
 **/
package org.gdms.data.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

/**
 * Default implementation for the Metadata interface
 * 
 */
public class DefaultMetadata implements Metadata {

        public static final String ALREADY_EXISTS = " already exists";
        private static final String THE_FIELD = "The field ";
        private List<Type> fieldsTypes;
        private List<String> fieldsNames;

        /**
         * Creates a DefaultMetadata instance with no fields
         */
        public DefaultMetadata() {
                this.fieldsTypes = new ArrayList<Type>();
                this.fieldsNames = new ArrayList<String>();
        }

        /**
         * Creates a DefaultMetadata instance with the specified field names and
         * field types
         *
         * @param fieldsTypes
         * @param fieldsNames
         */
        public DefaultMetadata(Type[] fieldsTypes, String[] fieldsNames) {
                this.fieldsTypes = new LinkedList<Type>(Arrays.asList(fieldsTypes));
                this.fieldsNames = new LinkedList<String>(Arrays.asList(fieldsNames));
        }

        /**
         * Creates a DefaultMetadata instance with the same contents as the metadata
         * instance specified as a parameter
         *
         * @param originalMetadata
         * @throws DriverException
         *             If there is some exception reading the metadata from the
         *             parameter
         */
        public DefaultMetadata(final Metadata originalMetadata)
                throws DriverException {
                this();
                addAll(originalMetadata);

        }

        /**
         * Adds the whole content of a Metadata object to this Metadata
         * @param metadata the Metadata whose content is to add
         * @throws DriverException if a field from <tt>metadata</tt> already
         *      exists in this object.
         */
        public final void addAll(final Metadata metadata) throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        String fieldName = metadata.getFieldName(i);
                        addField(fieldName, metadata.getFieldType(i));
                }
        }

        /**
         * Adds the whole content of a Metadata object to this Metadata. If there is
         * naming conflicts between the added names and the already present ones,
         * the added names are changed like "oldname_0" "oldname_1" ... until a
         * available name is found.
         * 
         * @param metadata the Metadata whose content is to add
         * @throws DriverException if there is a problem accessing the metadata.
         */
        public final void addAndRenameAll(final Metadata metadata) throws DriverException {
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        String fieldName = metadata.getFieldName(i);
                        String newFieldName = fieldName;
                        int j = 0;
                        while (fieldsNames.contains(newFieldName)) {
                                newFieldName = fieldName + '_' + j;
                                j++;
                        }
                        fieldsTypes.add(metadata.getFieldType(i));
                        fieldsNames.add(newFieldName);
                }
        }

        @Override
        public int getFieldCount() {
                return fieldsTypes.size();
        }

        @Override
        public Type getFieldType(int fieldId) {
                return fieldsTypes.get(fieldId);
        }

        @Override
        public String getFieldName(int fieldId) {
                return fieldsNames.get(fieldId);
        }

        /**
         * Adds a field to the instance. This field will be taken into account by
         * the getFieldXXX methods that implement the Metadata interface
         *
         * @param fieldName
         * @param typeCode
         * @throws DriverException if the specified field already exists
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code
         */
        public void addField(final String fieldName, final int typeCode)
                throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(fieldName);
                        fieldsTypes.add(TypeFactory.createType(typeCode));

                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Adds a field to the instance. This field will be taken into account by
         * the getFieldXXX methods that implement the Metadata interface
         *
         * @param fieldName
         * @param typeCode
         * @param constraints
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code or the
         *             specified constraints are not valid for the given type
         * @throws DriverException if the specified field already exists
         */
        public void addField(final String fieldName, final int typeCode,
                final Constraint... constraints) throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(fieldName);
                        fieldsTypes.add(TypeFactory.createType(typeCode, constraints));
                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Adds a field to the instance. This field will be taken into account by
         * the getFieldXXX methods that implement the Metadata interface. This
         * method gives the type a name to be displayed to the user
         *
         * @param fieldName
         * @param typeCode
         * @param typeName
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code
         * @throws DriverException if the specified field already exists
         */
        public void addField(final String fieldName, final int typeCode,
                final String typeName) throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(fieldName);
                        fieldsTypes.add(TypeFactory.createType(typeCode, typeName));
                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Adds a field to the instance. This field will be taken into account by
         * the getFieldXXX methods that implement the Metadata interface. This
         * method gives the type a name to be displayed to the user
         *
         * @param fieldName
         * @param typeCode
         * @param typeName
         * @param constraints
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code or the
         *             specified constraints are not valid for the given type
         * @throws DriverException if the specified field already exists
         */
        public void addField(final String fieldName, final int typeCode,
                final String typeName, final Constraint... constraints)
                throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(fieldName);
                        fieldsTypes.add(TypeFactory.createType(typeCode, typeName,
                                constraints));
                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Inserts a field into the specified position. This field will be taken
         * into account by the getFieldXXX methods that implement the Metadata
         * interface.
         *
         * @param index
         * @param fieldName
         * @param typeCode
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code
         * @throws DriverException if the specified field already exists
         */
        public void addField(int index, String fieldName, int typeCode)
                throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(index, fieldName);
                        fieldsTypes.add(index, TypeFactory.createType(typeCode));

                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Inserts a field into the specified position. This field will be taken
         * into account by the getFieldXXX methods that implement the Metadata
         * interface.
         *
         * @param index
         * @param fieldName
         * @param typeCode
         * @param constraints
         * @throws InvalidTypeException
         *             If the specified type code is not a valid type code or the
         *             specified constraints are not valid for the given type
         * @throws DriverException if the specified field already exists
         */
        public void addField(int index, String fieldName, int typeCode,
                Constraint... constraints) throws
                DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(index, fieldName);
                        fieldsTypes.add(index, TypeFactory.createType(typeCode, "",
                                constraints));
                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + ALREADY_EXISTS);
                }
        }

        /**
         * Adds a field with the specified name and type. This field will be taken
         * into account by the getFieldXXX methods that implement the Metadata
         * interface.
         *
         * @param fieldName
         * @param type
         * @throws DriverException if the specified field already exists
         */
        public void addField(String fieldName, Type type) throws DriverException {
                if (!isFieldExists(fieldName)) {
                        fieldsNames.add(fieldName);
                        fieldsTypes.add(type);
                } else {
                        throw new DriverException(THE_FIELD + fieldName
                                + " already exists.");
                }
        }

        /**
         * Check is a field exists
         *
         * @param fieldName
         * @return
         */
        private boolean isFieldExists(String fieldName) {
                return fieldsNames.contains(fieldName);
        }
        
        /**
         * Clears all fields
         */
        public void clear() {
                fieldsNames.clear();
                fieldsTypes.clear();
        }

        /**
         * Get the field index. Return -1 is the field doesn't exist.
         *
         */
        @Override
        public int getFieldIndex(String fieldName) {
                int i = 0;
                for (String fName : fieldsNames) {
                        if (fName.equals(fieldName)) {
                                return i;
                        }
                        i++;

                }
                return -1;
        }

        /**
         * @return the schema
         */
        @Override
        public Schema getSchema() {
                return null;
        }

        @Override
        public String[] getFieldNames() {
                return fieldsNames.toArray(new String[fieldsNames.size()]);
        }
}
