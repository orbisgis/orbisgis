/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.gdms.data.types;

import java.util.HashMap;

/**
 * factory to create data type instances
 * 
 * @author Fernando Gonzalez Cortes
 */
public final class TypeFactory {

        /**
         * Creates a type with the specified type code. The code must be one of the
         * constants in Type interface
         *
         * @param typeCode
         * @return
         */
        public static Type createType(final int typeCode) {
                return createType(typeCode, DefaultType.typesDescription.get(typeCode));
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
         *             If the constraints are not valid for this type
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
         *             If the constraints are not valid for this type
         */
        public static Type createType(final int typeCode, final String typeName,
                final Constraint... constraints) {
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

        public static String getTypeName(int typeCode) {
                int toEval = (typeCode & Type.GEOMETRY) != 0 && typeCode != Type.GEOMETRY ? typeCode ^ Type.GEOMETRY : typeCode;
                switch (toEval) {
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
                        case Type.GEOMETRYCOLLECTION :
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

        public static boolean isNumerical(int typeCode) {
                return (typeCode == Type.BYTE) || (typeCode == Type.DOUBLE)
                        || (typeCode == Type.FLOAT) || (typeCode == Type.INT)
                        || (typeCode == Type.LONG) || (typeCode == Type.SHORT);
        }

        public static int[] getTypes() {
                return new int[]{Type.BINARY, Type.BOOLEAN, Type.BYTE, Type.DATE,
                                Type.DOUBLE, Type.FLOAT, Type.GEOMETRY, Type.INT, Type.LONG,
                                Type.RASTER, Type.SHORT, Type.STRING, Type.TIME, Type.TIMESTAMP};
        }

        public static boolean isSpatial(int typeCode) {
                return (typeCode == Type.GEOMETRY) || (typeCode == Type.RASTER);
        }

        /**
         *
         * Return the type being able to accept all the values the other type
         * accepts. Returns -1 if the types are not compatible.
         *
         * @param type1
         * @param type2
         * @return
         *
         * @author gearscape
         */
        public static int getBroaderType(int type1, int type2) {
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
                } else {
                        if (type1 == type2) {
                                return type1;
                        }
                }

                return -1;
        }

        /**
         *
         * @param typeCode
         * @return
         *
         * @author gearscape
         */
        public static boolean isTime(int typeCode) {
                return (typeCode == Type.DATE) || (typeCode == Type.TIME)
                        || (typeCode == Type.TIMESTAMP);
        }

        public static boolean canBeCastTo(int fromTypeCode, int toTypeCode) {
                if (fromTypeCode == toTypeCode) {
                        return true;
                }
                return getBroaderType(toTypeCode, fromTypeCode) == toTypeCode;
        }

        private TypeFactory() {
        }
}
