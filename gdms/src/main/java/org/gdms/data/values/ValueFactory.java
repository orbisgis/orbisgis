package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

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
     * @param n valor que se quiere representar
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
     * @param l valor que se quiere representar
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
     * @param b valor que se quiere representar
     *
     * @return objeto Value con el valor que se pasa como par�metro
     */
    public static Value createValue(byte b) {
        return new ByteValue(b);
    }

    /**
     * Crea un objeto de tipo Value a partir de un long
     *
     * @param l valor que se quiere representar
     *
     * @return objeto Value con el valor que se pasa como par�metro
     */
    public static ShortValue createValue(short l) {
        return new ShortValue(l);
    }

    /**
     * Crea un objeto de tipo Value a partir de un String
     *
     * @param s valor que se quiere representar
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
     * @param f valor que se quiere representar
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
     * @param d valor que se quiere representar
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
     * @param d valor que se quiere representar
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
     * @param t Time value
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
     * @param t Timestamp value
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
     * @param b valor que se quiere representar
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
     * @param values DOCUMENT ME!
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
     * @param text Texto del valor
     * @param type Tipo del valor
     *
     * @return Objeto Value del tipo adecuado
     *
     * @throws SemanticException Si el tipo del literal no est� soportado
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
                throw new SemanticException("Unexpected literal type: " + text +
                    "->" + type);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     */
    public static Value createValueByType(String text, int type)
        throws ParseException, NumberFormatException {
        Value value;

        switch (type) {
            case Value.LONG:
                value = ValueFactory.createValue(Long.parseLong(text));

                break;

            case Value.BOOLEAN:
                value = ValueFactory.createValue(Boolean.valueOf(text)
                                                        .booleanValue());

                break;

            case Value.STRING:
                value = ValueFactory.createValue(text);

                break;

            case Value.DATE:
                value = ValueFactory.createValue(DateFormat.getDateInstance(DateFormat.SHORT).parse(text));

                break;

            case Value.DOUBLE:
                value = ValueFactory.createValue(DecimalFormat.getNumberInstance().parse(text).doubleValue());

                break;

            case Value.INT:
                value = ValueFactory.createValue(Integer.parseInt(text));

                break;

            case Value.FLOAT:
                value = ValueFactory.createValue(DecimalFormat.getNumberInstance().parse(text).floatValue());

                break;

            case Value.SHORT:
                value = ValueFactory.createValue(Short.parseShort(text));

                break;

            case Value.BYTE:
                value = ValueFactory.createValue(Byte.parseByte(text));

                break;

            case Value.BINARY:

                if ((text.length() / 2) != (text.length() / 2.0)) {
                    throw new ParseException("binary fields must have even number of characters.",
                        0);
                }

                byte[] array = new byte[text.length() / 2];

                for (int i = 0; i < (text.length() / 2); i++) {
                    String byte_ = text.substring(2 * i, (2 * i) + 2);
                    array[i] = (byte) Integer.parseInt(byte_, 16);
                }

                value = ValueFactory.createValue(array);

                break;

            case Value.TIMESTAMP:
                value = ValueFactory.createValue(Timestamp.valueOf(text));

                break;

            case Value.TIME:
                DateFormat tf = DateFormat.getTimeInstance();
                value = ValueFactory.createValue(new Time(
                            tf.parse(text).getTime()));

                break;

            default:
                value = ValueFactory.createValue(text);
        }

        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     * @param className DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SemanticException DOCUMENT ME!
     *
     * @deprecated Use createValueWithType(String, int) instead
     */
    public static Value createValue(String text, String className)
        throws SemanticException {
        if (className.equals("com.hardcode.gdbms.engine.values.BooleanValue")) {
            return createValue(Boolean.getBoolean(text));
        }

        if (className.equals("com.hardcode.gdbms.engine.values.DateValue")) {
            try {
                return createValue(DateFormat.getInstance().parse(text));
            } catch (ParseException e) {
                throw new SemanticException(e);
            }
        }

        if (className.equals("com.hardcode.gdbms.engine.values.DoubleValue")) {
            return createValue(Double.parseDouble(text));
        }

        if (className.equals("com.hardcode.gdbms.engine.values.FloatValue")) {
            return createValue(Float.parseFloat(text));
        }

        if (className.equals("com.hardcode.gdbms.engine.values.IntValue")) {
            return createValue(Integer.parseInt(text));
        }

        if (className.equals("com.hardcode.gdbms.engine.values.LongValue")) {
            return createValue(Long.parseLong(text));
        }

        if (className.equals("com.hardcode.gdbms.engine.values.StringValue")) {
            return createValue(text);
        }

        // default:
        throw new SemanticException(
            "Unexpected className in createValue (GDBMS) text: " + text +
            "-> className: " + className);
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
     * @param v1 first value
     * @param v2 second value
     *
     * @return a numeric value with the operation
     */
    static NumericValue suma(NumericValue v1, NumericValue v2) {
        int type = Math.max(v1.getType(), v2.getType());

        while (true) {
            switch (type) {
                /*
                 * El operador '+' en java no est� definido para byte ni short, as�
                 * que nosotros tampoco lo definimos.
                 * Por otro lado no conocemos manera de detectar el overflow al operar
                 * con long's ni double's de manera eficiente, as� que no se detecta.
                 */
                case Value.BYTE:
                case Value.SHORT:
                case Value.INT:

                    int intValue = v1.intValue() + v2.intValue();

                    if ((intValue) != (v1.longValue() + v2.longValue())) {
                        type = Value.LONG;

                        continue;
                    } else {
                        return (NumericValue) createValue(intValue);
                    }

                case Value.LONG:
                    return (NumericValue) createValue(v1.longValue() +
                        v2.longValue());

                case Value.FLOAT:

                    float floatValue = v1.floatValue() + v2.floatValue();

                    if ((floatValue) != (v1.doubleValue() + v2.doubleValue())) {
                        type = Value.DOUBLE;

                        continue;
                    } else {
                        return (NumericValue) createValue(floatValue);
                    }

                case Value.DOUBLE:
                    return (NumericValue) createValue(v1.doubleValue() +
                        v2.doubleValue());
            }
        }
    }

    /**
     * Gets the value of the operation v1  v2
     *
     * @param v1 first value
     * @param v2 second value
     *
     * @return a numeric value with the operation
     */
    static NumericValue producto(NumericValue v1, NumericValue v2) {
        int type = Math.max(v1.getType(), v2.getType());

        while (true) {
            switch (type) {
                /*
                 * El operador '+' en java no est� definido para byte ni short, as�
                 * que nosotros tampoco lo definimos.
                 * Por otro lado no conocemos manera de detectar el overflow al operar
                 * con long's ni double's de manera eficiente, as� que no se detecta.
                 */
                case Value.BYTE:
                case Value.SHORT:
                case Value.INT:

                    int intValue = v1.intValue() * v2.intValue();

                    if ((intValue) != (v1.intValue() * v2.intValue())) {
                        type = Value.LONG;

                        continue;
                    } else {
                        return (NumericValue) createValue(intValue);
                    }

                case Value.LONG:
                    return (NumericValue) createValue(v1.longValue() * v2.longValue());

                case Value.FLOAT:

                    float floatValue = v1.floatValue() * v2.floatValue();

                    if ((floatValue) != (v1.doubleValue() * v2.doubleValue())) {
                        type = Value.DOUBLE;

                        continue;
                    } else {
                        return (NumericValue) createValue(floatValue);
                    }

                case Value.DOUBLE:
                    return (NumericValue) createValue(v1.doubleValue() * v2.doubleValue());
            }
        }
    }

    /**
     * Calcula la inversa (1/v) del valor que se pasa como par�metro.
     *
     * @param v Valor cuya inversa se quiere obtener
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
     * @param bytes bytes of the value
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
}
