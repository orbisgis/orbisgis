/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.values;

import java.io.Serializable;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Wrapper for boolean
 * Ir provides some conveniency methods for comparison between two booleans.
 * @author Fernando Gonzalez Cortes
 */
class BooleanValue extends AbstractValue implements Serializable {
        /**
         * Test if this&gt;value. It's true if and only if this is true and value is false.
         * @param value
         *          a Value
         * @return 
         *          a BooleanValue as Value : true if this&gt;value
         * @throws IncompatibleTypesException
         *          if value is not an instance of BooleanValue
         */
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
        /**
         * Test if this&gt;=value. True if and only if this is true or valueis false.
         * @param value
         * @return 
         *              a BooleanValue : true if this&gt;=value
         * @throws IncompatibleTypesException if value is not an instance of BooleanValue
         */
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
        /**
         * Test if this &lt; value. True if and only if ths is false and value is true.
         * @param value
         * @return
         *          a BooleanValue : true if this&lt;value
         * @throws IncompatibleTypesException
         *          if value is not an instance of BooleanValue
         */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory.createValue(((BooleanValue) value).value
				&& !(this.value));
	}
        /**
         * Test if this &lt;= value. True if and only if this is false or value is true.
         * @param value
         * @return
         *          a BooleanValue : true if this&lt;=value
         * @throws IncompatibleTypesException
         *          if value is not an instance of BooleanValue
         */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory
				.createValue(!(!((BooleanValue) value).value && this.value));
	}

	private boolean value;

	/**
	 * Creates a new BooleanValue object.
	 * 
	 * @param value
	 *            boolean vlue which will be contained in this object
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
	 * Test if this and values contain the same boolean value.
	 * 
	 * @param value
	 *            The value to be compared to.
	 * 
	 * @return 
	 *             A BooleanValue which contains true if this and value  contains the same boolean value.
	 * @throws IncompatibleTypesException
	 *             If value is not an instance of BooleanValue.
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value == ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
		}
	}

	/**
	 * Test if this and value are not equal.
	 * 
	 * @param value
	 *         Another BooleanValue
	 * 
	 * @return 
	 *             A BooleanValue that contains true if and only if this and value are different.
	 * @throws IncompatibleTypesException
	 *             If value is not an instance of BooleanValue.
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value != ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
		}
	}

	/**
	 * Set the boolean stored in this object.
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

	/**
	 * Get the boolean stored in this object.
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
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
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
			throw new IncompatibleTypesException(
					"The specified value is not a boolean:"
							+ TypeFactory.getTypeName(value.getType()));
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
        /**
         * Get the boolean value as a byte.
         * @return A  byte table that contain one value :  one if ths is true, 0 if this is false.
         */
	public byte[] getBytes() {
		return new byte[] { (value) ? (byte) 1 : 0 };
	}
        /**
         * Create a new BooleanValue by testing the first element of buffer.
         * If it equals 1, the new Value will be true, falser either
         * @param buffer
         * @return
         *          a BooleanValue.
         */
	public static Value readBytes(byte[] buffer) {
		return new BooleanValue(buffer[0] == 1);
	}
        /**
         *
         * @return
         *          The boolean value stored in this object.
         * @throws IncompatibleTypesException
         */
	@Override
	public boolean getAsBoolean() throws IncompatibleTypesException {
		return value;
	}
        /**
         * Try to cast this Value to another value type. The only type codes possible here
         * are BOOLEAN and STRING
         * @param typeCode
         *          Must be {@link org.gdms.data.types.Type#BOOLEAN} or {@link org.gdms.data.types.Type#STRING}
         * @return
         *          the new Value.
         * @throws IncompatibleTypesException
         *          if typeCode is not {@link org.gdms.data.types.Type#BOOLEAN} or {@link org.gdms.data.types.Type#STRING}
         */
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