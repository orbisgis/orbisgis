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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Abstract class which gives a common basis to all the numeric values.
 * @author Fernando Gonzalez Cortes
 */
abstract class NumericValue extends AbstractValue implements Serializable {

        /**
         * Retrieve the numeric value as a byte. Abstract, must be implemented by clients.
         * @return the value as a byte.
         */
	public abstract byte byteValue();

        /**
         * Retrieve the numeric value as a short. Abstract, must be implemented by clients.
         * @return the value as a short.
         */
	public abstract short shortValue();

        /**
         * Retrieve the numeric value as an int. Abstract, must be implemented by clients.
         * @return the value as an int.
         */
	public abstract int intValue();

        /**
         * Retrieve the numeric value as a long. Abstract, must be implemented by clients.
         * @return the value as a long.
         */
	public abstract long longValue();

        /**
         * Retrieve the numeric value as a float. Abstract, must be implemented by clients.
         * @return the value as a float.
         */
	public abstract float floatValue();

        /**
         * Retrieve the numeric value as a double. Abstract, must be implemented by clients.
         * @return the value as a double.
         */
	public abstract double doubleValue();

	/**
	 * Returns the number of digits after the decimal point
	 * 
	 * @return
	 */
	public abstract int getDecimalDigitsCount();

	/**
	 * Compute the product between this and value
	 * 
	 * @param value
	 *            The value to compute the product with.
	 * 
	 * @return The result as a numeric value.
	 * 
	 * @throws IncompatibleTypesException
	 *             If value is not a numeric value.
	 */
	public Value multiply(Value value) throws IncompatibleTypesException {
		if (value.isNull()) {
			return ValueFactory.createNullValue();
		} else {
			if (!(value instanceof NumericValue)) {
				throw new IncompatibleTypesException(
						"The specified value is not a numeric:"
								+ TypeFactory.getTypeName(value.getType()));
			}

			return ValueFactory.producto(this, (NumericValue) value);
		}
	}

        /**
	 * Compute the sum between this and value
	 *
	 * @param value
	 *            The value to compute the sum with.
	 *
	 * @return The result as a numeric value.
	 *
	 * @throws IncompatibleTypesException
	 *             If value is not a numeric value.
	 */
	public Value sum(Value value) throws IncompatibleTypesException {
		if (value.isNull()) {
			return ValueFactory.createNullValue();
		} else {
			if (!(value instanceof NumericValue)) {
				throw new IncompatibleTypesException(
						"The specified value is not a numeric:"
								+ TypeFactory.getTypeName(value.getType()));
			}

			return ValueFactory.suma(this, (NumericValue) value);
		}
	}

        /**
         * Compute the inverse of this.
         * @return
         *          The inverse as a numeric value.
         * @throws IncompatibleTypesException
         */
	public Value inversa() throws IncompatibleTypesException {
		return ValueFactory.inversa(this);
	}

	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException("Type:" + value.getType());
		}

		return ValueFactory
				.createValue(this.doubleValue() == ((NumericValue) value)
						.doubleValue());
	}

	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a numeric:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory
				.createValue(this.doubleValue() > ((NumericValue) value)
						.doubleValue());
	}

	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a numeric:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory
				.createValue(this.doubleValue() >= ((NumericValue) value)
						.doubleValue());
	}

	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a numeric:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory
				.createValue(this.doubleValue() < ((NumericValue) value)
						.doubleValue());
	}

	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException(value.toString());
		}

		return ValueFactory
				.createValue(this.doubleValue() <= ((NumericValue) value)
						.doubleValue());
	}

	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException(
					"The specified value is not a numeric:"
							+ TypeFactory.getTypeName(value.getType()));
		}

		return ValueFactory
				.createValue(this.doubleValue() != ((NumericValue) value)
						.doubleValue());
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return intValue();
	}

	@Override
	public byte getAsByte() throws IncompatibleTypesException {
		return byteValue();
	}

	@Override
	public double getAsDouble() throws IncompatibleTypesException {
		return doubleValue();
	}

	@Override
	public float getAsFloat() throws IncompatibleTypesException {
		return floatValue();
	}

	@Override
	public int getAsInt() throws IncompatibleTypesException {
		return intValue();
	}

	@Override
	public long getAsLong() throws IncompatibleTypesException {
		return longValue();
	}

	@Override
	public short getAsShort() throws IncompatibleTypesException {
		return shortValue();
	}

	private boolean isDecimal(int type) {
		return (type == Type.FLOAT) || (type == Type.DOUBLE);
	}

	public Value toType(int typeCode) throws IncompatibleTypesException {
		switch (typeCode) {
		case Type.NULL:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.LONG:
		case Type.FLOAT:
		case Type.DOUBLE:
			if (!isDecimal(typeCode) && isDecimal(getType())) {
				throw new IncompatibleTypesException(
						"Cannot cast decimal to whole :"
								+ typeCode
								+ ": "
								+ getStringValue(ValueWriter.internalValueWriter));
			}
			return this;
		case Type.DATE:
			if (!isDecimal(getType())) {
				return ValueFactory.createValue(new Date(longValue()));
			} else {
				break;
			}
		case Type.STRING:
			return ValueFactory.createValue(toString());
		case Type.TIME:
			if (!isDecimal(getType())) {
				return ValueFactory.createValue(new Time(longValue()));
			} else {
				break;
			}
		case Type.TIMESTAMP:
			if (!isDecimal(getType())) {
				return ValueFactory.createValue(new Timestamp(longValue()));
			} else {
				break;
			}
		}
		throw new IncompatibleTypesException("Cannot cast to type:" + typeCode
				+ ": " + getStringValue(ValueWriter.internalValueWriter));
	}
}
