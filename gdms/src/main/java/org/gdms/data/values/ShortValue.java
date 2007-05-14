package org.gdms.data.values;

import java.sql.Types;




/**
 *
 */
public class ShortValue extends NumericValue {
	private short value;

	/**
	 * Crea un nuevo ShortValue.
	 *
	 * @param s DOCUMENT ME!
	 */
	ShortValue(short s) {
		value = s;
	}

	/**
	 * Crea un nuevo ShortValue.
	 */
	ShortValue() {
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
		return value;
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
     * @see org.gdms.data.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
     */
    public String getStringValue(ValueWriter writer) {
        return writer.getStatementString(value, Types.SMALLINT);
    }

    /**
     * @see org.gdms.data.values.Value#getType()
     */
    public int getType() {
        return Value.SHORT;
    }

    @Override
    public int getDecimalDigitsCount() {
        return 0;
    }

	public short getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
	}

}
