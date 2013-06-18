/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import com.vividsolutions.jts.geom.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.stream.GeoStream;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.grap.model.GeoRaster;

/**
 * Factory to create {@link Value} instances from basic types.
 * 
 * This factory is independent from any running Gdms instance and provides values
 * for all of them.
 * 
 * @author Antoine Gourlay
 * @author Fernando Gonzalez Cortes
 */
public final class ValueFactory {

        private static final Logger LOG = Logger.getLogger(ValueFactory.class);
        public static final BooleanValue TRUE = new DefaultBooleanValue(true);
        public static final BooleanValue FALSE = new DefaultBooleanValue(false);
        static final StringValue EMPTYTEXT = new DefaultStringValue();

        /**
         * Creates a Value instance that contains the specified int value.
         *
         * @param n an int
         * @return a wrapper over the int
         */
        public static IntValue createValue(int n) {
                return new DefaultIntValue(n);
        }

        /**
         * Creates a Value instance that contains the specified long value.
         *
         * @param l a long
         * @return a wrapper of the long
         */
        public static LongValue createValue(long l) {
                return new DefaultLongValue(l);
        }

        /**
         * Creates a Value instance that contains the specified byte value.
         *
         * @param b a byte
         * @return a wrapper over the byte
         */
        public static ByteValue createValue(byte b) {
                return new DefaultByteValue(b);
        }

        /**
         * Creates a Value instance that contains the specified short value.
         *
         * @param s a short
         * @return a wrapper over the short
         */
        public static ShortValue createValue(short s) {
                return new DefaultShortValue(s);
        }

        /**
         * Creates a Value instance that contains the specified text (as a String value).
         *
         * @param s a string
         * @return a wrapper over the text
         */
        public static StringValue createValue(String s) {
                if (s != null) {
                        if (s.isEmpty()) {
                                return EMPTYTEXT;
                        } else {
                                return new DefaultStringValue(s);
                        }
                } else {
                        return createNullValue();
                }
        }
        
        /**
         * Creates a Value instance that contains the specified text (as a char array).
         * 
         * No reference to the char array <tt>s</tt> is kept, the array is copied.
         *
         * @param s a char array
         * @return a wrapper over the text
         */
        public static StringValue createValue(char[] s) {
                if (s != null) {
                        if (s.length == 0) {
                                return EMPTYTEXT;
                        } else {
                                return new DefaultStringValue(s);
                        }
                } else {
                        return createNullValue();
                }
        }
        
