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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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

public interface Type {
	public static final int BINARY = 0;
	public static final int BOOLEAN = 1;
	public static final int BYTE = 2;
	public static final int DATE = 3;
	public static final int DOUBLE = 4;
	public static final int FLOAT = 5;
	public static final int INT = 6;
	public static final int LONG = 7;
	public static final int SHORT = 8;
	public static final int STRING = 9;
	public static final int TIMESTAMP = 10;
	public static final int TIME = 11;
	public static final int GEOMETRY = 30000;
	
	public static final int NULL = Integer.MIN_VALUE;
	public static final int COLLECTION = Integer.MAX_VALUE;

	/**
	 * @return the constraints
	 */
	public abstract Constraint[] getConstraints();

	/**
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * @return the typeCode
	 */
	public abstract int getTypeCode();

	public abstract String check(final Value value);

	public String getConstraintValue(final ConstraintNames constraintNames);

	public abstract boolean isRemovable();

	public Constraint getConstraint(final ConstraintNames constraintNames);
	
	// public abstract boolean hasConstraint(ConstraintNames constraintNames);
	// public abstract boolean isaPrimaryKeyField();
	// public abstract boolean isaUniqueField();
	// public abstract boolean isaReadOnlyField();
}