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
package org.gdms.data.types;

import org.gdms.data.values.Value;

/**
 * Interface to represent constraints of types, like length in string, precision
 * in numeric types, etc.
 *
 */
public interface Constraint {

	public static final int AUTO_INCREMENT = 1;
	public static final int CRS = 2;
	public static final int GEOMETRY_TYPE = 4;
	public static final int GEOMETRY_DIMENSION = 8;
	public static final int LENGTH = 16;
	public static final int MAX = 32;
	public static final int MIN = 64;
	public static final int NOT_NULL = 128;
	public static final int PATTERN = 256;
	public static final int PK = 512;
	public static final int PRECISION = 1024;
	public static final int READONLY = 2048;
	public static final int SCALE = 4096;
	public static final int UNIQUE = 8192;
	public static final int RASTER_TYPE = 16384;
	public static final int ALL = AUTO_INCREMENT | CRS | GEOMETRY_DIMENSION
			| GEOMETRY_TYPE | LENGTH | MAX | MIN | NOT_NULL | PATTERN | PK
			| PRECISION | READONLY | SCALE | UNIQUE | RASTER_TYPE;

	public static final int CONSTRAINT_TYPE_BOOLEAN = 0;
	public static final int CONSTRAINT_TYPE_CHOICE = 1;
	public static final int CONSTRAINT_TYPE_STRING_LITERAL = 2;
	public static final int CONSTRAINT_TYPE_INTEGER_LITERAL = 3;
	public static final int CONSTRAINT_TYPE_OTHER = 4;

	/**
	 * Gets the human readable constraint name
	 *
	 * @return
	 */
	public int getConstraintCode();

	/**
	 * Gets the string representation of the value of the constraint
	 *
	 * @return
	 */
	public String getConstraintValue();

	/**
	 * Checks if the specified value fits the constraint
	 *
	 * @param value
	 * @return
	 */
	public String check(final Value value);

	/**
	 * Returns true if the constraint lets the removal of the field. For example
	 * false in the case of primary key constraint
	 *
	 * @return
	 */
	public boolean allowsFieldRemoval();

	/**
	 * Gets a byte[] representation of this constraint
	 *
	 * @return
	 */
	public byte[] getBytes();

	/**
	 * Returns the type of the constraint based on the way to represent the
	 * information. It can return any of the following
	 * constants:CONSTRAINT_TYPE_BOOLEAN, CONSTRAINT_TYPE_CHOICE,
	 * CONSTRAINT_TYPE_STRING_LITERAL, CONSTRAINT_TYPE_INTEGER_LITERAL,
	 * CONSTRAINT_TYPE_OTHER
	 *
	 * @return
	 */
	public int getType();

	/**
	 * Returns the available choices for a CONSTRAINT_TYPE_CHOICE constraint.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this constraint is not of type CONSTRAINT_TYPE_CHOICE
	 */
	public String[] getChoiceStrings() throws UnsupportedOperationException;

	/**
	 * Returns the code of the available choices for a CONSTRAINT_TYPE_CHOICE
	 * constraint.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this constraint is not of type CONSTRAINT_TYPE_CHOICE
	 */
	public int[] getChoiceCodes() throws UnsupportedOperationException;

	/**
	 * Gets a human readable representation of the value of this Constraint
	 *
	 * @return
	 */
	public String getConstraintHumanValue();
}