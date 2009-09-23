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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.data.types.Type;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class ValuesTest extends TestCase {
	private java.sql.Date d;

	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
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

		assertTrue(((BooleanValue) av.equals(av2)).getValue());
		assertTrue(av.hashCode() == av2.hashCode());

		for (int i = 0; i < 7; i++) {
			assertTrue(((BooleanValue) av.get(i).equals(
					ValueFactory.createValue(i))).getValue());
		}
	}

	public void testDoubleValue() {

		String value = "0.05";

		Value v = ValueFactory.createValue(value, SQLEngineConstants.FLOATING_POINT_LITERAL);
		assertTrue(v.getAsDouble() == 0.05);
	}

	/**
	 * Tests the NullValues operations
	 */
	public void testNullValueOperations() {
		Value n = ValueFactory.createNullValue();

		try {
			ValueCollection b = ValueFactory.createValue(new Value[0]);
			assertFalse(((BooleanValue) b.equals(n)).getValue());
			assertFalse(((BooleanValue) b.notEquals(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value b = ValueFactory.createValue(true);
			b.and(n);
			b.or(n);
			assertFalse(((BooleanValue) b.equals(n)).getValue());
			assertFalse(((BooleanValue) b.notEquals(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value i = ValueFactory.createValue(1);
			i.equals(n);
			i.notEquals(n);
			assertFalse(((BooleanValue) i.less(n)).getValue());
			assertFalse(((BooleanValue) i.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) i.greater(n)).getValue());
			assertFalse(((BooleanValue) i.greaterEqual(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value s = ValueFactory.createValue("test");
			assertFalse(((BooleanValue) s.equals(n)).getValue());
			assertFalse(((BooleanValue) s.notEquals(n)).getValue());
			assertFalse(((BooleanValue) s.less(n)).getValue());
			assertFalse(((BooleanValue) s.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) s.greater(n)).getValue());
			assertFalse(((BooleanValue) s.greaterEqual(n)).getValue());
			s.like(n);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value d = ValueFactory.createValue(new Date());
			assertFalse(((BooleanValue) d.equals(n)).getValue());
			assertFalse(((BooleanValue) d.notEquals(n)).getValue());
			assertFalse(((BooleanValue) d.less(n)).getValue());
			assertFalse(((BooleanValue) d.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) d.greater(n)).getValue());
			assertFalse(((BooleanValue) d.greaterEqual(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value t = ValueFactory.createValue(new Time(12));
			assertFalse(((BooleanValue) t.equals(n)).getValue());
			assertFalse(((BooleanValue) t.notEquals(n)).getValue());
			assertFalse(((BooleanValue) t.less(n)).getValue());
			assertFalse(((BooleanValue) t.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) t.greater(n)).getValue());
			assertFalse(((BooleanValue) t.greaterEqual(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			Value ts = ValueFactory.createValue(new Timestamp(12));
			assertFalse(((BooleanValue) ts.equals(n)).getValue());
			assertFalse(((BooleanValue) ts.notEquals(n)).getValue());
			assertFalse(((BooleanValue) ts.less(n)).getValue());
			assertFalse(((BooleanValue) ts.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) ts.greater(n)).getValue());
			assertFalse(((BooleanValue) ts.greaterEqual(n)).getValue());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}

		try {
			assertFalse(((BooleanValue) n.equals(n)).getValue());
			assertFalse(((BooleanValue) n.notEquals(n)).getValue());
			assertFalse(((BooleanValue) n.less(n)).getValue());
			assertFalse(((BooleanValue) n.lessEqual(n)).getValue());
			assertFalse(((BooleanValue) n.greater(n)).getValue());
			assertFalse(((BooleanValue) n.greaterEqual(n)).getValue());
			n.like(n);

			assertTrue(true);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public void testStringValueEquals() throws IncompatibleTypesException {
		Value v1 = ValueFactory.createValue("hola");
		Value v2 = ValueFactory.createValue("hola");
		Value v3 = ValueFactory.createValue("holA");
		assertTrue(((BooleanValue) v1.equals(v2)).getValue());
		assertFalse(((BooleanValue) v1.equals(v3)).getValue());
		assertFalse(((BooleanValue) v2.equals(v3)).getValue());
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testEscape() {
		assertTrue(ValueWriterImpl.escapeString("pp'pp").equals("pp''pp"));
		assertTrue(ValueWriterImpl.escapeString("pp''pp").equals("pp''''pp"));
	}

	public void testBooleanComparations() {
		Value vTrue = ValueFactory.createValue(true);
		Value vFalse = ValueFactory.createValue(false);
		try {
			assertTrue(!((BooleanValue) vTrue.greater(vTrue)).getValue());
			assertTrue(((BooleanValue) vTrue.greater(vFalse)).getValue());
			assertTrue(!((BooleanValue) vFalse.greater(vTrue)).getValue());
			assertTrue(!((BooleanValue) vFalse.greater(vFalse)).getValue());
			assertTrue(((BooleanValue) vFalse.greaterEqual(vFalse)).getValue());
			assertTrue(((BooleanValue) vTrue.greaterEqual(vTrue)).getValue());
			assertTrue(((BooleanValue) vTrue.greaterEqual(vFalse)).getValue());
			assertTrue(!((BooleanValue) vFalse.greaterEqual(vTrue)).getValue());
			assertTrue(!((BooleanValue) vTrue.less(vTrue)).getValue());
			assertTrue(!((BooleanValue) vTrue.less(vFalse)).getValue());
			assertTrue(((BooleanValue) vFalse.less(vTrue)).getValue());
			assertTrue(!((BooleanValue) vFalse.less(vFalse)).getValue());
			assertTrue(((BooleanValue) vTrue.lessEqual(vTrue)).getValue());
			assertTrue(!((BooleanValue) vTrue.lessEqual(vFalse)).getValue());
			assertTrue(((BooleanValue) vFalse.lessEqual(vTrue)).getValue());
			assertTrue(((BooleanValue) vFalse.lessEqual(vFalse)).getValue());
		} catch (IncompatibleTypesException e) {
			assertTrue(false);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testCreateByType() throws Exception {
		assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
				Type.LONG).equals(ValueFactory.createValue(1L))).getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType("true",
				Type.BOOLEAN).equals(ValueFactory.createValue(true)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValueByType("false",
				Type.BOOLEAN).equals(ValueFactory.createValue(false)))
				.getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType("carajo",
				Type.STRING).equals(ValueFactory.createValue("carajo")))
				.getValue());

		Calendar c = Calendar.getInstance();

		// month is 0-based
		c.set(1980, 8, 5, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);

		assertTrue(((BooleanValue) ValueFactory.createValueByType(d.toString(),
				Type.DATE).equals(ValueFactory.createValue(d))).getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				NumberFormat.getNumberInstance().format(1.1), Type.DOUBLE)
				.equals(ValueFactory.createValue(1.1d))).getValue());

		assertTrue(((BooleanValue) ValueFactory
				.createValueByType("1", Type.INT).equals(
						ValueFactory.createValue(1))).getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				NumberFormat.getNumberInstance().format(1.1), Type.FLOAT)
				.equals(ValueFactory.createValue(1.1f))).getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
				Type.SHORT).equals(ValueFactory.createValue(1))).getValue());

		assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
				Type.BYTE).equals(ValueFactory.createValue(1))).getValue());

		byte[] array = new byte[] { (byte) 255, (byte) 160, (byte) 7 };
		assertTrue(((BooleanValue) ValueFactory.createValueByType("FFA007",
				Type.BINARY).equals(ValueFactory.createValue(array)))
				.getValue());

		c.set(1970, 0, 1, 22, 45, 00);
		c.set(Calendar.MILLISECOND, 0);

		Time t = new Time(c.getTime().getTime());
		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t),
				Type.TIME).equals(ValueFactory.createValue(t))).getValue());

		c.set(1970, 0, 1, 22, 45, 20);
		c.set(Calendar.MILLISECOND, 2345);

		Timestamp ts = new Timestamp(c.getTime().getTime());
		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				ts.toString(), Type.TIMESTAMP).equals(
				ValueFactory.createValue(ts))).getValue());
	}

	public void testToStringFromStringCoherente() throws Exception {
		Value v = ValueFactory.createValue(1300.5566d);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.DOUBLE))).getValue());

		v = ValueFactory.createValue(13.5f);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.FLOAT))).getValue());

		v = ValueFactory.createValue(1300L);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.LONG))).getValue());

		v = ValueFactory.createValue(false);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.BOOLEAN))).getValue());

		v = ValueFactory.createValue("hola");
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.STRING))).getValue());

		Calendar c = Calendar.getInstance();

		// month is 0-based
		c.set(1980, 8, 5, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);

		v = ValueFactory.createValue(d);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.DATE))).getValue());

		v = ValueFactory.createValue(15);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.INT))).getValue());

		v = ValueFactory.createValue((short) 13);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.SHORT))).getValue());

		v = ValueFactory.createValue((byte) 5);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.BYTE))).getValue());

		v = ValueFactory.createValue(new byte[] { 4, 5, 7, 8, 3, 8 });
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.BINARY))).getValue());

		c.set(1970, 0, 1, 22, 45, 20);
		c.set(Calendar.MILLISECOND, 0);

		Time t = new Time(c.getTime().getTime());
		v = ValueFactory.createValue(t);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.TIME))).getValue());

		v = ValueFactory.createValue(new Timestamp(2465));
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.TIMESTAMP))).getValue());
	}

	public void testDecimalDigits() throws Exception {
		assertTrue(((NumericValue) ValueFactory.createValue(2.3d))
				.getDecimalDigitsCount() == 1);
		assertTrue(((NumericValue) ValueFactory.createValue(2d))
				.getDecimalDigitsCount() == 0);
		assertTrue(((NumericValue) ValueFactory.createValue(23))
				.getDecimalDigitsCount() == 0);
		assertTrue(((NumericValue) ValueFactory.createValue(2.030f))
				.getDecimalDigitsCount() == 2);
		assertTrue(((NumericValue) ValueFactory.createValue(2.00000000002d))
				.getDecimalDigitsCount() == 11);
	}

	public void testValuesTypes() throws Exception {
		assertTrue(ValueFactory.createValue(false).getType() == Type.BOOLEAN);
		assertTrue(ValueFactory.createValue(new byte[] { 2, 3 }).getType() == Type.BINARY);
		assertTrue(ValueFactory.createValue(new Date()).getType() == Type.DATE);
		assertTrue(ValueFactory.createValue(3.0d).getType() == Type.DOUBLE);
		assertTrue(ValueFactory.createValue(3.5f).getType() == Type.FLOAT);
		assertTrue(ValueFactory.createValue(4).getType() == Type.INT);
		assertTrue(ValueFactory.createValue(4L).getType() == Type.LONG);
		assertTrue(ValueFactory.createValue("").getType() == Type.STRING);
		assertTrue(ValueFactory.createValue(new Time(1)).getType() == Type.TIME);
		assertTrue(ValueFactory.createValue(new Timestamp(1)).getType() == Type.TIMESTAMP);
	}

	public void testBinaryValueConversion() throws Exception {
		Value binary = ValueFactory.createValue(new byte[] { 3, 5, 7 });
		Set<Integer> set = new HashSet<Integer>();
		set.add(0);
		checkConversions(binary, set);
	}

	public void testBooleanValueConversion() throws Exception {
		Value value = ValueFactory.createValue(false);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		checkConversions(value, set);
	}

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

	public void testDateValueConversion() throws Exception {
		Value value = ValueFactory.createValue(new Date());
		Set<Integer> set = new HashSet<Integer>();
		set.add(3);
		checkConversions(value, set);
	}

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

	public void testGeometryValueConversion() throws Exception {
		Value value = ValueFactory.createValue(Geometries.getMultiPoint3D());
		Set<Integer> set = new HashSet<Integer>();
		set.add(6);
		checkConversions(value, set);
	}

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

	public void testStringValueConversion() throws Exception {
		Value value = ValueFactory.createValue("gdms");
		Set<Integer> set = new HashSet<Integer>();
		set.add(10);
		checkConversions(value, set);
	}

	public void testTimeValueConversion() throws Exception {
		Value value = ValueFactory.createValue(new Time(System
				.currentTimeMillis()));
		Set<Integer> set = new HashSet<Integer>();
		set.add(11);
		checkConversions(value, set);
	}

	public void testTimestampValueConversion() throws Exception {
		Value value = ValueFactory.createValue(new Timestamp(System
				.currentTimeMillis()));
		Set<Integer> set = new HashSet<Integer>();
		set.add(12);
		checkConversions(value, set);
	}

	public void testValueCollectionConversion() throws Exception {
		Value value = ValueFactory
				.createValue(new Value[] { ValueFactory.createValue(2d),
						ValueFactory.createValue("hello") });
		Set<Integer> set = new HashSet<Integer>();
		set.add(13);
		checkConversions(value, set);
	}

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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
						assertTrue(false);
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
	// BooleanValue bv = (BooleanValue) dateValue.toType(
	// timeType.getTypeCode()).equals(timeValue);
	// assertTrue(bv.getValue());
	// }
	//
	// private void checkToString(Type firstType, Type secondType, Value value)
	// throws IncompatibleTypesException {
	// Value newValue = value.toType(secondType.getTypeCode()).toType(
	// firstType.getTypeCode());
	// assertTrue(((BooleanValue) newValue.equals(value)).getValue());
	// }

	public void testValuesIO() throws Exception {
		Value v;
		v = ValueFactory.createValue(false);
		checkIO(v);
		v = ValueFactory.createValue(new byte[] { 2, 3, 6 });
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
		v = ValueFactory.createValue(new GeometryFactory()
				.createPoint(new Coordinate(10, 10, 10)));
		checkIO(v);
		v = ValueFactory.createValue(new GeometryFactory()
				.createPoint(new Coordinate(10, 10)));
		checkIO(v);
	}

	public void testCheckByteRasterIO() throws Exception {
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
		byte[] bytePixels = new byte[] { 60, 120, (byte) 190, (byte) 240 };
		GeoRaster grBytes = GeoRasterFactory.createGeoRaster(bytePixels,
				rasterMetadata);
		GeoRaster gr = checkRasterMetadataIO(grBytes);
		byte[] savedPixels = gr.getBytePixels();
		assertTrue(savedPixels.length == bytePixels.length);
		for (int i = 0; i < savedPixels.length; i++) {
			assertTrue(i + "", savedPixels[i] == bytePixels[i]);
		}
	}

	public void testCheckShortRasterIO() throws Exception {
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
		short[] shortPixels = new short[] { 1, 20000, (short) 40000,
				(short) 60000 };
		GeoRaster grBytes = GeoRasterFactory.createGeoRaster(shortPixels,
				rasterMetadata);
		GeoRaster gr = checkRasterMetadataIO(grBytes);
		short[] savedPixels = gr.getShortPixels();
		assertTrue(savedPixels.length == shortPixels.length);
		for (int i = 0; i < savedPixels.length; i++) {
			assertTrue(i + "", savedPixels[i] == shortPixels[i]);
		}
	}

	public void testCheckFloatRasterIO() throws Exception {
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
		float[] floatPixels = new float[] { 1.2f, 2000123.2f, -322225.2f, 4.3f };
		GeoRaster grBytes = GeoRasterFactory.createGeoRaster(floatPixels,
				rasterMetadata);
		GeoRaster gr = checkRasterMetadataIO(grBytes);
		float[] savedPixels = gr.getFloatPixels();
		assertTrue(savedPixels.length == floatPixels.length);
		for (int i = 0; i < savedPixels.length; i++) {
			assertTrue(i + "", savedPixels[i] == floatPixels[i]);
		}
	}

	public void testCheckIntRasterIO() throws Exception {
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 10, 10, 2, 2);
		int[] intPixels = new int[] { 1, Integer.MAX_VALUE / 2,
				Integer.MIN_VALUE / 2, 4 };
		GeoRaster grBytes = GeoRasterFactory.createGeoRaster(intPixels,
				rasterMetadata);
		GeoRaster gr = checkRasterMetadataIO(grBytes);
		int[] savedPixels = gr.getIntPixels();
		assertTrue(savedPixels.length == intPixels.length);
		for (int i = 0; i < savedPixels.length; i++) {
			assertTrue(i + "", savedPixels[i] == intPixels[i]);
		}
	}

	private GeoRaster checkRasterMetadataIO(GeoRaster grSource) {
		Value v = ValueFactory.createValue(grSource);
		Value v2 = ValueFactory.createValue(v.getType(), v.getBytes());
		GeoRaster gr = v2.getAsRaster();
		assertTrue(gr.getMetadata().equals(grSource.getMetadata()));

		return gr;
	}

	private void checkIO(Value v) throws IncompatibleTypesException {
		Value v2 = ValueFactory.createValue(v.getType(), v.getBytes());
		assertTrue(((BooleanValue) v2.equals(v)).getValue());
	}

	public void testEmptyStringIsNotValidGeometry() throws Exception {
		try {
			ValueFactory.createValueByType("", Type.GEOMETRY);
			assertTrue(false);
		} catch (ParseException e) {

		}
	}

	public void test3DGeoms() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coords2D = new Coordinate[] { new Coordinate(10, 10, 10),
				new Coordinate(40, 10, 10), new Coordinate(40, 40, 10),
				new Coordinate(10, 40, 10), new Coordinate(10, 10, 10), };
		Coordinate[] coords3D = new Coordinate[] { new Coordinate(10, 10),
				new Coordinate(40, 10), new Coordinate(40, 40),
				new Coordinate(10, 40), new Coordinate(10, 10), };

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

		p1 = ValueFactory.createValue(gf
				.createMultiLineString(new LineString[] { l1 }));
		p2 = ValueFactory.createValue(gf
				.createMultiLineString(new LineString[] { l2 }));
		checkDifferent(p1, p2);

		p1 = ValueFactory.createValue(gf
				.createMultiPolygon(new Polygon[] { pol1 }));
		p2 = ValueFactory.createValue(gf
				.createMultiPolygon(new Polygon[] { pol2 }));
		checkDifferent(p1, p2);

	}

	private void checkDifferent(Value p1, Value p2) {
		assertTrue(p1.equals(p2).getAsBoolean() == false);
		assertTrue(p1.equals(p1).getAsBoolean());
	}

	public void testNullOperations() throws Exception {
		Value nullv = ValueFactory.createNullValue();
		Value numv = ValueFactory.createValue(4d);
		// Value strv = ValueFactory.createValue("s");
		// Value falsev = numv.less(numv);
		// Value truev = numv.equals(numv);
		assertTrue(nullv.producto(numv).isNull());
		assertTrue(numv.producto(nullv).isNull());
		assertTrue(nullv.suma(numv).isNull());
		assertTrue(numv.suma(nullv).isNull());
		// TODO uncomment and fix this bad behavior
		// assertTrue(strv.like(nullv).isNull());
		// assertTrue(nullv.like(strv).isNull());
		// assertTrue(falsev.or(nullv).isNull());
		// assertTrue(nullv.or(falsev).isNull());
		// assertTrue(truev.and(nullv).isNull());
		// assertTrue(nullv.and(truev).isNull());
		// assertTrue(numv.greaterEqual(nullv).isNull());
		// assertTrue(numv.equals(nullv).isNull());
		// assertTrue(numv.notEquals(nullv).isNull());
		// assertTrue(numv.less(nullv).isNull());
		// assertTrue(numv.lessEqual(nullv).isNull());
	}

	@Override
	protected void setUp() throws Exception {
		d = new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd").parse(
				"1980/2/12").getTime());
		super.setUp();
	}
}
