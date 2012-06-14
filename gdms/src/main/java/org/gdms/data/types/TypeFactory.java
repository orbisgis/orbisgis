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
package org.gdms.data.types;

import java.util.HashMap;

/**
 * Factory to create data type instances.
 *
 * @author Fernando Gonzalez Cortes
 */
public final class TypeFactory {
        
        private static void checkNotNull(int typeCode) {
                if (typeCode == Type.NULL) {
                        throw new InvalidTypeException("You cannot build a NULL type!");
                }
        }

        /**
         * Creates a type with the specified type code. The code must be one of the
         * constants in Type interface
         *
         * @param typeCode
         * @return
         */
        public static Type createType(final int typeCode) {
                final String desc = DefaultType.typesDescription.get(typeCode);
                if (desc == null ) {
                        throw new InvalidTypeException("There is no known type with typeCode " + typeCode);
                }
                return createType(typeCode, desc);
        }

        /**
         * Creates a type with the specified type code and the specified name. The
         * code must be one of the constants in Type interface
         *
         * @param typeCode
         * @param typeName
         * @return
         */
        public static Type createType(final int typeCode, final String typeName) {
                if (null == typeName) {
                        return createType(typeCode);
                } else {
                        checkNotNull(typeCode);
                        final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
                                typeCode);
                        return typeDef.createType();
                }
        }

        /**
         * Creates a type with the specified type code and the specified
         * constraints. The code must be one of the constants in Type interface
         *
         * @param typeCode
         * @param constraints
         * @return
         * @throws InvalidTypeException
         * If the constraints are not valid for this type
         */
        public static Type createType(final int typeCode,
                final Constraint... constraints) {
                if (null == constraints) {
                        return createType(typeCode);
                } else {
                        return createType(typeCode, DefaultType.typesDescription.get(typeCode), constraints);
                }
        }

        /**
         * Creates a type with the specified type code and the specified constraints
         * and name. The code must be one of the constants in Type interface
         *
         * @param typeCode
         * @param typeName
         * @param constraints
         * @return
         * @throws InvalidTypeException
         * If the constraints are not valid for this type
         */
        public static Type createType(final int typeCode, final String typeName,
                final Constraint... constraints) {
                checkNotNull(typeCode);
                if (null == constraints) {
                        return createType(typeCode, typeName);
                } else {
                        final int fc = constraints.length;
                        final int[] constraintNames = new int[fc];
                        for (int i = 0; i < fc; i++) {
                                constraintNames[i] = constraints[i].getConstraintCode();
                        }
                        final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
                                typeCode, constraintNames);
                        return typeDef.createType(constraints);
                }
        }

        /**
         * Gets the name of given type.
         *
         * @param typeCode a valid type code.
         * @return a name
         * @throws IllegalArgumentException if the type code is not valid
         */
        public static String getTypeName(int typeCode) {
                switch (typeCode) {
                        case Type.BINARY:
                                return "binary";
                        case Type.BOOLEAN:
                                return "boolean";
                        case Type.BYTE:
                                return "byte";
                        case Type.COLLECTION:
                                return "value collection";
                        case Type.DATE:
                                return "date";
                        case Type.DOUBLE:
                                return "double";
                        case Type.FLOAT:
                                return "float";
                        case Type.GEOMETRY:
                                return "geometry";
                        case Type.INT:
                                return "int";
                        case Type.LONG:
                                return "long";
                        case Type.NULL:
                                return "null";
                        case Type.RASTER:
                                return "raster";
                        case Type.SHORT:
                                return "short";
                        case Type.STRING:
                                return "string";
                        case Type.TIME:
                                return "time";
                        case Type.TIMESTAMP:
                                return "timestamp";
                        case Type.GEOMETRYCOLLECTION:
                                return "geometrycollection";
                        case Type.POINT:
                                return "point";
                        case Type.MULTIPOINT:
                                return "multipoint";
                        case Type.LINESTRING:
                                return "linestring";
                        case Type.MULTILINESTRING:
                                return "multilinestring";
                        case Type.POLYGON:
                                return "polygon";
                        case Type.MULTIPOLYGON:
                                return "multipolygon";
                        default:
                                throw new IllegalArgumentException("Unknown data type: " + typeCode);
                }
        }

        /**
         * Checks if the type code is that of a numeric type.
         *
         * @param typeCode a valid type code.
         * @return true if it is a numeric type
         */
        public static boolean isNumerical(int typeCode) {
                return ((typeCode == Type.BYTE) || (typeCode == Type.DOUBLE)
                        || (typeCode == Type.FLOAT) || (typeCode == Type.INT)
                        || (typeCode == Type.LONG) || (typeCode == Type.SHORT)) || typeCode == Type.NULL;
        }

        /**
         * Gets all type codes.
         *
         * @return a array of all type codes
         */
        public static int[] getTypes() {
                return new int[]{Type.BINARY, Type.BOOLEAN, Type.BYTE, Type.DATE,
                                Type.DOUBLE, Type.FLOAT, Type.GEOMETRY, Type.INT, Type.LONG,
                                Type.RASTER, Type.SHORT, Type.STRING, Type.TIME, Type.TIMESTAMP,
                                Type.POINT, Type.MULTIPOINT, Type.LINESTRING, Type.MULTILINESTRING,
                                Type.POLYGON, Type.MULTIPOLYGON, Type.GEOMETRYCOLLECTION};
        }

        /**
         * Checks if the type code if that of a spatial type.
         *
         * @param typeCode a valid type code
         * @return true if it is a spatial type
         */
        public static boolean isSpatial(int typeCode) {
                return ((typeCode & Type.GEOMETRY) != 0 || (typeCode == Type.RASTER)) || typeCode == Type.NULL;
        }

        /**
         * Get an integer array containing all the vectorial type codes.
         *
         * @return a array of type codes.
         */
        public static int[] getVectorialTypes() {
                return new int[]{Type.GEOMETRY, Type.POINT, Type.MULTIPOINT, Type.LINESTRING,
                                Type.MULTILINESTRING, Type.POLYGON, Type.MULTIPOLYGON, Type.GEOMETRYCOLLECTION};
        }

        /**
         * Check whether typeCode represents a valid vectorial code, ie if it is a
         * geometry or geometry collection of any authorized kind.
         *
         * @param typeCode
         * @return
         */
        public static boolean isVectorial(int typeCode) {
                return (typeCode & Type.GEOMETRY) != 0 || typeCode == Type.NULL;
        }

        /**
         * Returns the type being able to accept all the values the other type
         * accepts. Returns Type.NULL if the types are not compatible.
         * 
         * Type.NULL is compatible with everything, and the broader type is the other type.
         *
         * @param type1
         * @param type2
         * @return the broader type code of the two parameter type codes.
         */
        public static int getBroaderType(int type1, int type2) {
                if (type1 == Type.NULL) {
                        return type2;
                } else if (type2 == Type.NULL) {
                        return type1;
                }
                
                if (isNumerical(type1) && isNumerical(type2)) {
                        HashMap<Integer, Integer> typeSort = new HashMap<Integer, Integer>();
                        typeSort.put(Type.BYTE, 0);
                        typeSort.put(Type.SHORT, 1);
                        typeSort.put(Type.INT, 2);
                        typeSort.put(Type.LONG, 3);
                        typeSort.put(Type.FLOAT, 4);
                        typeSort.put(Type.DOUBLE, 5);
                        HashMap<Integer, Integer> sortType = new HashMap<Integer, Integer>();
                        sortType.put(0, Type.BYTE);
                        sortType.put(1, Type.SHORT);
                        sortType.put(2, Type.INT);
                        sortType.put(3, Type.LONG);
                        sortType.put(4, Type.FLOAT);
                        sortType.put(5, Type.DOUBLE);

                        Integer sort1 = typeSort.get(type1);
                        Integer sort2 = typeSort.get(type2);
                        int sort = Math.max(sort1, sort2);
                        return sortType.get(sort);
                } else if (isTime(type1) && isTime(type2)) {
                        HashMap<Integer, Integer> typeSort = new HashMap<Integer, Integer>();
                        typeSort.put(Type.DATE, 0);
                        typeSort.put(Type.TIME, 1);
                        typeSort.put(Type.TIMESTAMP, 2);
                        HashMap<Integer, Integer> sortType = new HashMap<Integer, Integer>();
                        sortType.put(0, Type.DATE);
                        sortType.put(1, Type.TIME);
                        sortType.put(2, Type.TIMESTAMP);

                        Integer sort1 = typeSort.get(type1);
                        Integer sort2 = typeSort.get(type2);
                        int sort = Math.max(sort1, sort2);
                        return sortType.get(sort);
                } else if (isVectorial(type1) && isVectorial(type2)) {
                        if ((type1 & Type.GEOMETRYCOLLECTION) == Type.GEOMETRYCOLLECTION 
                                && (type2 & Type.GEOMETRYCOLLECTION) == Type.GEOMETRYCOLLECTION) {
                                return Type.GEOMETRYCOLLECTION;
                        } else {
                                return Type.GEOMETRY;
                        }
                } else if (type1 == type2) {
                                return type1;
                }

                return Type.NULL;
        }

        /**
         * Checks if the type code is that of a date/time-related type code.
         *
         * @param typeCode
         * @return true if it is a date/time-related type code
         */
        public static boolean isTime(int typeCode) {
                return (typeCode == Type.DATE) || (typeCode == Type.TIME)
                        || (typeCode == Type.TIMESTAMP) || typeCode == Type.NULL;
        }

        /**
         * Checks if the first type can be casted to the second.
         *
         * @param fromTypeCode a type code
         * @param toTypeCode another type code
         * @return true if the cast if possible
         */
        public static boolean canBeCastTo(int fromTypeCode, int toTypeCode) {
                if (fromTypeCode == toTypeCode) {
                        return true;
                }
                return getBroaderType(toTypeCode, fromTypeCode) == toTypeCode;
        }

        private TypeFactory() {
        }
}
