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
import java.sql.Types;
import java.text.ParseException;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;

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

	@Override
	public String getAsString() throws IncompatibleTypesException {
		return value;
	}

	@Override
	public Value toType(int typeCode) throws IncompatibleTypesException {
		try {
			Value ret = ValueFactory.createValueByType(value, typeCode);
			if (ret.getType() == typeCode) {
				return ret;
			} else {
				throw new IncompatibleTypesException(
						"Cannot convert string to "
								+ TypeFactory.getTypeName(typeCode) + ": "
								+ value);
			}
		} catch (NumberFormatException e) {
			throw new IncompatibleTypesException("Cannot convert value to "
					+ TypeFactory.getTypeName(typeCode) + ": " + value, e);
		} catch (ParseException e) {
			throw new IncompatibleTypesException("Cannot convert value to "
					+ TypeFactory.getTypeName(typeCode) + ": " + value, e);
		}
	}
}