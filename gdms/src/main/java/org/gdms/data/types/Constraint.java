/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
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
}