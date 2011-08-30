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

import com.vividsolutions.jts.io.ParseException;
import org.junit.Before;
import org.junit.Test;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import org.gdms.Geometries;
import org.gdms.data.types.Type;
import org.gdms.data.types.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.text.DateFormat;
import java.util.Locale;

import static org.junit.Assert.*;

public class ValuesTest {

        private java.sql.Date d;

        @Test
        public void testArrayValue() throws Exception {
                Value[] v = new Value[7];

                for (int i = 0; i < v.length; i++) {
                        v[i] = ValueFactory.createValue(i);
                }

                ValueCollection av = ValueFactory.createValue(v);

                for (int i = 0; i < v.length; i++) {
                        v[i] = ValueFactory.createValue(i);
                }

                ValueCollection av2 = ValueFactory.createValue(v);

                assertTrue((av.equals(av2)).getAsBoolean());
                assertEquals(av.hashCode(), av2.hashCode());

                for (int i = 0; i < 7; i++) {
                        assertTrue((av.get(i).equals(ValueFactory.createValue(i))).getAsBoolean());
                }
        }

        /**
         * Tests the NullValues operations
         */
        @Test
        public void testNullValueOperations() throws Exception {
                Value n = ValueFactory.createNullValue();


                ValueCollection b = ValueFactory.createValue(new Value[0]);
                assertFalse((b.equals(n)).getAsBoolean());
                assertFalse((b.notEquals(n)).getAsBoolean());

                Value v = ValueFactory.createValue(true);
                v.and(n);
                v.or(n);
                // SQL UNKNOWN
                assertNull((v.equals(n)).getAsBoolean());
                assertNull((v.notEquals(n)).getAsBoolean());

                Value i = ValueFactory.createValue(1);
                i.equals(n);
                i.notEquals(n);
                assertTrue((i.less(n)).isNull());
                assertTrue((i.lessEqual(n)).isNull());
                assertTrue((i.greater(n)).isNull());
                assertTrue((i.greaterEqual(n)).isNull());

                Value s = ValueFactory.createValue("test");
                assertTrue((s.equals(n)).isNull());
                assertTrue((s.notEquals(n)).isNull());
                assertTrue((s.less(n)).isNull());
                assertTrue((s.lessEqual(n)).isNull());
                assertTrue((s.greater(n)).isNull());
                assertTrue((s.greaterEqual(n)).isNull());
                s.like(n);

                Value d = ValueFactory.createValue(new Date());
                assertTrue((d.equals(n)).isNull());
                assertTrue((d.notEquals(n)).isNull());
                assertTrue((d.less(n)).isNull());
                assertTrue((d.lessEqual(n)).isNull());
                assertTrue((d.greater(n)).isNull());
                assertTrue((d.greaterEqual(n)).isNull());

                Value t = ValueFactory.createValue(new Time(12));
                assertTrue((t.equals(n)).isNull());
                assertTrue((t.notEquals(n)).isNull());
                assertTrue((t.less(n)).isNull());
                assertTrue((t.lessEqual(n)).isNull());
                assertTrue((t.greater(n)).isNull());
                assertTrue((t.greaterEqual(n)).isNull());

                Value ts = ValueFactory.createValue(new Timestamp(12));
                assertTrue((ts.equals(n)).isNull());
                assertTrue((ts.notEquals(n)).isNull());
                assertTrue((ts.less(n)).isNull());
                assertTrue((ts.lessEqual(n)).isNull());
                assertTrue((ts.greater(n)).isNull());
                assertTrue((ts.greaterEqual(n)).isNull());

                assertTrue((n.equals(n)).isNull());
                assertTrue((n.notEquals(n)).isNull());
                assertTrue((n.less(n)).isNull());
                assertTrue((n.lessEqual(n)).isNull());
                assertTrue((n.greater(n)).isNull());
                assertTrue((n.greaterEqual(n)).isNull());
                n.like(n);

        }

        /**
         * DOCUMENT ME!
         *
         * @throws IncompatibleTypesException
         *             DOCUMENT ME!
         */
        @Test
        public void testStringValueEquals() throws IncompatibleTypesException {
                Value v1 = ValueFactory.createValue("hola");
                Value v2 = ValueFactory.createValue("hola");
                Value v3 = ValueFactory.createValue("holA");
                assertTrue((v1.equals(v2)).getAsBoolean());
                assertFalse((v1.equals(v3)).getAsBoolean());
                assertFalse((v2.equals(v3)).getAsBoolean());
        }

