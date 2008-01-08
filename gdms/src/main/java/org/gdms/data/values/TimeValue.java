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

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper for times
 *
 * @author Fernando Gonzalez Cortes
 */
class TimeValue extends AbstractValue implements Serializable {
	private Time value;

	/**
	 * Creates a new DateValue object.
	 *
	 * @param d
	 *            DOCUMENT ME!
	 */
	TimeValue(Time d) {
		value = d;
	}

	/**
	 * Creates a new DateValue object.
	 */
	TimeValue() {
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
		return DateFormat.getTimeInstance().format(value);
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

	public Value toType(int typeCode) throws IncompatibleTypesException {
		switch (typeCode) {
		case Type.TIME:
			return this;
		case Type.TIMESTAMP:
			return ValueFactory.createValue(new Timestamp(value.getTime()));
		case Type.STRING:
			return ValueFactory.createValue(toString());
		}
		throw new IncompatibleTypesException("Cannot cast to type: " + typeCode);
	}

	@Override
	public Time getAsTime() throws IncompatibleTypesException {
		return value;
	}
}