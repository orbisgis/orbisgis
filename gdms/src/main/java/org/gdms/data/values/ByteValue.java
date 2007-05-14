package org.gdms.data.values;

import java.sql.Types;




/**
 *
 */
public class ByteValue extends NumericValue {
	private byte value;

	/**
	 * Crea un nuevo ByteValue.
	 *
	 * @param value DOCUMENT ME!
	 */
	ByteValue(byte value) {
		this.value = value;
	}

	/**
	 * Crea un nuevo ByteValue.
	 */
	ByteValue() {
	}

	/**
	 * @see org.gdms.data.values.NumericValue#byteValue()
	 */
	public byte byteValue() {
		return value;
	}

	/**
	 * @see org.gdms.data.values.NumericValue#shortValue()
	 */
	public short shortValue() {
		return (short) value;
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
        return writer.getStatementString(value, Types.TINYINT);
    }

    /**
     * @see org.gdms.data.values.Value#getType()
     */
    public int getType() {
        return Value.BYTE;
    }

    @Override
    public int getDecimalDigitsCount() {
        return 0;
    }

	public byte getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
	}

}
