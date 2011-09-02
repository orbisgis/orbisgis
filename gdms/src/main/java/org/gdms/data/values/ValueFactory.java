/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import org.gdms.data.types.Type;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.InvalidTypeException;

/**
 * Factory to instantiate Value instances from basic types
 * 
 */
public final class ValueFactory {

        private static final Logger LOG = Logger.getLogger(ValueFactory.class);
        public static final BooleanValue TRUE = new DefaultBooleanValue(true);
        public static final BooleanValue FALSE = new DefaultBooleanValue(false);
        /**
         * Max size of the Value cache.
         */
        public static final int VALUECACHEMAXSIZE = 50;

        /**
         * Creates a Value instance that contains the specified int value
         *
         * @param n
         * @return
         *
         */
        public static IntValue createValue(int n) {
                return new DefaultIntValue(n);
        }

        /**
         * Creates a Value instance that contains the specified long value
         *
         * @param l
         * @return
         */
        public static LongValue createValue(long l) {
                return new DefaultLongValue(l);
        }

        /**
         * Creates a Value instance that contains the specified byte value
         *
         * @param b
         * @return
         */
        public static ByteValue createValue(byte b) {
                return new DefaultByteValue(b);
        }

        /**
         * Creates a Value instance that contains the specified short value
         *
         * @param l
         * @return
         */
        public static ShortValue createValue(short l) {
                return new DefaultShortValue(l);
        }

