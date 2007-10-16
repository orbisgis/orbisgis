package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class ValuesTest extends SourceTest {
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

	/**
	 * DOCUMENT ME!
	 *
	 * @param dsName
	 *            DOCUMENT ME!
	 *
	 * @throws NoSuchTableException
	 *             DOCUMENT ME!
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws DataSourceCreationException
	 * @throws DriverLoadException
	 */
	private void doTestNullValues(String dsName) throws NoSuchTableException,
			DriverException, DriverLoadException, DataSourceCreationException {
		DataSource d = dsf.getDataSource(dsName);
		d.open();

		for (int i = 0; i < d.getRowCount(); i++) {
			for (int j = 0; j < d.getMetadata().getFieldCount(); j++) {
				assertTrue(d.getFieldValue(i, j) != null);
				assertFalse(d.getFieldValue(i, j).toString().equals("'null'"));
			}
		}

		d.cancel();
	}

	/**
	 * Tests the DataSources never return null instead of NullValue
	 *
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testNullValues() throws Throwable {
		String[] resources = super.getSmallResourcesWithNullValues();
		for (String ds : resources) {
			doTestNullValues(ds);
		}

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
		StringValue v1 = ValueFactory.createValue("hola");
		StringValue v2 = ValueFactory.createValue("hola");
		StringValue v3 = ValueFactory.createValue("holA");
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

		c.set(1970, 0, 1, 22, 45, 20);
		c.set(Calendar.MILLISECOND, 0);

		Time t = new Time(c.getTime().getTime());
		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				DateFormat.getTimeInstance().format(t), Type.TIME).equals(
				ValueFactory.createValue(t))).getValue());

		c.set(1970, 0, 1, 22, 45, 20);
		c.set(Calendar.MILLISECOND, 2345);

		Timestamp ts = new Timestamp(c.getTime().getTime());
		assertTrue(((BooleanValue) ValueFactory.createValueByType(
				ts.toString(), Type.TIMESTAMP).equals(
				ValueFactory.createValue(ts))).getValue());
	}

	public void testToStringFromStringCoherente() throws Exception {
		Value v = ValueFactory.createValue(13.5d);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.DOUBLE))).getValue());

		v = ValueFactory.createValue(13.5f);
		assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v
				.toString(), Type.FLOAT))).getValue());

		v = ValueFactory.createValue(13L);
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
	}

	private void checkIO(Value v) throws IncompatibleTypesException {
		Value v2 = ValueFactory.createValue(v.getType(), v.getBytes());
		assertTrue(((BooleanValue) v2.equals(v)).getValue());
	}

	@Override
	protected void setUp() throws Exception {
		d = new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd").parse(
				"1980/2/12").getTime());
		setWritingTests(false);
		super.setUp();
	}
}
