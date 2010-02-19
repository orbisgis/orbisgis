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
package org.gdms.data.values;

import java.io.Serializable;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonzalez Cortes
 */
class NullValue extends AbstractValue implements Serializable {
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		return "";
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return 0;
	}

	@Override
	public Value producto(Value value) throws IncompatibleTypesException {
		return ValueFactory.createNullValue();
	}

	@Override
	public Value suma(Value value) throws IncompatibleTypesException {
		return ValueFactory.createNullValue();
	}

	public Value and(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value equals(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value greater(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value less(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value lessEqual(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value like(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value notEquals(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	public Value or(Value value) throws IncompatibleTypesException {
		return ValueFactory.createValue(false);
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getNullStatementString();
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.NULL;
	}

	public byte[] getBytes() {
		return new byte[0];
	}

	@Override
	public boolean isNull() {
		return true;
	}
}