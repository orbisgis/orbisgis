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
package org.gdms.sql.function;

import org.apache.log4j.Logger;
import org.gdms.data.types.Type;

/**
 * Specifies the type, description and validation of a sql function
 * Value argument
 * 
 */
public class ScalarArgument implements Argument {

        public static final ScalarArgument BINARY = new ScalarArgument(Type.BINARY);
        public static final ScalarArgument BOOLEAN = new ScalarArgument(Type.BOOLEAN);
        public static final ScalarArgument BYTE = new ScalarArgument(Type.BYTE);
        public static final ScalarArgument DATE = new ScalarArgument(Type.DATE);
        public static final ScalarArgument DOUBLE = new ScalarArgument(Type.DOUBLE);
        public static final ScalarArgument FLOAT = new ScalarArgument(Type.FLOAT);
        public static final ScalarArgument GEOMETRY = new ScalarArgument(Type.GEOMETRY);
        public static final ScalarArgument INT = new ScalarArgument(Type.INT);
        public static final ScalarArgument LONG = new ScalarArgument(Type.LONG);
        public static final ScalarArgument RASTER = new ScalarArgument(Type.RASTER);
        public static final ScalarArgument SHORT = new ScalarArgument(Type.SHORT);
        public static final ScalarArgument STRING = new ScalarArgument(Type.STRING);
        public static final ScalarArgument TIME = new ScalarArgument(Type.TIME);
        public static final ScalarArgument TIMESTAMP = new ScalarArgument(Type.TIMESTAMP);
        private int typeCode;
        private String description;
        private ArgumentValidator argValidator;
        private Logger logger = Logger.getLogger(ScalarArgument.class);

        public ScalarArgument(int typeCode) {
                this(typeCode, getValidation(typeCode));
        }

        /**
         * Returns a String describing the <tt>typeCode</tt>, or throws an exception
         * if the code is unknown.
         * @param typeCode a type code
         * @return a non-null string describing the argument type code
         * @throws IllegalArgumentException if the type code is unknown
         */
        private static String getValidation(int typeCode) {
                if (typeCode == Type.STRING) {
                        return "String parameter";
                } else if (typeCode == Type.BYTE) {
                        return "Byte parameter";
                } else if (typeCode == Type.DOUBLE) {
                        return "Double parameter";
                } else if (typeCode == Type.FLOAT) {
                        return "Float parameter";
                } else if (typeCode == Type.INT) {
                        return "Int parameter";
                } else if (typeCode == Type.LONG) {
                        return "Long parameter";
                } else if (typeCode == Type.NULL) {
                        return "Null parameter";
                } else if (typeCode == Type.SHORT) {
			return "Short parameter";
		} else if (typeCode == Type.DATE) {
                        return "Date parameter";
                } else if (typeCode == Type.TIME) {
                        return "Time parameter";
                } else if (typeCode == Type.TIMESTAMP) {
                        return "Timestamp parameter";
                } else if (typeCode == Type.GEOMETRY) {
                        return "Geometry parameter";
                } else if (typeCode == Type.RASTER) {
                        return "Raster parameter";
                } else if (typeCode == Type.BOOLEAN) {
                        return "Boolean parameter";
                } else if (typeCode == Type.BINARY) {
                        return "Binary parameter";
                } else {
                        throw new IllegalArgumentException("Unknown type code: " + typeCode);
                }
        }

        /**
         * Creates a new ScalarArgument with the given type code and validator
         * @param typeCode
         * @param argumentValidator
         */
        public ScalarArgument(int typeCode, ArgumentValidator argumentValidator) {
                this(typeCode, getValidation(typeCode), argumentValidator);
        }

        /**
         * Creates a new ScalarArgument with the given type code and description
         * @param typeCode
         * @param description
         */
        public ScalarArgument(int typeCode, String description) {
                this(typeCode, description, null);
        }

        /**
         * Creates a new ScalarArgument with the given type code, description
         * and validator
         * @param typeCode
         * @param description
         * @param argValidator
         */
        public ScalarArgument(int typeCode, String description,
                ArgumentValidator argValidator) {
                this.typeCode = typeCode;
                this.description = description;
                this.argValidator = argValidator;
                logger.trace("Constructor");
        }

        /**
         * Validates a type against this argument
         * @param type a type
         * @return true if the type is valid, false otherwise
         */
        public boolean isValid(Type type) {
                if ((type.getTypeCode() & this.typeCode) == 0) {
                        return false;
                } else {
                        if (argValidator != null) {
                                return argValidator.isValid(type);
                        } else {
                                return true;
                        }
                }
        }

        @Override
        public String getDescription() {
                return description;
        }

        /**
         * Get a bit-or of this argument accepted parameters.
         *
         * @return
         */
        public int getTypeCode() {
                return typeCode;
        }

        @Override
        public boolean isScalar() {
                return true;
        }

        @Override
        public boolean isTable() {
                return false;
        }
}
