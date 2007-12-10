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
package org.gdms.data.values;

import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Datatypes must implement this interface in order to the drivers to return
 * that datatype. The implementation can inherit from AbstractValue or must
 * implement equals and hashCode in the way explained at doEquals method javadoc
 */
public interface Value {
	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#and(com.hardcode.gdbms.engine.values.value);
	 */
	public Value and(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#or(com.hardcode.gdbms.engine.values.value);
	 */
	public Value or(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#producto(com.hardcode.gdbms.engine.values.value);
	 */
	public Value producto(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#suma(com.hardcode.gdbms.engine.values.value);
	 */
	public Value suma(Value value) throws IncompatibleTypesException;

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value inversa() throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#equals(org.gdms.data.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#notEquals(org.gdms.data.values.Value)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greater(org.gdms.data.values.Value)
	 */
	public Value greater(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#less(org.gdms.data.values.Value)
	 */
	public Value less(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greaterEqual(org.gdms.data.values.Value)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#lessEqual(org.gdms.data.values.Value)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.data.values.Operations#like(org.gdms.data.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException;

	/**
	 * In order to index the tables equals and hashCode must be defined.
	 * AbstractValue overrides these methods by calling doEquals and doHashCode.
	 * Any Value must inherit from abstract Value or override those methods in
	 * the same way.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean doEquals(Object obj);

	/**
	 * The hashCode implementation. Every value with the same semantic
	 * information must return the same int
	 *
	 * @return integer
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int doHashCode();

	/**
	 * Gets the string representation of the value as it is defined in the
	 * specified ValueWriter
	 *
	 * @param writer
	 *            Specifies the string representation for the values
	 *
	 * @return String
	 */
	public String getStringValue(ValueWriter writer);

	/**
	 * Gets the type of the value
	 *
	 * @return integer
	 */
	public int getType();

	/**
	 * Gets this value as an array of bytes
	 *
	 * @return
	 */
	public byte[] getBytes();

	/**
	 * Tries to transform this value as a value of the specified type
	 *
	 * @param typeCode
	 * @return
	 */
	public Value toType(int typeCode) throws IncompatibleTypesException;
}