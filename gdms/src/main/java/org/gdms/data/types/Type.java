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
 * Interface that represent the type of a field
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

	int NULL = -1;
	int COLLECTION = 16384;

	/**
	 * Returns the array of the constraints this type has
	 *
	 * @return the constraints
	 */
	public abstract Constraint[] getConstraints();

	/**
	 * Gets the code of this type. Must be one of the constants in this
	 * interface
	 *
	 * @return the typeCode
	 */
	public abstract int getTypeCode();

	/**
	 * Checks thatif the specified value is suitable for this type taking into
	 * account all the constraints
	 *
	 * @param value
	 * @return
	 */
	public abstract String check(final Value value);

	/**
	 * Gets the value of the specified constraint
	 *
	 * @param constraintNames
	 * @return
	 */
	public String getConstraintValue(int constraint);

	/**
	 * returns true if the field can be removed, false otherwise
	 *
	 * @return
	 */
	public abstract boolean isRemovable();

	/**
	 * Gets the specified constraint
	 *
	 * @param constraintNames
	 * @return
	 */
	public Constraint getConstraint(int constraint);

	/**
	 * Gets the value of a constraint as an int value
	 *
	 * @param constraint
	 * @return the value of the constraint or -1 if the constraint doesn't exist
	 * @throws IllegalArgumentException
	 *             if the constraint cannot be expressed as an integer value
	 */
	public abstract int getIntConstraint(int constraint);

	/**
	 * Gets the value of a constraint as boolean value
	 *
	 * @param constraint
	 * @return the value of the constraint or false if the constraint doesn't
	 *         exist
	 */
	public abstract boolean getBooleanConstraint(int constraint);

	/**
	 * Gets the constraints in the type filtered by a bit mask. The constants
	 * used in the mask are those from {@link Constraint}
	 *
	 * @param constraintMask
	 * @return
	 */
	public abstract Constraint[] getConstraints(int constraintMask);
}