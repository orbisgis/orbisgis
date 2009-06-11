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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * @author Fernando Gonzalez Cortes
 */
abstract class NumericValue extends AbstractValue implements Serializable {

	public abstract byte byteValue();

	public abstract short shortValue();

	public abstract int intValue();

	public abstract long longValue();

	public abstract float floatValue();

	public abstract double doubleValue();

	/**
	 * Returns the number of digits after the decimal point
	 * 
	 * @return
	 */
	public abstract int getDecimalDigitsCount();

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
	public Value producto(Value value) throws IncompatibleTypesException {
		if (value.isNull()) {
			return ValueFactory.createNullValue();
		} else {
			if (!(value instanceof NumericValue)) {
				throw new IncompatibleTypesException();
			}

			return ValueFactory.producto(this, (NumericValue) value);
		}
	}

	public Value suma(Value value) throws IncompatibleTypesException {
		if (value.isNull()) {
			return ValueFactory.createNullValue();
		} else {
			if (!(value instanceof NumericValue)) {
				throw new IncompatibleTypesException();
			}

			return ValueFactory.suma(this, (NumericValue) value);
		}
	}

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
			throw new IncompatibleTypesException();
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
			throw new IncompatibleTypesException();
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
			throw new IncompatibleTypesException();
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
			throw new IncompatibleTypesException();
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
