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

import java.sql.Types;

import org.gdms.data.types.Type;
import org.orbisgis.utils.FormatUtils;

/**
 * Wrapper for floats
 *
 * @author Fernando Gonzalez Cortes
 */
class FloatValue extends NumericValue {
	private float value;

	FloatValue() {
	}

	FloatValue(float value) {
		this.value = value;
	}

	/**
	 * set the value
	 *
	 * @param value
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * get the value
	 *
	 * @return
	 */
	public float getValue() {
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
		return FormatUtils.round(value, getDecimalDigitsCount());
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
		return writer.getStatementString(value, Types.REAL);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.FLOAT;
	}

	@Override
	public int getDecimalDigitsCount() {
		String str = Float.toString(value);
		if (str.endsWith(".0")) {
			return 0;
		}
		return str.length() - (str.indexOf(".") + 1);
	}

	public byte[] getBytes() {
		return IntValue.getBytes(Float.floatToIntBits(value));
	}

	public static Value readBytes(byte[] buffer) {
		return new FloatValue(Float.intBitsToFloat(IntValue.getInt(buffer)));
	}
}