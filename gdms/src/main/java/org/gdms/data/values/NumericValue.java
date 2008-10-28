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

import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
abstract class NumericValue extends AbstractValue implements Serializable {
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract byte byteValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract short shortValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract int intValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract long longValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public abstract float floatValue();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
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
		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.producto(this, (NumericValue) value);
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
	public Value suma(Value value) throws IncompatibleTypesException {
		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.suma(this, (NumericValue) value);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value inversa() throws IncompatibleTypesException {
		return ValueFactory.inversa(this);
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

		if (!(value instanceof NumericValue)) {
			throw new IncompatibleTypesException("Type:" + value.getType());
		}

		return ValueFactory
				.createValue(this.doubleValue() == ((NumericValue) value)
						.doubleValue());
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

}
