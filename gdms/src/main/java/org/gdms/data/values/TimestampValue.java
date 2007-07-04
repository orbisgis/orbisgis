package org.gdms.data.values;

import java.io.Serializable;
import java.sql.Timestamp;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo Date
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TimestampValue extends AbstractValue implements Serializable {
	private Timestamp value;

	/**
	 * Creates a new DateValue object.
	 * 
	 * @param d
	 *            DOCUMENT ME!
	 */
	TimestampValue(Timestamp d) {
		value = d;
	}

	/**
	 * Creates a new DateValue object.
	 */
	TimestampValue() {
	}

	/**
	 * Establece el valor
	 * 
	 * @param d
	 *            valor
	 */
	public void setValue(Timestamp d) {
		value = d;
	}

	/**
	 * @see org.gdms.data.values.Operations#equals(org.gdms.data.values.DateValue)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof TimestampValue) {
			return new BooleanValue(this.value
					.equals(((TimestampValue) value).value));
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

		if (value instanceof TimestampValue) {
			return new BooleanValue(this.value
					.compareTo(((TimestampValue) value).value) > 0);
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

		if (value instanceof TimestampValue) {
			return new BooleanValue(this.value
					.compareTo(((TimestampValue) value).value) >= 0);
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

		if (value instanceof TimestampValue) {
			return new BooleanValue(this.value
					.compareTo(((TimestampValue) value).value) < 0);
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

		if (value instanceof TimestampValue) {
			return new BooleanValue(this.value
					.compareTo(((TimestampValue) value).value) <= 0);
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

		if (value instanceof TimestampValue) {
			return new BooleanValue(!this.value
					.equals(((TimestampValue) value).value));
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
		return value.toString();
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
	public Timestamp getValue() {
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
		return Type.TIMESTAMP;
	}
}