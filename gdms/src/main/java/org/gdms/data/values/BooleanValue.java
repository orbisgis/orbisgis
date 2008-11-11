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
 * Wrapper for boolean
 * 
 * @author Fernando Gonzalez Cortes
 */
class BooleanValue extends AbstractValue implements Serializable {
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue((!((BooleanValue) value).value)
				&& this.value);
	}

	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory
				.createValue(!(((BooleanValue) value).value && !this.value));
	}

	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(((BooleanValue) value).value
				&& !(this.value));
	}

	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory
				.createValue(!(!((BooleanValue) value).value && this.value));
	}

	private boolean value;

	/**
	 * Creates a new BooleanValue object.
	 * 
	 * @param value
	 *            Valor booleano que tendr� este objeto
	 */
	BooleanValue(boolean value) {
		this.value = value;
	}

	/**
	 * Creates a new BooleanValue object.
	 */
	BooleanValue() {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value == ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value != ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * Establece el valor de este objeto
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 * 
	 * @return
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#and(org.gdms.sql.instruction.BooleanValue)
	 */
	public Value and(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			Value ret = ValueFactory.createValue(this.value
					&& ((BooleanValue) value).getValue());

			return ret;
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#and(org.gdms.sql.instruction.BooleanValue)
	 */
	public Value or(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			Value ret = ValueFactory.createValue(this.value
					|| ((BooleanValue) value).getValue());

			return ret;
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return Boolean.valueOf(value).hashCode();
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value);
	}

	/**
	 * @see org.gdms.data.values.Value#inversa()
	 */
	public Value inversa() throws IncompatibleTypesException {
		return ValueFactory.createValue(!value);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.BOOLEAN;
	}

	public byte[] getBytes() {
		return new byte[] { (value) ? (byte) 1 : 0 };
	}

	public static Value readBytes(byte[] buffer) {
		return new BooleanValue(buffer[0] == 1);
	}

	@Override
	public boolean getAsBoolean() throws IncompatibleTypesException {
		return value;
	}

	@Override
	public Value toType(int typeCode) throws IncompatibleTypesException {
		switch (typeCode) {
		case Type.BOOLEAN:
			return this;
		case Type.STRING:
			return ValueFactory.createValue(Boolean.toString(value));
		}
		throw new IncompatibleTypesException("Cannot cast to type: " + typeCode);
	}

}