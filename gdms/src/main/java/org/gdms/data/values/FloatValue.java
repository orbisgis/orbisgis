package org.gdms.data.values;

import java.sql.Types;
import java.text.NumberFormat;



/**
 * Wrapper sobre la clase float
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FloatValue extends NumericValue {
	private float value;

	/**
	 * Establece el valor de este objeto
	 *
	 * @param value
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 *
	 * @return
	 */
	public float getValue() {
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
		return (double) value;
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
     * @see org.gdms.data.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
     */
    public String getStringValue(ValueWriter writer) {
        return writer.getStatementString(value, Types.REAL);
    }

    /**
     * @see org.gdms.data.values.Value#getType()
     */
    public int getType() {
        return Value.FLOAT;
    }

    @Override
    public int getDecimalDigitsCount() {
        String str = Float.toString(value);
        if (str.endsWith(".0")) {
            return 0;
        }
        return str.length() - (str.indexOf(".")+1);
    }
}
