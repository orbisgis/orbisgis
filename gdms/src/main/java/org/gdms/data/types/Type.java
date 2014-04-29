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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
 * Interface that represent the type of a field. The values stored here are 
 * used to make efficient computation between the types. They are designed so that 
 * geometry types are compatible, when possible.
 * 
 */
public interface Type {
	int BINARY = 1;
	int BOOLEAN = 2;
	int BYTE = 4;
	int DATE = 8;
	int DOUBLE = 16;
	int FLOAT = 32;
	int INT = 64;
	int LONG = 128;
	int SHORT = 256;
	int STRING = 512;
	int TIMESTAMP = 1024;
	int TIME = 2048;
	int GEOMETRY = 4096;
	int RASTER = 8192;
        int STREAM = 4194304;

	int NULL = -1;
	int COLLECTION = 16384;
        
        int POINT = 32768 | Type.GEOMETRY;
        int LINESTRING = 65536| Type.GEOMETRY;
        int POLYGON = 131072 | Type.GEOMETRY;
        int MULTIPOLYGON = 262144 | Type.GEOMETRYCOLLECTION;
        int MULTILINESTRING = 524288 | Type.GEOMETRYCOLLECTION;
        int MULTIPOINT = 1048576 | Type.GEOMETRYCOLLECTION;
        int GEOMETRYCOLLECTION = 2097152 | Type.GEOMETRY;

	/**
	 * Returns the array of the constraints this type has
	 * 
	 * @return the constraints
	 */
	Constraint[] getConstraints();

	/**
	 * Gets the code of this type. Must be one of the constants in this
	 * interface
	 * 
	 * @return the typeCode
	 */
	int getTypeCode();

	/**
	 * Checks that the specified value is suitable for this type taking into
	 * account all the constraints
	 * 
	 * @param value
	 * @return
	 */
	String check(final Value value);

	/**
	 * Gets the value of the specified constraint
	 * 
         * @param constraint 
         * @return
	 */
	String getConstraintValue(int constraint);

	/**
	 * returns true if the field can be removed, false otherwise
	 * 
	 * @return
	 */
	boolean isRemovable();

	/**
	 * Gets the specified constraint
	 * 
         * @param constraint
	 * @return
	 */
	Constraint getConstraint(int constraint);

	/**
	 * Gets the value of a constraint as an int value
	 * 
	 * @param constraint
	 * @return the value of the constraint or -1 if the constraint doesn't exist
	 * @throws IllegalArgumentException
	 *             if the constraint cannot be expressed as an integer value
	 */
	int getIntConstraint(int constraint);

	/**
	 * Gets the value of a constraint as boolean value
	 * 
	 * @param constraint
	 * @return the value of the constraint or false if the constraint doesn't
	 *         exist
	 */
	boolean getBooleanConstraint(int constraint);

	/**
	 * Gets the constraints in the type filtered by a bit mask. The constants
	 * used in the mask are those from {@link Constraint}
	 * 
	 * @param constraintMask
	 * @return
	 */
	Constraint[] getConstraints(int constraintMask);

    /**
     * Get a human readable definition of the {@code Type} instance.
     * @return
     */
    String getHumanType();
}