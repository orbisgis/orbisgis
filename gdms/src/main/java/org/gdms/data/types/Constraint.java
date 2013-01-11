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

import org.gdms.data.values.Value;

/**
 * Interface to represent constraints of types, like length in string, precision
 * in numeric types, etc.
 *
 */
public interface Constraint {

        int AUTO_INCREMENT = 1;
        int CRS = 2;
        int GEOMETRY_TYPE = 4;
        int DIMENSION_3D_GEOMETRY = 8;
        int LENGTH = 16;
        int MAX = 32;
        int MIN = 64;
        int NOT_NULL = 128;
        int PATTERN = 256;
        int PK = 512;
        int PRECISION = 1024;
        int READONLY = 2048;
        int SCALE = 4096;
        int UNIQUE = 8192;
        int RASTER_TYPE = 16384;
        int DEFAULT_STRING_VALUE = 32768;
        int FK = 65536;
        int DIMENSION_2D_GEOMETRY = 131072;

        int ALL = AUTO_INCREMENT | CRS | DIMENSION_3D_GEOMETRY
                | LENGTH | MAX | MIN | NOT_NULL | PATTERN | PK
                | PRECISION | READONLY | SCALE | UNIQUE | RASTER_TYPE| DIMENSION_2D_GEOMETRY;
        int CONSTRAINT_TYPE_FIELD = 0;
        int CONSTRAINT_TYPE_CHOICE = 1;
        int CONSTRAINT_TYPE_STRING_LITERAL = 2;
        int CONSTRAINT_TYPE_INTEGER_LITERAL = 3;
        int CONSTRAINT_TYPE_RASTER = 4;
        int CONSTRAINT_TYPE_CRS = 5;

        /**
         * Gets the human readable constraint name
         *
         * @return
         */
        int getConstraintCode();

        /**
         * Gets the string representation of the value of the constraint
         *
         * @return
         */
        String getConstraintValue();

        /**
         * Checks if the specified value fits the constraint
         *
         * @param value
         * @return
         */
        String check(final Value value);

        /**
         * Returns true if the constraint lets the removal of the field. For example
         * false in the case of primary key constraint
         *
         * @return
         */
        boolean allowsFieldRemoval();

        /**
         * Gets a byte[] representation of this constraint
         *
         * @return
         */
        byte[] getBytes();

        /**
         * Returns the type of the constraint based on the way to represent the
         * information.
         *
         * @return typeCode
         */
        int getType();

        /**
         * Returns the available choices for a CONSTRAINT_TYPE_CHOICE constraint.
         *
         * @return
         * @throws UnsupportedOperationException
         *             If this constraint is not of type CONSTRAINT_TYPE_CHOICE
         */
        String[] getChoiceStrings();

        /**
         * Returns the code of the available choices for a CONSTRAINT_TYPE_CHOICE
         * constraint.
         *
         * @return
         * @throws UnsupportedOperationException
         *             If this constraint is not of type CONSTRAINT_TYPE_CHOICE
         */
        int[] getChoiceCodes();

        /**
         * Gets a human readable representation of the value of this Constraint
         *
         * @return
         */
        String getConstraintHumanValue();
}
