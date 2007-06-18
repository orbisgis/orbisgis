package org.gdms.data.values;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo Date
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DateValue extends AbstractValue implements Serializable {
	private Date value;

	/**
	 * Creates a new DateValue object.
	 *
	 * @param d
	 *            Data to set
	 */
	DateValue(Date d) {
		value = d;
	}

	/**
	 *
	 */
	public DateValue() {
	}

	/**
	 * Establece el valor
	 *
	 * @param d
	 *            valor
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

		if (value instanceof DateValue) {
			return new BooleanValue(this.value
					.equals(((DateValue) value).value));
		} else {
			throw new IncompatibleTypesException(value.getClass().getName());
		}
	}

	/**
	 * @see org.gdms.data.values.Operations#greater(org.gdms.data.values.DateValue)
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof DateValue) {
			return new BooleanValue(this.value
					.compareTo(((DateValue) value).value) > 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(this.value
					.compareTo(((DateValue) value).value) >= 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(this.value
					.compareTo(((DateValue) value).value) < 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(this.value
					.compareTo(((DateValue) value).value) <= 0);
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

		if (value instanceof DateValue) {
			return new BooleanValue(!this.value
					.equals(((DateValue) value).value));
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
		return DateFormat.getDateInstance(DateFormat.SHORT).format(value);
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
	 * @see org.gdms.data.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
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
}