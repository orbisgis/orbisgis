package org.gdms.data.values;

import java.io.Serializable;
import java.sql.Types;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo de datos String
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class StringValue extends AbstractValue implements Serializable {
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
}