        /**
         * Creates a Value instance that contains the specified String value
         *
         * @param s
         * @return
         */
        public static StringValue createValue(String s) {
                if (s != null) {
                        return new DefaultStringValue(s);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified float value
         *
         * @param f
         * @return
         */
        public static FloatValue createValue(float f) {
                return new DefaultFloatValue(f);
        }

        /**
         * Creates a Value instance that contains the specified double value
         *
         * @param d
         * @return
         */
        public static DoubleValue createValue(double d) {
                return new DefaultDoubleValue(d);
        }

        /**
         * Creates a Value instance that contains the specified date value
         *
         * @param d
         * @return
         */
        public static DateValue createValue(Date d) {
                if (d != null) {
                        return new DefaultDateValue(new java.sql.Date(d.getTime()));
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified time value
         *
         * @param t
         * @return
         */
        public static TimeValue createValue(Time t) {
                if (t != null) {
                        return new DefaultTimeValue(t);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a TimestampValue object
         *
         * @param t
         * @return
         */
        public static TimestampValue createValue(Timestamp t) {
                if (t != null) {
                        return new DefaultTimestampValue(t);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified boolean value
         *
         * @param b
         * @return
         */
        public static BooleanValue createValue(boolean b) {
                if (b) {
                        return TRUE;
                } else {
                        return FALSE;
                }
        }

        /**
         * Creates a Value collection
         *
         * @param values
         * @return
         */
        public static ValueCollection createValue(Value[] values) {
                ValueCollection v = new DefaultValueCollection();
                v.setValues(values);

                return v;
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
         * @throws NumberFormatException
         */
        public static Value createValueByType(String text, int type)
                throws ParseException {
                Value value;

                switch (type) {
                        case Type.LONG:
                                value = createValue(Long.parseLong(text));

                                break;

                        case Type.BOOLEAN:
                                value = createValue(Boolean.valueOf(text));

                                break;

                        case Type.DATE:
                                value = DefaultDateValue.parseString(text);

                                break;

                        case Type.DOUBLE:
                                value = createValue(DecimalFormat.getNumberInstance(Locale.ROOT).parse(text).doubleValue());

                                break;

                        case Type.INT:
                                value = createValue(Integer.parseInt(text));

                                break;

                        case Type.FLOAT:
                                value = createValue(DecimalFormat.getNumberInstance(Locale.ROOT).parse(text).floatValue());

                                break;

                        case Type.GEOMETRY:
                        case Type.GEOMETRYCOLLECTION:
                        case Type.POINT:
                        case Type.LINESTRING:
                        case Type.POLYGON:
                        case Type.MULTILINESTRING:
                        case Type.MULTIPOINT:
                        case Type.MULTIPOLYGON:
                                try {
                                        value = DefaultGeometryValue.parseString(text);
                                } catch (com.vividsolutions.jts.io.ParseException e) {
                                        LOG.error("Error parsing geometry", e);
                                        throw new ParseException("Cannot parse geometry:" + e.getMessage(), -1);
                                }

                                break;

                        case Type.SHORT:
                                value = createValue(Short.parseShort(text));

                                break;

                        case Type.BYTE:
                                value = createValue(Byte.parseByte(text));

                                break;

                        case Type.BINARY:

                                if (text.length() % 2 != 0) {
                                        throw new ParseException(
                                                "binary fields must have even number of characters.", 0);
                                }

                                byte[] array = new byte[text.length() / 2];

                                for (int i = 0; i < (text.length() / 2); i++) {
                                        String theByte = text.substring(2 * i, (2 * i) + 2);
                                        array[i] = (byte) Integer.parseInt(theByte, 16);
                                }

                                value = createValue(array);

                                break;

                        case Type.TIMESTAMP:
                                try {
                                        value = createValue(Timestamp.valueOf(text));
                                } catch (IllegalArgumentException e) {
                                        LOG.error("Error parsing Timestamp", e);
                                        throw new ParseException(e.getMessage(), -1);
                                }

                                break;

                        case Type.TIME:
                                value = DefaultTimeValue.parseString(text);

                                break;

                        case Type.STRING:
                        default:
                                value = createValue(text);
                }

                return value;
        }

        /**
         * Creates a new null Value
         *
         * @param <T> 
         * @return NullValue
         */
        public static <T extends Value> T createNullValue() {
                return (T) NullValue.NULL;
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
        static NumericValue sum(NumericValue v1, NumericValue v2) {
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
                                        return createValue(intValue);
                                }

                        case Type.LONG:
                                return createValue(v1.longValue() + v2.longValue());

                        case Type.FLOAT:

                                float floatValue = v1.floatValue() + v2.floatValue();

                                if ((floatValue) != (v1.doubleValue() + v2.doubleValue())) {
                                        type = Type.DOUBLE;

                                } else {
                                        return createValue(floatValue);
                                }

                        case Type.DOUBLE:
                                return createValue(v1.doubleValue()
                                        + v2.doubleValue());
                        default:
                                throw new IncompatibleTypesException("Cannot sum this data types: "
                                        + v1.getType() + " and " + v2.getType());
                }

        }

        static NumericValue remainder(NumericValue v1, NumericValue v2) {
                int type = getType(v1.getType(), v2.getType());

                switch (type) {
                        case Type.BYTE:
                        case Type.SHORT:
                        case Type.INT:
                                return createValue(v1.intValue() % v2.intValue());

                        case Type.FLOAT:
                                return createValue(v1.floatValue() % v2.floatValue());
                        case Type.LONG:
                                return createValue(v1.longValue() % v2.longValue());
                        case Type.DOUBLE:
                                return createValue(v1.doubleValue() % v2.doubleValue());
                        default:
                                throw new IncompatibleTypesException("Cannot mod this data types: "
                                        + v1.getType() + " and " + v2.getType());
                }
        }

        static BooleanValue and(BooleanValue v1, BooleanValue v2) {
                final boolean aNull = v1.isNull();
                final boolean bNull = v2.isNull();

                if (aNull && bNull) {
                        return createNullValue();
                }
                final Boolean bBool = v2.getAsBoolean();
                final Boolean aBool = v1.getAsBoolean();

                if ((aNull && bBool) || (bNull && aBool)) {
                        return createNullValue();
                } else {
                        return createValue(aBool && bBool);
                }
        }

        static BooleanValue or(BooleanValue v1, BooleanValue v2) {
                final boolean aNull = v1.isNull();
                final boolean bNull = v2.isNull();

                if (aNull && bNull) {
                        return createNullValue();
                }
                final Boolean bBool = v2.getAsBoolean();
                final Boolean aBool = v1.getAsBoolean();

                if ((aNull && !bBool) || (bNull && !aBool)) {
                        return createNullValue();
                } else {
                        return createValue(aBool || bBool);
                }
        }

        static BooleanValue equals(BooleanValue v1, BooleanValue v2) {
                if (v1.isNull() || v2.isNull()) {
                        return createNullValue();
                } else {
                        return createValue(v1.getAsBoolean().equals(v2.getAsBoolean()));
                }
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
        static NumericValue product(NumericValue v1, NumericValue v2) {
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
                                                return createValue(intValue);
                                        }

                                case Type.LONG:
                                        return createValue(v1.longValue()
                                                * v2.longValue());

                                case Type.FLOAT:

                                        float floatValue = v1.floatValue() * v2.floatValue();

                                        if ((floatValue) != (v1.doubleValue() * v2.doubleValue())) {
                                                type = Type.DOUBLE;

                                                continue;
                                        } else {
                                                return createValue(floatValue);
                                        }

                                case Type.DOUBLE:
                                        return createValue(v1.doubleValue()
                                                * v2.doubleValue());
                                default:
                                        throw new IncompatibleTypesException("Cannot multiply these data types: "
                                                + v1.getType() + " and " + v2.getType());
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
        static NumericValue inverse(NumericValue v) {
                if (v.getAsDouble() == 0) {
                        throw new ArithmeticException("Division by zero");
                } else {
                        return createValue(1 / v.doubleValue());
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
        public static BinaryValue createValue(byte[] bytes) {
                return new DefaultBinaryValue(bytes);
        }

        /**
         * Creates a Value instance that contains the specified geometry value
         *
         * @param geom
         * @return
         */
        public static GeometryValue createValue(Geometry geom) {
                if (geom != null) {
                        if (geom instanceof Point) {
                                return createValue((Point) geom);
                        } else if (geom instanceof LineString) {
                                return createValue((LineString) geom);
                        } else if (geom instanceof Polygon) {
                                return createValue((Polygon) geom);
                        } else if (geom instanceof GeometryCollection) {
                                return createValue((GeometryCollection) geom);
                        } else if (geom instanceof MultiPoint) {
                                return createValue((MultiPoint) geom);
                        } else if (geom instanceof MultiLineString) {
                                return createValue((MultiLineString) geom);
                        } else if (geom instanceof MultiPolygon) {
                                return createValue((MultiPolygon) geom);
                        } else {
                                throw new InvalidTypeException("Unknown geometry type: " + geom.getGeometryType());
                        }
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified Point value
         *
         * @param geom
         * @return
         */
        public static PointValue createValue(Point geom) {
                if (geom != null) {
                        return new DefaultPointValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static LineStringValue createValue(LineString geom) {
                if (geom != null) {
                        return new DefaultLineStringValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static PolygonValue createValue(Polygon geom) {
                if (geom != null) {
                        return new DefaultPolygonValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static GeometryCollectionValue createValue(GeometryCollection geom) {
                if (geom != null) {
                        return new DefaultGeometryCollectionValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static MultiPointValue createValue(MultiPoint geom) {
                if (geom != null) {
                        return new DefaultMultiPointValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static MultiLineStringValue createValue(MultiLineString geom) {
                if (geom != null) {
                        return new DefaultMultiLineStringValue(geom);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified LineString value
         *
         * @param geom
         * @return
         */
        public static MultiPolygonValue createValue(MultiPolygon geom) {
                if (geom != null) {
                        return new DefaultMultiPolygonValue(geom);
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
        public static RasterValue createValue(GeoRaster raster) {
                if (raster != null) {
                        return new DefaultRasterValue(raster);
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
        public static Value createValue(final int valueType, byte[] buffer) {
                //In many cases, Type.GEOMETRY will be set with a concrete geometry type.
                //If this is so, we just keep Type.GEOMETRY, as the needed result
                //will be returned by createValue(Geometry).
                int toEval = (valueType & Type.GEOMETRY) != 0 ? Type.GEOMETRY : valueType;
                switch (toEval) {
                        case Type.BINARY:
                                return DefaultBinaryValue.readBytes(buffer);
                        case Type.BOOLEAN:
                                return DefaultBooleanValue.readBytes(buffer);
                        case Type.BYTE:
                                return DefaultByteValue.readBytes(buffer);
                        case Type.COLLECTION:
                                return DefaultValueCollection.readBytes(buffer);
                        case Type.DATE:
                                return DefaultDateValue.readBytes(buffer);
                        case Type.DOUBLE:
                                return DefaultDoubleValue.readBytes(buffer);
                        case Type.FLOAT:
                                return DefaultFloatValue.readBytes(buffer);
                        case Type.GEOMETRY:
                        case Type.GEOMETRYCOLLECTION:
                        case Type.POINT:
                        case Type.LINESTRING:
                        case Type.POLYGON:
                        case Type.MULTILINESTRING:
                        case Type.MULTIPOINT:
                        case Type.MULTIPOLYGON:
                                return DefaultGeometryValue.readBytes(buffer);
                        case Type.INT:
                                return DefaultIntValue.readBytes(buffer);
                        case Type.LONG:
                                return DefaultLongValue.readBytes(buffer);
                        case Type.NULL:
                                return NullValue.NULL;
                        case Type.RASTER:
                                return DefaultRasterValue.readBytes(buffer);
                        case Type.SHORT:
                                return DefaultShortValue.readBytes(buffer);
                        case Type.STRING:
                                return DefaultStringValue.readBytes(buffer);
                        case Type.TIME:
                                return DefaultTimeValue.readBytes(buffer);
                        case Type.TIMESTAMP:
                                return DefaultTimestampValue.readBytes(buffer);
                        default:
                                throw new IllegalArgumentException("Wrong type: " + valueType);
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
                if (valueType == Type.RASTER) {
                        return DefaultRasterValue.readBytes(buffer, byteProvider);
                } else {
                        throw new IllegalArgumentException("Wrong type: " + valueType);
                }
        }

        public static int getRasterHeaderSize() {
                return DefaultRasterValue.getHeaderSize();
        }

        private ValueFactory() {
        }
}
