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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Wrapper for times
 * 
 * @author Fernando Gonzalez Cortes
 */
class TimeValue extends AbstractValue implements Serializable {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private Time value;

	/**
	 * Creates a new TimeValue object.
	 * 
	 * @param d
	 *            DOCUMENT ME!
	 */
	TimeValue(Time d) {
		value = d;
	}

	/**
	 * Creates a new TimeValue object.
	 */
	TimeValue() {
	}

	public TimeValue(String text) throws ParseException {
		SimpleDateFormat sdf = getDateFormat();
		value = new Time(sdf.parse(text).getTime());
	}

	private SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(TIME_FORMAT);
	}

	/**
	 * Establece el valor
	 * 
	 * @param d
	 *            valor
	 */
	public void setValue(Time d) {
		value = d;
	}

	/**
	 * @see org.gdms.data.values.Operations#equals(org.gdms.data.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value
					.equals(((TimeValue) value).value));
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#greater(org.gdms.data.values.DateValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value
					.compareTo(((TimeValue) value).value) > 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#greaterEqual(org.gdms.data.values.DateValue)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value
					.compareTo(((TimeValue) value).value) >= 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#less(org.gdms.data.values.DateValue)
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value
					.compareTo(((TimeValue) value).value) < 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#lessEqual(org.gdms.data.values.DateValue)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(this.value
					.compareTo(((TimeValue) value).value) <= 0);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#notEquals(org.gdms.data.values.DateValue)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimeValue) {
			return new BooleanValue(!this.value
					.equals(((TimeValue) value).value));
		} else {
			throw new IncompatibleTypesException();
		}
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

	/**
	 * @return
	 */
	public Time getValue() {
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
		return Type.TIME;
	}

	public byte[] getBytes() {
		return LongValue.getBytes(value.getTime());
	}

	public static Value readBytes(byte[] buffer) {
		return new TimeValue(new Time(LongValue.getLong(buffer)));
	}

	@Override
	public Time getAsTime() throws IncompatibleTypesException {
		return value;
	}

	@Override
	public Value toType(int typeCode) throws IncompatibleTypesException {
		switch (typeCode) {
		case Type.DATE:
			return ValueFactory.createValue(new Date(value.getTime()));
		case Type.TIME:
			return this;
		case Type.TIMESTAMP:
			return ValueFactory.createValue(new Timestamp(value.getTime()));
		case Type.STRING:
			return ValueFactory.createValue(toString());
		}
		throw new IncompatibleTypesException("Cannot cast to type: " + typeCode);
	}

}