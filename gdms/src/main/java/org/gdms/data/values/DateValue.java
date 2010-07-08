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
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Wrapper for dates
 *
 * @author Fernando Gonzalez Cortes
 */
class DateValue extends AbstractValue implements Serializable {
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private Date value;

	private static SimpleDateFormat simpleDateFormat;

	/**
	 * Creates a new DateValue object.
	 *
	 * @param d
	 *            Date to set
	 */
	DateValue(Date d) {
		value = d;
	}

	/**
	 * Creates a new DateValue
	 */
	DateValue() {
	}

	DateValue(String text) throws ParseException {
		SimpleDateFormat sdf = getDateFormat();
		value = new Date(sdf.parse(text).getTime());
	}

	/**
	 * Set the stored datvalue.
	 *
	 * @param d
	 *            The new date
	 */
	public void setValue(java.util.Date d) {
		value = new Date(d.getTime());
	}

	/**
	 * @see org.gdms.data.values.Operations#equals(org.gdms.data.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.equals(getDate(value)));
	}

	/**
	 * @see org.gdms.data.values.Operations#greater(org.gdms.data.values.DateValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(getDate(value)) > 0);
	}

	private Date getDate(Value value) throws IncompatibleTypesException {
		if (value instanceof DateValue) {
			return ((DateValue) value).value;
		} else if (value instanceof StringValue) {
			String str = ((StringValue) value).getValue();
			try {
				return new Date(getDateFormat().parse(str).getTime());
			} catch (ParseException e) {
				throw new IncompatibleTypesException(
						"The specified value is not a date:"
								+ TypeFactory.getTypeName(value.getType()));
			}
		} else {
			throw new IncompatibleTypesException(
					"The specified value is not a date:"
							+ TypeFactory.getTypeName(value.getType()));
		}
	}

	private SimpleDateFormat getDateFormat() {
		if (simpleDateFormat == null) {
			simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			simpleDateFormat.setLenient(false);
		}
		return simpleDateFormat;
	}

	/**
	 * @see org.gdms.data.values.Operations#greaterEqual(org.gdms.data.values.DateValue)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(getDate(value)) >= 0);
	}

	/**
	 * @see org.gdms.data.values.Operations#less(org.gdms.data.values.DateValue)
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(getDate(value)) < 0);
	}

	/**
	 * @see org.gdms.data.values.Operations#lessEqual(org.gdms.data.values.DateValue)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(getDate(value)) <= 0);
	}

	/**
	 * @see org.gdms.data.values.Operations#notEquals(org.gdms.data.values.DateValue)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(!this.value.equals(getDate(value)));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		return getDateFormat().format(value);
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return value.hashCode();
	}

	public Date getValue() {
		return value;
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.DATE;
	}
        /**
         * Puts the date in an array of bytes.
         * @return
         *          the date, put in a long value, and then in an array of bytes.
         */

	public byte[] getBytes() {
		long v = value.getTime();
		byte[] ret = LongValue.getBytes(v);

		return ret;
	}
        /**
         * Create a new DateValue, give a array of bytes.
         * @param buffer
         *      The array to be read
         * @return
         *      a new DateValue
         */
	public static Value readBytes(byte[] buffer) {
		return new DateValue(new Date(LongValue.getLong(buffer)));
	}
        /**
         *
         * @return
         *      the {@link java.util.Date} stored in this object.
         * @throws IncompatibleTypesException
         */
	@Override
	public java.util.Date getAsDate() throws IncompatibleTypesException {
		return value;
	}
        /**
         * Try to cast ths Objet to anther type.Valid {@link org.gdms.data.types.Type}s are
         * {@link org.gdms.data.types.Type#DATE}, {@link org.gdms.data.types.Type#TIME}, {@link org.gdms.data.types.Type#TIMESTAMP}
         * and {@link org.gdms.data.types.Type#STRING}
         * @param typeCode
         * @return
         * @throws IncompatibleTypesException
         */
	@Override
	public Value toType(int typeCode) throws IncompatibleTypesException {
		switch (typeCode) {
		case Type.DATE:
			return this;
		case Type.TIME:
			return ValueFactory.createValue(new Time(value.getTime()));
		case Type.TIMESTAMP:
			return ValueFactory.createValue(new Timestamp(value.getTime()));
		case Type.STRING:
			return ValueFactory.createValue(toString());
		}
		throw new IncompatibleTypesException("Cannot cast to type: " + typeCode);
	}
}