        /**
         * DOCUMENT ME!
         */
        @Test
        public void testEscape() {
                assertEquals(ValueWriterImpl.escapeString("pp'pp"), "pp''pp");
                assertEquals(ValueWriterImpl.escapeString("pp''pp"), "pp''''pp");
        }

        /**
         * DOCUMENT ME!
         *
         * @throws Exception
         *             DOCUMENT ME!
         */
        @Test
        public void testCreateByType() throws Exception {
                assertTrue((ValueFactory.createValueByType("1",
                        Type.LONG).equals(ValueFactory.createValue(1L))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType("true",
                        Type.BOOLEAN).equals(ValueFactory.createValue(true))).getAsBoolean());
                assertTrue((ValueFactory.createValueByType("false",
                        Type.BOOLEAN).equals(ValueFactory.createValue(false))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType("carajo",
                        Type.STRING).equals(ValueFactory.createValue("carajo"))).getAsBoolean());

                Calendar c = Calendar.getInstance();

                // month is 0-based
                c.set(1980, 8, 5, 0, 0, 0);
                c.set(Calendar.MILLISECOND, 0);

                assertTrue((ValueFactory.createValueByType(d.toString(),
                        Type.DATE).equals(ValueFactory.createValue(d))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType(
                        NumberFormat.getNumberInstance(Locale.ROOT).format(1.1), Type.DOUBLE).equals(ValueFactory.createValue(1.1d))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType("1", Type.INT).equals(
                        ValueFactory.createValue(1))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType(
                        NumberFormat.getNumberInstance(Locale.ROOT).format(1.1), Type.FLOAT).equals(ValueFactory.createValue(1.1f))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType("1",
                        Type.SHORT).equals(ValueFactory.createValue(1))).getAsBoolean());

                assertTrue((ValueFactory.createValueByType("1",
                        Type.BYTE).equals(ValueFactory.createValue(1))).getAsBoolean());

                byte[] array = new byte[]{(byte) 255, (byte) 160, (byte) 7};
                assertTrue((ValueFactory.createValueByType("FFA007",
                        Type.BINARY).equals(ValueFactory.createValue(array))).getAsBoolean());

                c.set(1970, 0, 1, 22, 45, 00);
                c.set(Calendar.MILLISECOND, 0);

                Time t = new Time(c.getTime().getTime());
                assertTrue((ValueFactory.createValueByType(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t),
                        Type.TIME).equals(ValueFactory.createValue(t))).getAsBoolean());

                c.set(1970, 0, 1, 22, 45, 20);
                c.set(Calendar.MILLISECOND, 2345);

