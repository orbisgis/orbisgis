package org.gdms.data.values;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper sobre el tipo Date
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DateValue extends AbstractValue implements Serializable {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
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

	DateValue(String text) throws ParseException {
		SimpleDateFormat sdf = getDateFormat();
		value = new Date(sdf.parse(text).getTime());
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
				return new Date(getDateFormat().parse(str)
						.getTime());
			} catch (ParseException e) {
				throw new IncompatibleTypesException(e);
			}
		} else {
			throw new IncompatibleTypesException();
		}
	}

	private SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT);
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
}