/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.SQLEngineConstants;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Factor�a abstracta de objetos value que dado un tipo b�sico, devuelve el
 * wrapper apropiado
 *
 * @author $author$
 * @version $Revision: 1.1.4.1 $
 */
public class ValueFactory {

	public static final Value TRUE = createValue(true);

	public static final Value FALSE = createValue(false);

	/**
	 * Crea un objeto de tipo Value a partir de un int
	 *
	 * @param n
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static IntValue createValue(int n) {
		IntValue ret = new IntValue();
		ret.setValue(n);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Value a partir de un long
	 *
	 * @param l
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static LongValue createValue(long l) {
		LongValue ret = new LongValue();
		ret.setValue(l);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Value a partir de un byte
	 *
	 * @param b
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static Value createValue(byte b) {
		return new ByteValue(b);
	}

	/**
	 * Crea un objeto de tipo Value a partir de un long
	 *
	 * @param l
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static ShortValue createValue(short l) {
		return new ShortValue(l);
	}

	/**
	 * Crea un objeto de tipo Value a partir de un String
	 *
	 * @param s
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static StringValue createValue(String s) {
		StringValue ret = new StringValue();
		ret.setValue(s);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Value a partir de un float
	 *
	 * @param f
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static FloatValue createValue(float f) {
		FloatValue ret = new FloatValue();
		ret.setValue(f);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Value a partir de un double
	 *
	 * @param d
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static DoubleValue createValue(double d) {
		DoubleValue ret = new DoubleValue();
		ret.setValue(d);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Date a partir de un Date
	 *
	 * @param d
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static DateValue createValue(Date d) {
		DateValue ret = new DateValue();
		ret.setValue(d);

		return ret;
	}

	/**
	 * Creates a TimeValue object
	 *
	 * @param t
	 *            Time value
	 *
	 * @return TimeValue
	 */
	public static TimeValue createValue(Time t) {
		TimeValue ret = new TimeValue();
		ret.setValue(t);

		return ret;
	}

	/**
	 * Creates a TimestampValue object
	 *
	 * @param t
	 *            Timestamp value
	 *
	 * @return TimestampValue
	 */
	public static TimestampValue createValue(Timestamp t) {
		TimestampValue ret = new TimestampValue();
		ret.setValue(t);

		return ret;
	}

	/**
	 * Crea un objeto de tipo Value a partir de un booleano
	 *
	 * @param b
	 *            valor que se quiere representar
	 *
	 * @return objeto Value con el valor que se pasa como par�metro
	 */
	public static BooleanValue createValue(boolean b) {
		BooleanValue ret = new BooleanValue();
		ret.setValue(b);

		return ret;
	}

	/**
	 * Creates an ArrayValue
	 *
	 * @param values
	 *            DOCUMENT ME!
	 *
	 * @return ArrayValue
	 */
	public static ValueCollection createValue(Value[] values) {
		ValueCollection v = new ValueCollection();
		v.setValues(values);

		return v;
	}

	/**
	 * Crea un Value a partir de un literal encontrado en una instrucci�n y su
	 * tipo
	 *
	 * @param text
	 *            Texto del valor
	 * @param type
	 *            Tipo del valor
	 *
	 * @return Objeto Value del tipo adecuado
	 *
	 * @throws SemanticException
	 *             Si el tipo del literal no est� soportado
	 */
	public static Value createValue(String text, int type)
			throws SemanticException {
		switch (type) {
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
			throw new SemanticException("Unexpected literal type: " + text
					+ "->" + type);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param text
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws ParseException
	 *             DOCUMENT ME!
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
			value = ValueFactory.createValue(Timestamp.valueOf(text));

			break;

		case Type.TIME:
			DateFormat tf = DateFormat.getTimeInstance();
			value = ValueFactory
					.createValue(new Time(tf.parse(text).getTime()));

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
	public static NullValue createNullValue() {
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
	static NumericValue suma(NumericValue v1, NumericValue v2) {
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
	static NumericValue producto(NumericValue v1, NumericValue v2) {
		int type = getType(v1.getType(), v2.getType());

		while (true) {
			switch (type) {
			/*
			 * El operador '+' en java no est� definido para byte ni short, as�
			 * que nosotros tampoco lo definimos. Por otro lado no conocemos
			 * manera de detectar el overflow al operar con long's ni double's
			 * de manera eficiente, as� que no se detecta.
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
	 * Calcula la inversa (1/v) del valor que se pasa como par�metro.
	 *
	 * @param v
	 *            Valor cuya inversa se quiere obtener
	 *
	 * @return DoubleValue
	 */
	static NumericValue inversa(NumericValue v) {
		v.getType();

		return (NumericValue) createValue(1 / v.doubleValue());
	}

	/**
	 * Creates a byte array value
	 *
	 * @param bytes
	 *            bytes of the value
	 *
	 * @return
	 */
	public static BinaryValue createValue(byte[] bytes) {
		BinaryValue ret = new BinaryValue(bytes);

		return ret;
	}

	public static Value createValue(Geometry geom) {
		return new GeometryValue(geom);
	}

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
}