                Timestamp ts = new Timestamp(c.getTime().getTime());
                assertTrue((ValueFactory.createValueByType(
                        ts.toString(), Type.TIMESTAMP).equals(
                        ValueFactory.createValue(ts))).getAsBoolean());
        }

        @Test
        public void testToStringFromStringCoherente() throws Exception {
                Value v = ValueFactory.createValue(1300.5566d);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.DOUBLE))).getAsBoolean());

                v = ValueFactory.createValue(13.5f);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.FLOAT))).getAsBoolean());

                v = ValueFactory.createValue(1300L);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.LONG))).getAsBoolean());

                v = ValueFactory.createValue(false);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.BOOLEAN))).getAsBoolean());

                v = ValueFactory.createValue("hola");
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.STRING))).getAsBoolean());

                Calendar c = Calendar.getInstance();

                // month is 0-based
                c.set(1980, 8, 5, 0, 0, 0);
                c.set(Calendar.MILLISECOND, 0);

                v = ValueFactory.createValue(d);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.DATE))).getAsBoolean());

                v = ValueFactory.createValue(15);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.INT))).getAsBoolean());

                v = ValueFactory.createValue((short) 13);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.SHORT))).getAsBoolean());

                v = ValueFactory.createValue((byte) 5);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.BYTE))).getAsBoolean());

                v = ValueFactory.createValue(new byte[]{4, 5, 7, 8, 3, 8});
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.BINARY))).getAsBoolean());

                c.set(1970, 0, 1, 22, 45, 20);
                c.set(Calendar.MILLISECOND, 0);

                Time t = new Time(c.getTime().getTime());
                v = ValueFactory.createValue(t);
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.TIME))).getAsBoolean());

                v = ValueFactory.createValue(new Timestamp(2465));
                assertTrue((v.equals(ValueFactory.createValueByType(v.toString(), Type.TIMESTAMP))).getAsBoolean());
        }

        @Test
        public void testDecimalDigits() throws Exception {
                assertEquals(((NumericValue) ValueFactory.createValue(2.3d)).getDecimalDigitsCount(), 1);
                assertEquals(((NumericValue) ValueFactory.createValue(2d)).getDecimalDigitsCount(), 0);
                assertEquals(((NumericValue) ValueFactory.createValue(23)).getDecimalDigitsCount(), 0);
                assertEquals(((NumericValue) ValueFactory.createValue(2.030f)).getDecimalDigitsCount(), 2);
                assertEquals(((NumericValue) ValueFactory.createValue(2.00000000002d)).getDecimalDigitsCount(), 11);
        }

        @Test
        public void testValuesTypes() throws Exception {
                assertEquals(ValueFactory.createValue(false).getType(), Type.BOOLEAN);
                assertEquals(ValueFactory.createValue(new byte[]{2, 3}).getType(), Type.BINARY);
                assertEquals(ValueFactory.createValue(new Date()).getType(), Type.DATE);
                assertEquals(ValueFactory.createValue(3.0d).getType(), Type.DOUBLE);
                assertEquals(ValueFactory.createValue(3.5f).getType(), Type.FLOAT);
                assertEquals(ValueFactory.createValue(4).getType(), Type.INT);
                assertEquals(ValueFactory.createValue(4L).getType(), Type.LONG);
                assertEquals(ValueFactory.createValue("").getType(), Type.STRING);
                assertEquals(ValueFactory.createValue(new Time(1)).getType(), Type.TIME);
                assertEquals(ValueFactory.createValue(new Timestamp(1)).getType(), Type.TIMESTAMP);
        }

        @Test
        public void testBinaryValueConversion() throws Exception {
                Value binary = ValueFactory.createValue(new byte[]{3, 5, 7});
                Set<Integer> set = new HashSet<Integer>();
                set.add(0);
                checkConversions(binary, set);
        }

        @Test
        public void testBooleanValueConversion() throws Exception {
                Value value = ValueFactory.createValue(false);
                Set<Integer> set = new HashSet<Integer>();
                set.add(1);
                checkConversions(value, set);
        }

        @Test
        public void testByteValueConversion() throws Exception {
                Value value = ValueFactory.createValue((byte) 4);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testDateValueConversion() throws Exception {
                Value value = ValueFactory.createValue(new Date());
                Set<Integer> set = new HashSet<Integer>();
                set.add(3);
                checkConversions(value, set);
        }

        @Test
        public void testDoubleValueConversion() throws Exception {
                Value value = ValueFactory.createValue(4.3d);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testFloatValueConversion() throws Exception {
                Value value = ValueFactory.createValue(3.7f);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testGeometryValueConversion() throws Exception {
                Value value = ValueFactory.createValue(Geometries.getMultiPoint3D());
                Set<Integer> set = new HashSet<Integer>();
                set.add(6);
                checkConversions(value, set);
        }

        @Test
        public void testIntValueConversion() throws Exception {
                Value value = ValueFactory.createValue(Integer.MAX_VALUE);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testLongValueConversion() throws Exception {
                Value value = ValueFactory.createValue(Long.MAX_VALUE);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testShortValueConversion() throws Exception {
                Value value = ValueFactory.createValue(Short.MAX_VALUE);
                Set<Integer> set = new HashSet<Integer>();
                set.add(2);
                set.add(4);
                set.add(5);
                set.add(7);
                set.add(8);
                set.add(9);
                checkConversions(value, set);
        }

        @Test
        public void testStringValueConversion() throws Exception {
                Value value = ValueFactory.createValue("gdms");
                Set<Integer> set = new HashSet<Integer>();
                set.add(10);
                checkConversions(value, set);
        }

        @Test
        public void testTimeValueConversion() throws Exception {
                Value value = ValueFactory.createValue(new Time(System.currentTimeMillis()));
                Set<Integer> set = new HashSet<Integer>();
                set.add(11);
                checkConversions(value, set);
        }

        @Test
        public void testTimestampValueConversion() throws Exception {
                Value value = ValueFactory.createValue(new Timestamp(System.currentTimeMillis()));
                Set<Integer> set = new HashSet<Integer>();
                set.add(12);
                checkConversions(value, set);
        }

        @Test
        public void testValueCollectionConversion() throws Exception {
                Value value = ValueFactory.createValue(new Value[]{ValueFactory.createValue(2d),
                                ValueFactory.createValue("hello")});
                Set<Integer> set = new HashSet<Integer>();
                set.add(13);
                checkConversions(value, set);
        }

        @Test
        public void testNullValueConversion() throws Exception {
                Value value = ValueFactory.createNullValue();
                Set<Integer> set = new HashSet<Integer>();
                for (int i = 0; i < 14; i++) {
                        set.add(i);
                }
                checkConversions(value, set);
        }

        private void checkConversions(Value value, Set<Integer> a) {
                for (int i = 0; i < 13; i++) {
                        switch (i) {
                                case 0:
                                        if (a.contains(i)) {
                                                value.getAsBinary();
                                        } else {
                                                try {
                                                        value.getAsBinary();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 1:
                                        if (a.contains(i)) {
                                                value.getAsBoolean();
                                        } else {
                                                try {
                                                        value.getAsBoolean();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 2:
                                        if (a.contains(i)) {
                                                value.getAsByte();
                                        } else {
                                                try {
                                                        value.getAsByte();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 3:
                                        if (a.contains(i)) {
                                                value.getAsDate();
                                        } else {
                                                try {
                                                        value.getAsDate();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 4:
                                        if (a.contains(i)) {
                                                value.getAsDouble();
                                        } else {
                                                try {
                                                        value.getAsDouble();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 5:
                                        if (a.contains(i)) {
                                                value.getAsFloat();
                                        } else {
                                                try {
                                                        value.getAsFloat();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 6:
                                        if (a.contains(i)) {
                                                value.getAsGeometry();
                                        } else {
                                                try {
                                                        value.getAsGeometry();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 7:
                                        if (a.contains(i)) {
                                                value.getAsInt();
                                        } else {
                                                try {
                                                        value.getAsInt();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 8:
                                        if (a.contains(i)) {
                                                value.getAsLong();
                                        } else {
                                                try {
                                                        value.getAsLong();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 9:
                                        if (a.contains(i)) {
                                                value.getAsShort();
                                        } else {
                                                try {
                                                        value.getAsShort();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 10:
                                        if (a.contains(i)) {
                                                value.getAsString();
                                        } else {
                                                try {
                                                        value.getAsString();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 11:
                                        if (a.contains(i)) {
                                                value.getAsTime();
                                        } else {
                                                try {
                                                        value.getAsTime();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 12:
                                        if (a.contains(i)) {
                                                value.getAsTimestamp();
                                        } else {
                                                try {
                                                        value.getAsTimestamp();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                                        break;
                                case 13:
                                        if (a.contains(i)) {
                                                value.getAsValueCollection();
                                        } else {
                                                try {
                                                        value.getAsValueCollection();
                                                        fail();
                                                } catch (IncompatibleTypesException e) {
                                                }
                                        }
                        }

                }
        }

        //
        // public void testValueConversion() throws Exception {
        // Value intValue = ValueFactory.createValue(3);
        // Value doubleValue = ValueFactory.createValue(3.5d);
        // Value stringValue = ValueFactory.createValue("12");
        // Value boolStringValue = ValueFactory.createValue("false");
        // Value booleanValue = ValueFactory.createValue(true);
        // long longValue = System.currentTimeMillis();
        // Value timeValue = ValueFactory.createValue(new Time(longValue));
        // Value dateValue = ValueFactory.createValue(new Date(longValue));
        //
        // checkToString(intType, stringType, intValue);
        // checkToString(intType, doubleType, intValue);
        // checkToString(doubleType, stringType, doubleValue);
        // checkToString(stringType, intType, stringValue);
        // checkToString(stringType, booleanType, boolStringValue);
        // checkToString(booleanType, stringType, booleanValue);
        //
        // BooleanValue bv =  dateValue.toType(
        // timeType.getTypeCode()).equals(timeValue);
        // assertTrue(bv.getAsBoolean());
        // }
        //
        // private void checkToString(Type firstType, Type secondType, Value value)
        // throws IncompatibleTypesException {
        // Value newValue = value.toType(secondType.getTypeCode()).toType(
        // firstType.getTypeCode());
        // assertEquals(( newValue,value));
        // }
        @Test
        public void testValuesIO() throws Exception {
                Value v;
                v = ValueFactory.createValue(false);
                checkIO(v);
                v = ValueFactory.createValue(new byte[]{2, 3, 6});
                checkIO(v);
                v = ValueFactory.createValue(new Date());
                checkIO(v);
                v = ValueFactory.createValue((short) 32700);
                checkIO(v);
                v = ValueFactory.createValue(421359827);
                checkIO(v);
                v = ValueFactory.createValue(1080131636);
                checkIO(v);
                v = ValueFactory.createValue(3.0975525d);
                checkIO(v);
                v = ValueFactory.createValue(3.52345f);
                checkIO(v);
                v = ValueFactory.createValue(8223372036854780000L);
                checkIO(v);
                v = ValueFactory.createValue("asdg");
                checkIO(v);
                v = ValueFactory.createValue(new Time(1));
                checkIO(v);
                v = ValueFactory.createValue(new Timestamp(1));
                checkIO(v);
                v = ValueFactory.createValue(new GeometryFactory().createPoint(new Coordinate(10, 10, 10)));
                checkIO(v);
                v = ValueFactory.createValue(new GeometryFactory().createPoint(new Coordinate(10, 10)));
                checkIO(v);
        }

        @Test
        public void testCheckByteRasterIO() throws Exception {
                RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
                byte[] bytePixels = new byte[]{60, 120, (byte) 190, (byte) 240};
                GeoRaster grBytes = GeoRasterFactory.createGeoRaster(bytePixels,
                        rasterMetadata);
                GeoRaster gr = checkRasterMetadataIO(grBytes);
                byte[] savedPixels = gr.getBytePixels();
                assertEquals(savedPixels.length, bytePixels.length);
                for (int i = 0; i < savedPixels.length; i++) {
                        assertEquals(i + "", savedPixels[i], bytePixels[i]);
                }
        }

        @Test
        public void testCheckShortRasterIO() throws Exception {
                RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
                short[] shortPixels = new short[]{1, 20000, (short) 40000,
                        (short) 60000};
                GeoRaster grBytes = GeoRasterFactory.createGeoRaster(shortPixels,
                        rasterMetadata);
                GeoRaster gr = checkRasterMetadataIO(grBytes);
                short[] savedPixels = gr.getShortPixels();
                assertEquals(savedPixels.length, shortPixels.length);
                for (int i = 0; i < savedPixels.length; i++) {
                        assertEquals(i + "", savedPixels[i], shortPixels[i]);
                }
        }

        @Test
        public void testCheckFloatRasterIO() throws Exception {
                RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
                float[] floatPixels = new float[]{1.2f, 2000123.2f, -322225.2f, 4.3f};
                GeoRaster grBytes = GeoRasterFactory.createGeoRaster(floatPixels,
                        rasterMetadata);
                GeoRaster gr = checkRasterMetadataIO(grBytes);
                float[] savedPixels = gr.getFloatPixels();
                assertEquals(savedPixels.length, floatPixels.length);
                assertArrayEquals(savedPixels, floatPixels, 0);
        }

        @Test
        public void testCheckIntRasterIO() throws Exception {
                RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
                int[] intPixels = new int[]{1, Integer.MAX_VALUE / 2,
                        Integer.MIN_VALUE / 2, 4};
                GeoRaster grBytes = GeoRasterFactory.createGeoRaster(intPixels,
                        rasterMetadata);
                GeoRaster gr = checkRasterMetadataIO(grBytes);
                int[] savedPixels = gr.getIntPixels();
                assertEquals(savedPixels.length, intPixels.length);
                assertArrayEquals(savedPixels, intPixels);
        }

        private GeoRaster checkRasterMetadataIO(GeoRaster grSource) {
                Value v = ValueFactory.createValue(grSource);
                Value v2 = ValueFactory.createValue(v.getType(), v.getBytes());
                GeoRaster gr = v2.getAsRaster();
                assertEquals(gr.getMetadata(), grSource.getMetadata());

                return gr;
        }

        private void checkIO(Value v) throws IncompatibleTypesException {
                Value v2 = ValueFactory.createValue(v.getType(), v.getBytes());
                assertTrue((v2.equals(v)).getAsBoolean());
        }

        @Test(expected = ParseException.class)
        public void testEmptyStringIsNotValidGeometry() throws Exception {
                ValueFactory.createValueByType("", Type.GEOMETRY);
        }

        @Test
        public void test3DGeoms() throws Exception {
                GeometryFactory gf = new GeometryFactory();
                Coordinate[] coords2D = new Coordinate[]{new Coordinate(10, 10, 10),
                        new Coordinate(40, 10, 10), new Coordinate(40, 40, 10),
                        new Coordinate(10, 40, 10), new Coordinate(10, 10, 10),};
                Coordinate[] coords3D = new Coordinate[]{new Coordinate(10, 10),
                        new Coordinate(40, 10), new Coordinate(40, 40),
                        new Coordinate(10, 40), new Coordinate(10, 10),};

                Value p1 = ValueFactory.createValue(gf.createPoint(new Coordinate(10,
                        10, 10)));
                Value p2 = ValueFactory.createValue(gf.createPoint(new Coordinate(10,
                        10)));
                checkDifferent(p1, p2);

                LineString l1 = gf.createLineString(coords2D);
                p1 = ValueFactory.createValue(l1);
                LineString l2 = gf.createLineString(coords3D);
                p2 = ValueFactory.createValue(l2);
                checkDifferent(p1, p2);

                p1 = ValueFactory.createValue(gf.createMultiPoint(coords2D));
                p2 = ValueFactory.createValue(gf.createMultiPoint(coords3D));
                checkDifferent(p1, p2);

                Polygon pol1 = gf.createPolygon(gf.createLinearRing(coords2D), null);
                p1 = ValueFactory.createValue(pol1);
                Polygon pol2 = gf.createPolygon(gf.createLinearRing(coords3D), null);
                p2 = ValueFactory.createValue(pol2);
                checkDifferent(p1, p2);

                p1 = ValueFactory.createValue(gf.createMultiLineString(new LineString[]{l1}));
                p2 = ValueFactory.createValue(gf.createMultiLineString(new LineString[]{l2}));
                checkDifferent(p1, p2);

                p1 = ValueFactory.createValue(gf.createMultiPolygon(new Polygon[]{pol1}));
                p2 = ValueFactory.createValue(gf.createMultiPolygon(new Polygon[]{pol2}));
                checkDifferent(p1, p2);

        }

        private void checkDifferent(Value p1, Value p2) {
                assertFalse(p1.equals(p2).getAsBoolean());
                assertTrue(p1.equals(p1).getAsBoolean());
        }

        @Test
        public void testNullOperations() throws Exception {
                Value nullv = ValueFactory.createNullValue();
                Value numv = ValueFactory.createValue(4d);
                Value strv = ValueFactory.createValue("s");
                Value falsev = numv.less(numv);
                Value truev = numv.equals(numv);

                assertTrue(nullv.multiply(numv).isNull());
                assertTrue(numv.multiply(nullv).isNull());
                assertTrue(nullv.sum(numv).isNull());
                assertTrue(numv.sum(nullv).isNull());

                assertTrue(strv.like(nullv).isNull());
                assertTrue(nullv.like(strv).isNull());
                assertTrue(falsev.or(nullv).isNull());
                assertTrue(nullv.or(falsev).isNull());
                assertTrue(truev.and(nullv).isNull());
                assertTrue(nullv.and(truev).isNull());
                assertTrue(numv.greaterEqual(nullv).isNull());
                assertTrue(numv.equals(nullv).isNull());
                assertTrue(numv.notEquals(nullv).isNull());
                assertTrue(numv.less(nullv).isNull());
                assertTrue(numv.lessEqual(nullv).isNull());
                assertTrue(nullv.greaterEqual(numv).isNull());
                assertTrue(nullv.equals(numv).isNull());
                assertTrue(nullv.notEquals(numv).isNull());
                assertTrue(nullv.less(numv).isNull());
                assertTrue(nullv.lessEqual(numv).isNull());
        }

        @Test
        public void testNumericValueComparisons() throws Exception {

                Value v1 = ValueFactory.createValue(1);
                Value v2 = ValueFactory.createValue(18);
                Value v3 = ValueFactory.createValue(-5d);
                Value v4 = ValueFactory.createValue(100d);
                Value v5 = ValueFactory.createValue(18f);

                // same value
                assertEquals(v1.compareTo(v1), 0);

                // opposite comparisons
                assertEquals(v1.compareTo(v2), -1);
                assertEquals(v2.compareTo(v1), 1);

                // different types
                assertEquals(v1.compareTo(v3), 1);
                assertEquals(v2.compareTo(v4), -1);
                assertEquals(v2.compareTo(v5), 0);
        }

        @Test
        public void testBooleanComparisons() throws Exception {
                Value v1 = ValueFactory.createValue(true);
                Value v2 = ValueFactory.createValue(false);

                assertEquals(v1.compareTo(v1), 0);
                assertEquals(v2.compareTo(v2), 0);

                assertEquals(v1.compareTo(v2), 1);
                assertEquals(v2.compareTo(v1), -1);
        }

        @Test
        public void testTimeValuesComparisons() throws Exception {
                DateFormat dateInstance = new SimpleDateFormat("yyyy/MM/dd");
                Value v1 = ValueFactory.createValue(dateInstance.parse("2002/11/21"));
                Value v2 = ValueFactory.createValue(dateInstance.parse("2010/01/11"));

                assertEquals(v1.compareTo(v1), 0);
                assertEquals(v1.compareTo(v2), -1);
                assertEquals(v2.compareTo(v1), 1);

                v1 = ValueFactory.createValue(Time.valueOf("18:01:59"));
                v2 = ValueFactory.createValue(Time.valueOf("23:00:05"));

                assertEquals(v1.compareTo(v1), 0);
                assertEquals(v1.compareTo(v2), -1);
                assertEquals(v2.compareTo(v1), 1);

                v1 = ValueFactory.createValue(Timestamp.valueOf("2002-11-21 18:01:59"));
                v2 = ValueFactory.createValue(Timestamp.valueOf("2010-01-11 23:00:05"));

                assertEquals(v1.compareTo(v1), 0);
                assertEquals(v1.compareTo(v2), -1);
                assertEquals(v2.compareTo(v1), 1);

        }

        /**  
         * Test created to check that we effectively retrieve a good representation of  
         * empty multipolygons. indeed, a NullPointerException used to happen...  
         * @throws Exception  
         */
        @Test
        public void testGeometryCollectionStringRepresentation() throws Exception {
                GeometryFactory gf = new GeometryFactory();
                GeometryCollection mp = gf.createMultiPolygon(new Polygon[]{});
                Value val = ValueFactory.createValue(mp);
                String str = val.toString();
                assertEquals(str, "MULTIPOLYGON EMPTY");
                Polygon poly = gf.createPolygon(gf.createLinearRing(new Coordinate[]{}), new LinearRing[]{});
                assertTrue(poly.isEmpty());
                mp = gf.createMultiPolygon(new Polygon[]{poly,});
                val = ValueFactory.createValue(mp);
                str = val.toString();
                assertNotNull(str);
                Polygon polyBis = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                                new Coordinate(0, 0, 0),
                                new Coordinate(1, 1, 0),
                                new Coordinate(3, 4, 0),
                                new Coordinate(0, 0, 0),}), new LinearRing[]{});
                mp = gf.createMultiPolygon(new Polygon[]{poly, polyBis});
                val = ValueFactory.createValue(mp);
                str = val.toString();
                assertNotNull(str);
                GeometryCollection coll = gf.createGeometryCollection(new Geometry[]{
                                gf.createPolygon(gf.createLinearRing(new Coordinate[]{}), new LinearRing[]{}),
                                gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                                        new Coordinate(0, 0, 0),
                                        new Coordinate(1, 1, 0),
                                        new Coordinate(3, 4, 0),
                                        new Coordinate(0, 0, 0),}), new LinearRing[]{})
                        });
                mp = gf.createGeometryCollection(new Geometry[]{poly, coll, polyBis});
                val = ValueFactory.createValue(mp);
                str = val.toString();
                assertNotNull(str);
        }

        @Before
        public void setUp() throws Exception {
                d = new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd").parse(
                        "1980/2/12").getTime());
        }
}
