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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Wrapper for strings
 *
 * @author Fernando Gonzalez Cortes
 */
class StringValue extends AbstractValue implements Serializable {
	private String value;

	/**
	 * Construye un objeto StringValue con el texto que se pasa como parametro
	 *
	 * @param text
	 */
	StringValue(String text) {
		this.value = text;
	}

	/**
	 * Creates a new StringValue object.
	 */
	StringValue() {
	}

	/**
	 * Establece el valor de este objeto
	 *
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 *
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @see org.gdms.sql.instruction.expression.Operations#suma(org.gdms.sql.instruction.expression.Value)
	 */
	public Value suma(Value v) throws IncompatibleTypesException {
		if (v instanceof IntValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((IntValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof LongValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((LongValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof FloatValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((FloatValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof DoubleValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ ((DoubleValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof StringValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						+ Double.parseDouble(((StringValue) v).getValue()));

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.expression.Operations#producto(org.gdms.sql.instruction.expression.Value)
	 */
	public Value producto(Value v) throws IncompatibleTypesException {
		if (v instanceof IntValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((IntValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof LongValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((LongValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof FloatValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((FloatValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof DoubleValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* ((DoubleValue) v).getValue());

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else if (v instanceof StringValue) {
			try {
				DoubleValue ret = new DoubleValue();
				ret.setValue(Double.parseDouble(this.value)
						* Double.parseDouble(((StringValue) v).getValue()));

				return ret;
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException(getValue()
						+ " is not a number");
			}
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value;
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#equals(org.gdms.data.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return ValueFactory.createValue(this.value.equals(value.toString()));
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#greater(org.gdms.data.values.BooleanValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) > 0);
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#greaterEqual(org.gdms.data.values.BooleanValue)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) >= 0);
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#less(org.gdms.data.values.BooleanValue)
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) < 0);
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#lessEqual(org.gdms.data.values.BooleanValue)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(this.value.compareTo(value.toString()) <= 0);
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#notEquals(org.gdms.data.values.BooleanValue)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		return new BooleanValue(!this.value.equals(value.toString()));
	}

	/**
	 * @see org.gdms.data.values.Operations#like(org.gdms.data.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof StringValue) {
			String pattern = ((StringValue) value).getValue().replaceAll("%",
					".*");
			pattern = pattern.replaceAll("\\?", ".");

			return new BooleanValue(this.value.matches(pattern));
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return value.hashCode();
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value, Types.VARCHAR);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.STRING;
	}

	public byte[] getBytes() {
		return value.getBytes();
	}

	public static Value readBytes(byte[] buffer) {
		return new StringValue(new String(buffer));
	}

	public Value toType(int typeCode) throws IncompatibleTypesException {
		byte b;
		short s;
		int i;
		long l;
		float f;
		double d;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
		switch (typeCode) {
		case Type.BINARY:
			throw new IncompatibleTypesException("binary literal expected");
		case Type.BOOLEAN:
			if (value.trim().equalsIgnoreCase("false")) {
				return ValueFactory.createValue(false);
			} else if (value.trim().equalsIgnoreCase("true")) {
				return ValueFactory.createValue(true);
			} else {
				throw new IncompatibleTypesException("boolean literal expected");
			}
		case Type.BYTE:
			try {
				b = Byte.parseByte(value);
				s = Short.parseShort(value);
				if (b == s) {
					return ValueFactory.createValue(b);
				}
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("byte literal expected");
			}
		case Type.SHORT:
			try {
				s = Short.parseShort(value);
				i = Integer.parseInt(value);
				if (i == s) {
					return ValueFactory.createValue(s);
				}
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("short literal expected");
			}
		case Type.INT:
			try {
				i = Integer.parseInt(value);
				l = Long.parseLong(value);
				if (i == l) {
					return ValueFactory.createValue(i);
				}
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("integer literal expected");
			}
		case Type.LONG:
			try {
				l = Long.parseLong(value);
				return ValueFactory.createValue(l);
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("long literal expected");
			}
		case Type.DATE:
			try {
				ValueFactory.createValue(sdf.parse(value));
			} catch (ParseException e) {
				throw new IncompatibleTypesException(
						"date literal expected (yyyy-MM-dd)");
			}
		case Type.TIME:
			try {
				ValueFactory
						.createValue(new Time(stf.parse(value).getTime()));
			} catch (ParseException e) {
				throw new IncompatibleTypesException(
						"time literal expected (HH:mm:ss)");
			}
		case Type.TIMESTAMP:
			try {
				ValueFactory.createValue(Timestamp.valueOf(value));
			} catch (IllegalArgumentException e) {
				throw new IncompatibleTypesException(
						"timestamp literal expected (HH:mm:ss)");
			}
		case Type.COLLECTION:
			throw new IncompatibleTypesException("collection literal expected");
		case Type.FLOAT:
			try {
				f = Float.parseFloat(value);
				d = Double.parseDouble(value);
				if (f == d) {
					return ValueFactory.createValue(f);
				}
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("float literal expected");
			}
		case Type.DOUBLE:
			try {
				d = Double.parseDouble(value);
				return ValueFactory.createValue(d);
			} catch (NumberFormatException e) {
				throw new IncompatibleTypesException("double literal expected");
			}
		case Type.GEOMETRY:
			try {
				Geometry g = new WKTReader().read(value);
				return ValueFactory.createValue(g);
			} catch (com.vividsolutions.jts.io.ParseException e) {
				throw new IncompatibleTypesException(
						"geometry literal expected");
			}
		}
		return ValueFactory.createValue(value);
	}

	@Override
	public String getAsString() throws IncompatibleTypesException {
		return value;
	}
}