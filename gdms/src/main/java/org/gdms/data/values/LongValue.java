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

import org.gdms.data.types.Type;

/**
 * Wrapper for longs
 *
 * @author Fernando Gonzalez Cortes
 */
class LongValue extends NumericValue {
	private long value;

	/**
	 * Creates a new LongValue object.
	 *
	 * @param value
	 *            DOCUMENT ME!
	 */
	LongValue(long value) {
		this.value = value;
	}

	/**
	 * Creates a new LongValue object.
	 */
	LongValue() {
	}

	/**
	 * Establece el valor de este objeto
	 *
	 * @param value
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 *
	 * @return
	 */
	public long getValue() {
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
		return writer.getStatementString(value);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.LONG;
	}

	@Override
	public int getDecimalDigitsCount() {
		return 0;
	}

	public static byte[] getBytes(long value) {
		byte[] ret = new byte[8];
		ret[0] = (byte) (value >>> 56);
		ret[1] = (byte) (value >>> 48);
		ret[2] = (byte) (value >>> 40);
		ret[3] = (byte) (value >>> 32);
		ret[4] = (byte) (value >>> 24);
		ret[5] = (byte) (value >>> 16);
		ret[6] = (byte) (value >>> 8);
		ret[7] = (byte) (value >>> 0);
		return ret;
	}

	public byte[] getBytes() {
		return getBytes(value);
	}

	public static long getLong(byte[] buffer) {
		return (((long) buffer[0] << 56) + ((long) (buffer[1] & 255) << 48)
				+ ((long) (buffer[2] & 255) << 40)
				+ ((long) (buffer[3] & 255) << 32)
				+ ((long) (buffer[4] & 255) << 24) + ((buffer[5] & 255) << 16)
				+ ((buffer[6] & 255) << 8) + ((buffer[7] & 255) << 0));
	}

	public static Value readBytes(byte[] readBuffer) {
		return new LongValue(
				(((long) readBuffer[0] << 56)
						+ ((long) (readBuffer[1] & 255) << 48)
						+ ((long) (readBuffer[2] & 255) << 40)
						+ ((long) (readBuffer[3] & 255) << 32)
						+ ((long) (readBuffer[4] & 255) << 24)
						+ ((readBuffer[5] & 255) << 16)
						+ ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255) << 0)));
	}
}