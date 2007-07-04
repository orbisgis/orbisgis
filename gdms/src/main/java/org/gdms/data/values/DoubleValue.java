package org.gdms.data.values;

import java.sql.Types;
import java.text.NumberFormat;

import org.gdms.data.types.Type;

/**
 * Wrapper sobre el valor double
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DoubleValue extends NumericValue {
	private double value;

	/**
	 * Creates a new DoubleValue object.
	 * 
	 * @param val
	 *            DOCUMENT ME!
	 */
	DoubleValue(double val) {
		value = val;
	}

	/**
	 * Creates a new DoubleValue object.
	 */
	DoubleValue() {
	}

	/**
	 * Establece el valor de este objeto
	 * 
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 * 
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return NumberFormat.getNumberInstance().format(value);
	}

	/**
	 * @see org.gdms.data.values.NumericValue#intValue()
	 */
	public int intValue() {
		return (int) value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#longValue()
	 */
	public long longValue() {
		return (long) value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#floatValue()
	 */
	public float floatValue() {
		return (float) value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#doubleValue()
	 */
	public double doubleValue() {
		return value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#byteValue()
	 */
	public byte byteValue() {
		return (byte) value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#shortValue()
	 */
	public short shortValue() {
		return (short) value;
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value, Types.DOUBLE);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.DOUBLE;
	}

	@Override
	public int getDecimalDigitsCount() {
		String str = Double.toString(value);
		if (str.endsWith(".0")) {
			return 0;
		}
		return str.length() - (str.indexOf(".") + 1);
	}
}