package org.gdms.data.values;

import java.sql.Types;

import org.gdms.data.types.Type;

/**
 * Wrapper sobre el tipo int
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class IntValue extends NumericValue {
	private int value;

	/**
	 * Creates a new IntValue object.
	 *
	 * @param value
	 *            DOCUMENT ME!
	 */
	IntValue(int value) {
		this.value = value;
	}

	/**
	 * Creates a new IntValue object.
	 */
	IntValue() {
	}

	/**
	 * Establece el valor de este objeto
	 *
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 *
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
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
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value, Types.INTEGER);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.INT;
	}

	@Override
	public int getDecimalDigitsCount() {
		return 0;
	}

	public static byte[] getBytes(int v) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((v >>> 24) & 0x000FF);
		ret[1] = (byte) ((v >>> 16) & 0x000FF);
		ret[2] = (byte) ((v >>> 8) & 0x000FF);
		ret[3] = (byte) ((v >>> 0) & 0x000FF);

		return ret;
	}

	public byte[] getBytes() {
		return getBytes(value);
	}

	public static Value readBytes(byte[] buffer) {
		return new IntValue(getInt(buffer));
	}

	public static int getInt(byte[] buffer) {
		return ((0xFF & buffer[0]) << 24) + ((0xFF & buffer[1]) << 16)
				+ ((0xFF & buffer[2]) << 8) + ((0xFF & buffer[3]) << 0);
	}
}