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

import org.gdms.data.values.Value;

/**
 * Interface to represent constraints of types, like length in string, precision
 * in numeric types, etc.
 *
 */
public interface Constraint {

        int AUTO_INCREMENT = 1;
        int SRID = 2;
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

        int ALL = AUTO_INCREMENT | SRID | DIMENSION_3D_GEOMETRY
                | LENGTH | MAX | MIN | NOT_NULL | PATTERN | PK
                | PRECISION | READONLY | SCALE | UNIQUE | RASTER_TYPE| DIMENSION_2D_GEOMETRY;
        int CONSTRAINT_TYPE_FIELD = 0;
        int CONSTRAINT_TYPE_CHOICE = 1;
        int CONSTRAINT_TYPE_STRING_LITERAL = 2;
        int CONSTRAINT_TYPE_INTEGER_LITERAL = 3;
        int CONSTRAINT_TYPE_RASTER = 4;

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
