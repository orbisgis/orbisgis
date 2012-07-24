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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.gdms.data.values.Value;

public class DefaultType implements Type {

        private Constraint[] constraints;
        private int typeCode;
        public static final Map<Integer, String> typesDescription = new HashMap<Integer, String>();

        static {
                java.lang.reflect.Field[] fields = Type.class.getFields();
                for (int i = 0; i < fields.length; i++) {
                        try {
                                typesDescription.put((Integer) fields[i].get(null), fields[i].getName());
                        } catch (IllegalArgumentException e) {
                                Logger.getLogger(DefaultType.class).error("Initialization error", e);
                        } catch (IllegalAccessException e) {
                                Logger.getLogger(DefaultType.class).error("Initialization error", e);
                        }
                }
        }

        /**
         * Create a new Type with an empty array of constraints, using the given typeCode
         *
         * @param description
         * @param typeCode
         * @throws InvalidTypeException
         */
        DefaultType(final int typeCode) {
                this(new Constraint[0], typeCode);
        }

        /**
         * Create a new Type, using the given typeCode and constraints array.
         *
         * @param constraints
         * @param description
         * @param typeCode
         * @throws InvalidTypeException
         */
        DefaultType(final Constraint[] constraints, final int typeCode) {
                if (null == constraints) {
                        this.constraints = new Constraint[0];
                } else {
                        this.constraints = constraints;
                }
                this.typeCode = typeCode;
        }

        @Override
        public Constraint[] getConstraints() {
                return constraints;
        }

        @Override
        public int getTypeCode() {
                return typeCode;
        }

        @Override
        public String check(final Value value) {
                for (Constraint constraint : constraints) {
                        String error = constraint.check(value);
                        if (error != null) {
                                return error;
                        }
                }

                return null;
        }

        @Override
        public String getConstraintValue(final int constraint) {
                final Constraint c = getConstraint(constraint);
                return (null == c) ? null : c.getConstraintValue();
        }

        @Override
        public boolean isRemovable() {
                for (Constraint c : constraints) {
                        if (!c.allowsFieldRemoval()) {
                                return false;
                        }
                }

                return true;
        }

        @Override
        public Constraint getConstraint(int constraint) {
                for (Constraint c : constraints) {
                        if (c.getConstraintCode() == constraint) {
                                return c;
                        }
                }
                return null;
        }

        @Override
        public int getIntConstraint(int constraint) {
                String value = getConstraintValue(constraint);
                if (value != null) {
                        try {
                                return Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("The constraint cannot "
                                        + "be expressed as an int: " + constraint, e);
                        }
                } else {
                        return -1;
                }
        }

        @Override
        public boolean getBooleanConstraint(int constraint) {
                String value = getConstraintValue(constraint);
                if (value != null) {
                        return Boolean.parseBoolean(value);
                } else {
                        return false;
                }
        }

        @Override
        public Constraint[] getConstraints(int constraintMask) {
                ArrayList<Constraint> ret = new ArrayList<Constraint>();
                for (Constraint constraint : constraints) {
                        if ((constraint.getConstraintCode() & constraintMask) > 0) {
                                ret.add(constraint);
                        }
                }

                return ret.toArray(new Constraint[ret.size()]);
        }

        @Override
        public String getHumanType() {
                switch (getTypeCode()) {
                        case BINARY:
                                return "BINARY";
                        case BOOLEAN:
                                return "BOOLEAN";
                        case BYTE:
                                return "BYTE";
                        case DATE:
                                return "DATE";
                        case DOUBLE:
                                return "DOUBLE";
                        case FLOAT:
                                return "FLOAT";
                        case INT:
                                return "INT";
                        case LONG:
                                return "LONG";
                        case SHORT:
                                return "SHORT";
                        case STREAM:
                                return "STREAM";
                        case STRING:
                                return "STRING";
                        case TIMESTAMP:
                                return "TIMESTAMP";
                        case TIME:
                                return "TIME";
                        case GEOMETRY:
                                return "GEOMETRY";
                        case RASTER:
                                return "RASTER";
                        case NULL:
                                return "NULL";
                        case COLLECTION:
                                return "COLLECTION";
                        case POINT:
                                return "POINT";
                        case LINESTRING:
                                return "LINESTRING";
                        case POLYGON:
                                return "POLYGON";
                        case MULTIPOLYGON:
                                return "MULTIPOLYGON";
                        case MULTILINESTRING:
                                return "MULTILINESTRING";
                        case MULTIPOINT:
                                return "MULTIPOINT";
                        case GEOMETRYCOLLECTION:
                                return "GEOMETRYCOLLECTION";
                }
                throw new IllegalArgumentException("How did you manage to create"
                        + "an unrecognized type ?");
        }
}
