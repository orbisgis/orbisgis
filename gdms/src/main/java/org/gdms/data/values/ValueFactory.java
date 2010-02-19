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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.strategies.SemanticException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Factory to instantiate Value instances from basic types
 *
 */
public class ValueFactory {

	public static final Value TRUE = createValue(true);

	public static final Value FALSE = createValue(false);

	/**
	 * Creates a Value instance that contains the specified int value
	 *
	 * @param n
	 *
	 */
	public static Value createValue(int n) {
		IntValue ret = new IntValue();
		ret.setValue(n);

		return ret;
	}

	/**
	 * Creates a Value instance that contains the specified long value
	 *
	 * @param l
	 */
	public static Value createValue(long l) {
		LongValue ret = new LongValue();
		ret.setValue(l);

		return ret;
	}

	/**
	 * Creates a Value instance that contains the specified byte value
	 *
	 * @param b
	 */
	public static Value createValue(byte b) {
		return new ByteValue(b);
	}

	/**
	 * Creates a Value instance that contains the specified short value
	 *
	 * @param l
	 */
	public static Value createValue(short l) {
		return new ShortValue(l);
	}

	/**
	 * Creates a Value instance that contains the specified String value
	 *
	 * @param s
	 */
	public static Value createValue(String s) {
		if (s != null) {
			StringValue ret = new StringValue();
			ret.setValue(s);

			return ret;
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a Value instance that contains the specified float value
	 *
	 * @param f
	 */
	public static Value createValue(float f) {
		FloatValue ret = new FloatValue();
		ret.setValue(f);

		return ret;
	}

	/**
	 * Creates a Value instance that contains the specified double value
	 *
	 * @param d
	 */
	public static Value createValue(double d) {
		DoubleValue ret = new DoubleValue();
		ret.setValue(d);

		return ret;
	}

	/**
	 * Creates a Value instance that contains the specified date value
	 *
	 * @param d
	 */
	public static Value createValue(Date d) {
		if (d != null) {
			DateValue ret = new DateValue();
			ret.setValue(d);

			return ret;
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a Value instance that contains the specified time value
	 *
	 * @param t
	 */
	public static Value createValue(Time t) {
		if (t != null) {
			TimeValue ret = new TimeValue();
			ret.setValue(t);

			return ret;
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a TimestampValue object
	 *
	 * @param t
	 */
	public static Value createValue(Timestamp t) {
		if (t != null) {
			TimestampValue ret = new TimestampValue();
			ret.setValue(t);

			return ret;
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a Value instance that contains the specified boolean value
	 *
	 * @param b
	 */
	public static Value createValue(boolean b) {
		BooleanValue ret = new BooleanValue();
		ret.setValue(b);

		return ret;
	}

	/**
	 * Creates a Value collection
	 *
	 * @param values
	 */
	public static ValueCollection createValue(Value[] values) {
		ValueCollection v = new ValueCollection();
		v.setValues(values);

		return v;
	}

	/**
	 * Creates a Value instance with the specified literal
	 *
	 * @param text
	 *            Text containing the value
	 * @param type
	 *            Type of literal. SQL parser constant:
	 *            SQLEngineConstants.STRING_LITERAL,
	 *            SQLEngineConstants.INTEGER_LITERAL or
	 *            SQLEngineConstants.FLOATING_POINT_LITERAL
	 *
	 *
	 * @throws SemanticException
	 *             If the literal type is not valid
	 */
	public static Value createValue(String text, int type)
			throws IllegalArgumentException {
		switch (type) {
		case SQLEngineConstants.BOOLEAN_LITERAL:

			return new BooleanValue(Boolean.parseBoolean(text));

		case SQLEngineConstants.STRING_LITERAL:

			StringValue r1 = new StringValue();
			r1.setValue(text.substring(1, text.length() - 1));

			return r1;

		case SQLEngineConstants.INTEGER_LITERAL:

			try {
				IntValue r2 = new IntValue();
				r2.setValue(Integer.parseInt(text));

				return r2;
			} catch (NumberFormatException e) {
				LongValue r2 = new LongValue();
				r2.setValue(Long.parseLong(text));

				return r2;
			}

		case SQLEngineConstants.FLOATING_POINT_LITERAL:

			try {
				FloatValue r2 = new FloatValue();
				r2.setValue(Float.parseFloat(text));

				return r2;
			} catch (NumberFormatException e) {
				DoubleValue r2 = new DoubleValue();
				r2.setValue(Double.parseDouble(text));

				return r2;
			}

		default:
			throw new IllegalArgumentException("Unexpected literal type: "
					+ text + "->" + type);
		}
	}

	/**
	 * Instantiates a value of the specified type containing the value with the
	 * specified textual representation
	 *
	 * @param text
	 *            Textual representation of the value to instantiate
	 * @param type
	 *            Type of the value. Must be one of the constants of the Type
	 *            interface
	 *
	 * @return
	 *
	 * @throws ParseException
	 *             If the textual representation cannot be converted to the
	 *             specified type
	 */
	public static Value createValueByType(String text, int type)
			throws ParseException, NumberFormatException {
		Value value;

		switch (type) {
		case Type.LONG:
			value = ValueFactory.createValue(Long.parseLong(text));

			break;

		case Type.BOOLEAN:
			value = ValueFactory.createValue(Boolean.valueOf(text)
					.booleanValue());

			break;

		case Type.STRING:
			value = ValueFactory.createValue(text);

			break;

		case Type.DATE:
			value = new DateValue(text);

			break;

		case Type.DOUBLE:
			value = ValueFactory.createValue(DecimalFormat.getNumberInstance()
					.parse(text).doubleValue());

			break;

		case Type.INT:
			value = ValueFactory.createValue(Integer.parseInt(text));

			break;

		case Type.FLOAT:
			value = ValueFactory.createValue(DecimalFormat.getNumberInstance()
					.parse(text).floatValue());

			break;

		case Type.GEOMETRY:
			try {
				value = GeometryValue.parseString(text);
			} catch (com.vividsolutions.jts.io.ParseException e) {
				throw new ParseException("Cannot parse geometry:"
						+ e.getMessage(), -1);
			}

			break;

		case Type.SHORT:
			value = ValueFactory.createValue(Short.parseShort(text));

			break;

		case Type.BYTE:
			value = ValueFactory.createValue(Byte.parseByte(text));

			break;

		case Type.BINARY:

			if ((text.length() / 2) != (text.length() / 2.0)) {
				throw new ParseException(
						"binary fields must have even number of characters.", 0);
			}

			byte[] array = new byte[text.length() / 2];

			for (int i = 0; i < (text.length() / 2); i++) {
				String byte_ = text.substring(2 * i, (2 * i) + 2);
				array[i] = (byte) Integer.parseInt(byte_, 16);
			}

			value = ValueFactory.createValue(array);

			break;

		case Type.TIMESTAMP:
			try {
				value = ValueFactory.createValue(Timestamp.valueOf(text));
			} catch (IllegalArgumentException e) {
				throw new ParseException(e.getMessage(), -1);
			}

			break;

		case Type.TIME:
			value = new TimeValue(text);

			break;

		default:
			value = ValueFactory.createValue(text);
		}

		return value;
	}

	/**
	 * Creates a new null Value
	 *
	 * @return NullValue
	 */
	public static Value createNullValue() {
		return new NullValue();
	}

	/**
	 * Gets a Value with the value v1 plus v2
	 *
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 *
	 * @return a numeric value with the operation
	 */
	static Value suma(NumericValue v1, NumericValue v2) {
		int type = getType(v1.getType(), v2.getType());

		switch (type) {
		/*
		 * El operador '+' en java no est� definido para byte ni short, as� que
		 * nosotros tampoco lo definimos. Por otro lado no conocemos manera de
		 * detectar el overflow al operar con long's ni double's de manera
		 * eficiente, as� que no se detecta.
		 */
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:

			int intValue = v1.intValue() + v2.intValue();

			if ((intValue) != (v1.longValue() + v2.longValue())) {
				type = Type.LONG;

			} else {
				return (NumericValue) createValue(intValue);
			}

		case Type.LONG:
			return (NumericValue) createValue(v1.longValue() + v2.longValue());

		case Type.FLOAT:

			float floatValue = v1.floatValue() + v2.floatValue();

			if ((floatValue) != (v1.doubleValue() + v2.doubleValue())) {
				type = Type.DOUBLE;

			} else {
				return (NumericValue) createValue(floatValue);
			}

		case Type.DOUBLE:
			return (NumericValue) createValue(v1.doubleValue()
					+ v2.doubleValue());
		}

		throw new RuntimeException("Cannot sum this data types: "
				+ v1.getType() + " and " + v2.getType());
	}

	private static int getType(int type1, int type2) {
		int type;
		if ((type1 == Type.DOUBLE) || (type2 == Type.DOUBLE)) {
			type = Type.DOUBLE;
		} else if ((type1 == Type.FLOAT) || (type2 == Type.FLOAT)) {
			type = Type.FLOAT;
		} else if ((type1 == Type.LONG) || (type2 == Type.LONG)) {
			type = Type.LONG;
		} else {
			type = Type.INT;
		}
		return type;
	}

	/**
	 * Gets the value of the operation v1 v2
	 *
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 *
	 * @return a numeric value with the operation
	 */
	static Value producto(NumericValue v1, NumericValue v2) {
		int type = getType(v1.getType(), v2.getType());

		while (true) {
			switch (type) {
			/*
			 * El operador '+' en java no esta definido para byte ni short, asi
			 * que nosotros tampoco lo definimos. Por otro lado no conocemos
			 * manera de detectar el overflow al operar con long's ni double's
			 * de manera eficiente, asi que no se detecta.
			 */
			case Type.BYTE:
			case Type.SHORT:
			case Type.INT:

				int intValue = v1.intValue() * v2.intValue();

				if ((intValue) != (v1.intValue() * v2.intValue())) {
					type = Type.LONG;

					continue;
				} else {
					return (NumericValue) createValue(intValue);
				}

			case Type.LONG:
				return (NumericValue) createValue(v1.longValue()
						* v2.longValue());

			case Type.FLOAT:

				float floatValue = v1.floatValue() * v2.floatValue();

				if ((floatValue) != (v1.doubleValue() * v2.doubleValue())) {
					type = Type.DOUBLE;

					continue;
				} else {
					return (NumericValue) createValue(floatValue);
				}

			case Type.DOUBLE:
				return (NumericValue) createValue(v1.doubleValue()
						* v2.doubleValue());
			}
		}
	}

	/**
	 * Gets the inverse value (1/v) of the specified parameter.
	 *
	 * @param v
	 *
	 * @return
	 */
	static Value inversa(NumericValue v) {
		if (v.getAsDouble() == 0) {
			throw new ArithmeticException("Cannot divide by zero");
		} else {
			return (NumericValue) createValue(1 / v.doubleValue());
		}
	}

	/**
	 * Creates a byte array value
	 *
	 * @param bytes
	 *            bytes of the value
	 *
	 * @return
	 */
	public static Value createValue(byte[] bytes) {
		BinaryValue ret = new BinaryValue(bytes);

		return ret;
	}

	/**
	 * Creates a Value instance that contains the specified geometry value
	 *
	 * @param geom
	 * @return
	 */
	public static Value createValue(Geometry geom) {
		if (geom != null) {
			return new GeometryValue(geom);
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a Value instance that contains the specified raster value
	 *
	 * @param raster
	 * @return
	 */
	public static Value createValue(GeoRaster raster) {
		if (raster != null) {
			return new RasterValue(raster);
		} else {
			return createNullValue();
		}
	}

	/**
	 * Creates a Value from the specified bytes. Those bytes must have been
	 * obtained by a previous call to Value.getBytes
	 *
	 * @param valueType
	 *            The type of the value. one of the constants in Type interface
	 * @param buffer
	 *            byte representation of the value
	 *
	 * @return
	 */
	public static Value createValue(int valueType, byte[] buffer) {
		switch (valueType) {
		case Type.BINARY:
			return BinaryValue.readBytes(buffer);
		case Type.BOOLEAN:
			return BooleanValue.readBytes(buffer);
		case Type.BYTE:
			return ByteValue.readBytes(buffer);
		case Type.COLLECTION:
			return ValueCollection.readBytes(buffer);
		case Type.DATE:
			return DateValue.readBytes(buffer);
		case Type.DOUBLE:
			return DoubleValue.readBytes(buffer);
		case Type.FLOAT:
			return FloatValue.readBytes(buffer);
		case Type.GEOMETRY:
			return GeometryValue.readBytes(buffer);
		case Type.INT:
			return IntValue.readBytes(buffer);
		case Type.LONG:
			return LongValue.readBytes(buffer);
		case Type.NULL:
			return new NullValue();
		case Type.RASTER:
			return RasterValue.readBytes(buffer);
		case Type.SHORT:
			return ShortValue.readBytes(buffer);
		case Type.STRING:
			return StringValue.readBytes(buffer);
		case Type.TIME:
			return TimeValue.readBytes(buffer);
		case Type.TIMESTAMP:
			return TimestampValue.readBytes(buffer);
		default:
			throw new RuntimeException("Wrong type: " + valueType);
		}
	}

	/**
	 * <p>
	 * Creates a value of the specified type in two steps. The first one builds
	 * quickly the value based on the byte[], the second asks for data to the
	 * specified byteProvider to build the Value completely on demand.
	 * </p>
	 * <p>
	 * Note that this method only supports Rasters right now.
	 * </p>
	 *
	 * @param valueType
	 * @param buffer
	 * @param byteProvider
	 * @return
	 */
	public static Value createLazyValue(int valueType, byte[] buffer,
			ByteProvider byteProvider) {
		switch (valueType) {
		case Type.RASTER:
			return RasterValue.readBytes(buffer, byteProvider);
		default:
			throw new RuntimeException("Wrong type: " + valueType);
		}
	}
}