        /**
         * Creates a Value instance that contains a portion of the specified text (as a char array).
         * 
         * No reference to the char array <tt>s</tt> is kept, the required part of the array is copied.
         *
         * @param s a char array
         * @param start the start index in the array
         * @param length the length of the sub-array to keep
         * @return a wrapper over the text
         */
        public static StringValue createValue(char[] s, int start, int length) {
                if (s != null) {
                        if (s.length == 0 || length == 0) {
                                return EMPTYTEXT;
                        } else {
                                return new DefaultStringValue(s, start, length);
                        }
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified GeoStream value.
         *
         * @param gS a GeoStream object
         * @return a wrapper over the stream
         */
        public static StreamValue createValue(GeoStream gS) {
                if (gS != null) {
                        return new DefaultStreamValue(gS);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified float value.
         *
         * @param f a float
         * @return a wrapper over the float
         */
        public static FloatValue createValue(float f) {
                return new DefaultFloatValue(f);
        }

        /**
         * Creates a Value instance that contains the specified double value.
         *
         * @param d a double
         * @return a wrapper over the double
         */
        public static DoubleValue createValue(double d) {
                return new DefaultDoubleValue(d);
        }

        /**
         * Creates a Value instance that contains the specified date value.
         *
         * @param d a Date object
         * @return a wrapper over the Date
         */
        public static DateValue createValue(Date d) {
                if (d != null) {
                        return new DefaultDateValue(new java.sql.Date(d.getTime()));
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified time value.
         *
         * @param t a Time object
         * @return a wrapper over the Time
         */
        public static TimeValue createValue(Time t) {
                if (t != null) {
                        return new DefaultTimeValue(t);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified timestamp value.
         *
         * @param t a Timestamp object
         * @return a wrapper over the Timestamp
         */
        public static TimestampValue createValue(Timestamp t) {
                if (t != null) {
                        return new DefaultTimestampValue(t);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance that contains the specified boolean value.
         *
         * @param b a boolean
         * @return a wrapper over the boolean
         */
        public static BooleanValue createValue(boolean b) {
                return b ? TRUE : FALSE;
        }

        /**
         * Creates a heterogeneous Value collection.
         *
         * @param values some values
         * @return a wrapper value over the underlying array of values
         */
        public static ValueCollection createValue(Value[] values) {
                ValueCollection v = new DefaultValueCollection();
                v.setValues(values);

                return v;
        }

        /**
         * Instantiates a value of the specified type containing the value with the
         * specified textual representation.
         *
         * @param text textual representation of the value to instantiate
         * @param type typeCode of the value (must be one of the constants of the Type interface)
         * @return the parsed value, of the corresponding type
         * @throws ParseException if the textual representation cannot be converted to the
         * specified type
         * @throws NumberFormatException
         */
        public static Value createValueByType(String text, int type) throws ParseException {
                switch (type) {
                        case Type.LONG:
                                return createValue(Long.parseLong(text));

                        case Type.BOOLEAN:
                                return createValue(Boolean.valueOf(text));

                        case Type.DATE:
                                return DefaultDateValue.parseString(text);

                        case Type.DOUBLE:
                                return createValue(DecimalFormat.getNumberInstance(Locale.ROOT).parse(text).doubleValue());

                        case Type.INT:
                                return createValue(Integer.parseInt(text));

                        case Type.FLOAT:
                                return createValue(DecimalFormat.getNumberInstance(Locale.ROOT).parse(text).floatValue());

                        case Type.GEOMETRY:
                        case Type.GEOMETRYCOLLECTION:
                        case Type.POINT:
                        case Type.LINESTRING:
                        case Type.POLYGON:
                        case Type.MULTILINESTRING:
                        case Type.MULTIPOINT:
                        case Type.MULTIPOLYGON:
                                try {
                                        return DefaultGeometryValue.parseString(text, null);
                                } catch (com.vividsolutions.jts.io.ParseException e) {
                                        LOG.error("Error parsing geometry", e);
                                        throw new ParseException("Cannot parse geometry:" + e.getMessage(), -1);
                                }

                        case Type.SHORT:
                                return createValue(Short.parseShort(text));

                        case Type.BYTE:
                                return createValue(Byte.parseByte(text));

                        case Type.BINARY:

                                if (text.length() % 2 != 0) {
                                        throw new ParseException(
                                                "binary fields must have even number of characters.", 0);
                                }

                                byte[] array = new byte[text.length() / 2];

                                for (int i = 0; i < array.length; i++) {
                                        String theByte = text.substring(2 * i, (2 * i) + 2);
                                        array[i] = (byte) Integer.parseInt(theByte, 16);
                                }

                                return createValue(array);

                        case Type.TIMESTAMP:
                                try {
                                        return createValue(Timestamp.valueOf(text));
                                } catch (IllegalArgumentException e) {
                                        LOG.error("Error parsing Timestamp", e);
                                        throw new ParseException(e.getMessage(), -1);
                                }

                        case Type.TIME:
                                return DefaultTimeValue.parseString(text);

                        case Type.STRING:
                        default:
                                return createValue(text);
                }
        }

        /**
         * Creates a new null Value.
         *
         * @param <T>
         * @return NullValue
         */
        public static <T extends Value> T createNullValue() {
                return (T) NullValue.NULL;
        }

        /**
         * Gets a Value object with the value "v1 added to v2".
         *
         * @param v1 first value
         * @param v2 second value
         * @return a numeric value with the result of the operation
         */
        static NumericValue sum(NumericValue v1, NumericValue v2) {
                int type = getType(v1.getType(), v2.getType());

                switch (type) {
                        // The comment below is one the last of its kind, a memorial to the ancient time
                        // when this codebase was documentented and coded (including some variable
                        // and method names) in an ugly mix of catalan, french and(bad) english.
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
                                if (intValue == v1.longValue() + v2.longValue()) {
                                        return createValue(intValue);
                                }

                        case Type.LONG:
                                return createValue(v1.longValue() + v2.longValue());

                        case Type.FLOAT:
                                float floatValue = v1.floatValue() + v2.floatValue();
                                if (floatValue == v1.doubleValue() + v2.doubleValue()) {
                                        return createValue(floatValue);
                                }

                        case Type.DOUBLE:
                                return createValue(v1.doubleValue() + v2.doubleValue());
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
                // careful, boxing is necessary here, one of these can be null
                // the condition below + the one above carefully avoid that
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
                // careful, boxing is necessary here, one of these can be null
                // the condition below + the one above carefully avoid that
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
         * Gets a Value object with the value "v1 multiplied by v2".
         *
         * @param v1 first value
         * @param v2 second value
         * @return a numeric value with the result of the operation
         */
        static NumericValue product(NumericValue v1, NumericValue v2) {
                int type = getType(v1.getType(), v2.getType());

                switch (type) {
                        case Type.BYTE:
                        case Type.SHORT:
                        case Type.INT:

                                int intValue = v1.intValue() * v2.intValue();
                                if (intValue == v1.longValue() * v2.longValue()) {
                                        return createValue(intValue);
                                }

                        case Type.LONG:
                                return createValue(v1.longValue() * v2.longValue());

                        case Type.FLOAT:

                                float floatValue = v1.floatValue() * v2.floatValue();
                                if (floatValue == v1.doubleValue() * v2.doubleValue()) {
                                        return createValue(floatValue);
                                }

                        case Type.DOUBLE:
                                return createValue(v1.doubleValue() * v2.doubleValue());
                        default:
                                throw new IncompatibleTypesException("Cannot multiply these data types: "
                                        + v1.getType() + " and " + v2.getType());
                }
        }

        /**
         * Gets the inverse value (1/v) of the given (non-null) numeric value.
         *
         * @param v a numeric value
         * @return the inverse
         */
        static NumericValue inverse(NumericValue v) {
                if (v.getAsDouble() == 0) {
                        throw new ArithmeticException("Division by zero");
                } else {
                        return createValue(1 / v.doubleValue());
                }
        }

        /**
         * Creates a byte array value.
         *
         * @param bytes bytes of the value
         * @return a wrapper value over the byte array
         */
        public static BinaryValue createValue(byte[] bytes) {
                return new DefaultBinaryValue(bytes);
        }

        /**
         * Creates a Value instance over a geometry, with the given CRS.
         *
         * @param geom geometry
         * @param crs the CRS of the geometry
         * @return a wrapper value over the geometry
         */
        public static GeometryValue createValue(Geometry geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        if (geom instanceof Point) {
                                return createValue((Point) geom, crs);
                        } else if (geom instanceof LineString) {
                                return createValue((LineString) geom, crs);
                        } else if (geom instanceof Polygon) {
                                return createValue((Polygon) geom, crs);
                        } else if (geom instanceof MultiPoint) {
                                return createValue((MultiPoint) geom, crs);
                        } else if (geom instanceof MultiLineString) {
                                return createValue((MultiLineString) geom, crs);
                        } else if (geom instanceof MultiPolygon) {
                                return createValue((MultiPolygon) geom, crs);
                        } else if (geom instanceof GeometryCollection) {
                                return createValue((GeometryCollection) geom, crs);
                        } else {
                                throw new InvalidTypeException("Unknown geometry type: " + geom.getGeometryType());
                        }
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a geometry, with no defined CRS.
         *
         * @param geom geometry
         * @return a wrapper value over the geometry
         */
        public static GeometryValue createValue(Geometry geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a point, with the defined CRS.
         *
         * @param geom point
         * @param crs the CRS of the point
         * @return a wrapper value over the point
         */
        public static PointValue createValue(Point geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultPointValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a point, with no defined CRS.
         *
         * @param geom point
         * @return a wrapper value over the point
         */
        public static PointValue createValue(Point geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a line string, with the defined CRS.
         *
         * @param geom line string
         * @param crs the CRS of the line string
         * @return a wrapper value over the line string
         */
        public static LineStringValue createValue(LineString geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultLineStringValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a line string, with no defined CRS.
         *
         * @param geom line string
         * @return a wrapper value over the line string
         */
        public static LineStringValue createValue(LineString geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a polygon, with the defined CRS.
         *
         * @param geom a polygon
         * @param crs the CRS of the polygon
         * @return a wrapper value over the polygon
         */
        public static PolygonValue createValue(Polygon geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultPolygonValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a polygon, with no defined CRS.
         *
         * @param geom a polygon
         * @return a wrapper value over the polygon
         */
        public static PolygonValue createValue(Polygon geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a geometry collection, with the defined CRS.
         *
         * @param geom a geometry collection
         * @param crs the CRS of the geometry collection
         * @return a wrapper value over the geometry collection
         */
        public static GeometryCollectionValue createValue(GeometryCollection geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultGeometryCollectionValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a geometry collection, with no defined CRS.
         *
         * @param geom a geometry collection
         * @return a wrapper value over the geometry collection
         */
        public static GeometryCollectionValue createValue(GeometryCollection geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a multi-point, with the defined CRS.
         *
         * @param geom a multi-point
         * @param crs the CRS of the multi-point
         * @return a wrapper value over the multi-point
         */
        public static MultiPointValue createValue(MultiPoint geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultMultiPointValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a multi-point, with no defined CRS.
         *
         * @param geom a multi-point
         * @return a wrapper value over the multi-point
         */
        public static MultiPointValue createValue(MultiPoint geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a multi-line string, with the defined CRS.
         *
         * @param geom a multi-line string
         * @param crs the CRS of the multi-line string
         * @return a wrapper value over the multi-line string
         */
        public static MultiLineStringValue createValue(MultiLineString geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultMultiLineStringValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a multi-line string, with no defined CRS.
         *
         * @param geom a multi-line string
         * @return a wrapper value over the multi-line string
         */
        public static MultiLineStringValue createValue(MultiLineString geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a multi-polygon, with the defined CRS.
         *
         * @param geom a multi-polygon
         * @param crs the CRS of the multi-polygon
         * @return a wrapper value over the multi-polygon
         */
        public static MultiPolygonValue createValue(MultiPolygon geom, CoordinateReferenceSystem crs) {
                if (geom != null) {
                        return new DefaultMultiPolygonValue(geom, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a multi-polygon, with no defined CRS.
         *
         * @param geom a multi-polygon
         * @return a wrapper value over the multi-polygon
         */
        public static MultiPolygonValue createValue(MultiPolygon geom) {
                return createValue(geom, null);
        }

        /**
         * Creates a Value instance over a raster, with no defined CRS.
         *
         * @param raster a raster
         * @return a wrapper value over the raster
         */
        public static RasterValue createValue(GeoRaster raster) {
                if (raster != null) {
                        return new DefaultRasterValue(raster);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value instance over a raster, with the defined CRS.
         *
         * @param raster a raster
         * @param crs the CRS of the multi-polygon
         * @return a wrapper value over the raster
         */
        public static RasterValue createValue(GeoRaster raster, CoordinateReferenceSystem crs) {
                if (raster != null) {
                        return new DefaultRasterValue(raster, crs);
                } else {
                        return createNullValue();
                }
        }

        /**
         * Creates a Value from the specified bytes. 
         * 
         * Those bytes are the ones obtained by a call to {@link Value#getBytes() }.
         *
         * @param valueType the type of the value (one of the constants in the Type interface)
         * @param buffer the bytes representing the value
         * @return a new wrapper around the actual data in the byte buffer
         */
        public static Value createValue(final int valueType, byte[] buffer) {
                //In many cases, Type.GEOMETRY will be set with a concrete geometry type.
                //If this is so, we just keep Type.GEOMETRY, as the needed result
                //will be returned by createValue(Geometry).
                switch (valueType) {
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
                                return DefaultGeometryValue.readBytes(buffer, null);
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
         * Creates a lazily loaded value.
         * 
         * <p>
         * This creates a value of the specified type in two steps. The first one builds
         * quickly the value based on the byte[], the second asks for data to the
         * specified byteProvider to build the Value completely on demand.
         * </p>
         * <p>
         * Note that this method only supports Rasters right now.
         * </p>
         *
         * @param valueType the type of the value (one of the constants in the Type interface)
         * @param buffer some partial data
         * @param byteProvider a provider to get the rest of the value content when asked for
         * @return a lazy value of the requested type
         */
        public static Value createLazyValue(int valueType, byte[] buffer,
                ByteProvider byteProvider) {
                if (valueType == Type.RASTER) {
                        return DefaultRasterValue.readBytes(buffer, byteProvider);
                } else {
                        throw new IllegalArgumentException("Wrong type: " + valueType);
                }
        }

        /**
         * Gets the size, in bytes, of the header of a Raster object in a RasterValue.
         * 
         * This size if fixed for a particular platform, but can vary between platforms.
         * It is provided here so that file driver can know in advance this size, for
         * example for performance reasons.
         * 
         * @return the size in bytes of a raster header
         */
        public static int getRasterHeaderSize() {
                return DefaultRasterValue.getHeaderSize();
        }

        private ValueFactory() {
        }
}
