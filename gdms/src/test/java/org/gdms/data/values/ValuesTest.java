package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriterImpl;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class ValuesTest extends SourceTest {
    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
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
            assertTrue(((BooleanValue) av.get(i).equals(ValueFactory.createValue(
                        i))).getValue());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param dsName DOCUMENT ME!
     *
     * @throws NoSuchTableException DOCUMENT ME!
     * @throws DriverException DOCUMENT ME!
     * @throws DataSourceCreationException
     * @throws DriverLoadException
     */
    private void doTestNullValues(String dsName)
        throws NoSuchTableException, DriverException, DriverLoadException, DataSourceCreationException {
        DataSource d = dsf.getDataSource(dsName);
        d.open();

        for (int i = 0; i < d.getRowCount(); i++) {
            for (int j = 0; j < d.getDataSourceMetadata().getFieldCount(); j++) {
                assertTrue(d.getFieldValue(i, j) != null);
                assertFalse(d.getFieldValue(i, j).toString().equals("'null'"));
            }
        }

        d.cancel();
    }

    /**
     * Tests the DataSources never return null instead of NullValue
     *
     * @throws Throwable DOCUMENT ME!
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
            assertFalse(((BooleanValue)b.equals(n)).getValue());
            assertFalse(((BooleanValue)b.notEquals(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value b = ValueFactory.createValue(true);
            b.and(n);
            b.or(n);
            assertFalse(((BooleanValue)b.equals(n)).getValue());
            assertFalse(((BooleanValue)b.notEquals(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value i = ValueFactory.createValue(1);
            i.equals(n);
            i.notEquals(n);
            assertFalse(((BooleanValue)i.less(n)).getValue());
            assertFalse(((BooleanValue)i.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)i.greater(n)).getValue());
            assertFalse(((BooleanValue)i.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value s = ValueFactory.createValue("test");
            assertFalse(((BooleanValue)s.equals(n)).getValue());
            assertFalse(((BooleanValue)s.notEquals(n)).getValue());
            assertFalse(((BooleanValue)s.less(n)).getValue());
            assertFalse(((BooleanValue)s.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)s.greater(n)).getValue());
            assertFalse(((BooleanValue)s.greaterEqual(n)).getValue());
            s.like(n);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value d = ValueFactory.createValue(new Date());
            assertFalse(((BooleanValue)d.equals(n)).getValue());
            assertFalse(((BooleanValue)d.notEquals(n)).getValue());
            assertFalse(((BooleanValue)d.less(n)).getValue());
            assertFalse(((BooleanValue)d.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)d.greater(n)).getValue());
            assertFalse(((BooleanValue)d.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value t = ValueFactory.createValue(new Time(12));
            assertFalse(((BooleanValue)t.equals(n)).getValue());
            assertFalse(((BooleanValue)t.notEquals(n)).getValue());
            assertFalse(((BooleanValue)t.less(n)).getValue());
            assertFalse(((BooleanValue)t.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)t.greater(n)).getValue());
            assertFalse(((BooleanValue)t.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value ts = ValueFactory.createValue(new Timestamp(12));
            assertFalse(((BooleanValue)ts.equals(n)).getValue());
            assertFalse(((BooleanValue)ts.notEquals(n)).getValue());
            assertFalse(((BooleanValue)ts.less(n)).getValue());
            assertFalse(((BooleanValue)ts.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)ts.greater(n)).getValue());
            assertFalse(((BooleanValue)ts.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            assertFalse(((BooleanValue)n.equals(n)).getValue());
            assertFalse(((BooleanValue)n.notEquals(n)).getValue());
            assertFalse(((BooleanValue)n.less(n)).getValue());
            assertFalse(((BooleanValue)n.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)n.greater(n)).getValue());
            assertFalse(((BooleanValue)n.greaterEqual(n)).getValue());
            n.like(n);

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IncompatibleTypesException DOCUMENT ME!
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
            assertTrue(!((BooleanValue)vTrue.greater(vTrue)).getValue());
            assertTrue(((BooleanValue)vTrue.greater(vFalse)).getValue());
            assertTrue(!((BooleanValue)vFalse.greater(vTrue)).getValue());
            assertTrue(!((BooleanValue)vFalse.greater(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.greaterEqual(vFalse)).getValue());
            assertTrue(((BooleanValue)vTrue.greaterEqual(vTrue)).getValue());
            assertTrue(((BooleanValue)vTrue.greaterEqual(vFalse)).getValue());
            assertTrue(!((BooleanValue)vFalse.greaterEqual(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.less(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.less(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.less(vTrue)).getValue());
            assertTrue(!((BooleanValue)vFalse.less(vFalse)).getValue());
            assertTrue(((BooleanValue)vTrue.lessEqual(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.lessEqual(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.lessEqual(vTrue)).getValue());
            assertTrue(((BooleanValue)vFalse.lessEqual(vFalse)).getValue());
        } catch (IncompatibleTypesException e) {
            assertTrue(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCreateByType() throws Exception {
        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Value.LONG).equals(ValueFactory.createValue(1L))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("true",
                Value.BOOLEAN).equals(ValueFactory.createValue(true))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("false",
                Value.BOOLEAN).equals(ValueFactory.createValue(false))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("carajo",
                Value.STRING).equals(ValueFactory.createValue("carajo"))).getValue());

        Calendar c = Calendar.getInstance();

        //month is 0-based
        c.set(1980, 8, 5, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date d = c.getTime();
        assertTrue(((BooleanValue) ValueFactory.createValueByType(
                DateFormat.getDateInstance(DateFormat.SHORT).format(d), Value.DATE).equals(ValueFactory.createValue(
                    d))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType(NumberFormat.getNumberInstance().format(1.1),
                Value.DOUBLE).equals(ValueFactory.createValue(1.1d))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Value.INT).equals(ValueFactory.createValue(1))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType(NumberFormat.getNumberInstance().format(1.1),
                Value.FLOAT).equals(ValueFactory.createValue(1.1f))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Value.SHORT).equals(ValueFactory.createValue(1))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Value.BYTE).equals(ValueFactory.createValue(1))).getValue());

        byte[] array = new byte[] { (byte) 255, (byte) 160, (byte) 7 };
        assertTrue(((BooleanValue) ValueFactory.createValueByType("FFA007",
                Value.BINARY).equals(ValueFactory.createValue(array))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 0);

        Time t = new Time(c.getTime().getTime());
        assertTrue(((BooleanValue) ValueFactory.createValueByType(
                DateFormat.getTimeInstance().format(t), Value.TIME).equals(ValueFactory.createValue(
                    t))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 2345);

        Timestamp ts = new Timestamp(c.getTime().getTime());
        assertTrue(((BooleanValue) ValueFactory.createValueByType(ts.toString(), Value.TIMESTAMP).equals(ValueFactory.createValue(
                    ts))).getValue());
    }

    public void testToStringFromStringCoherente() throws Exception {
        Value v = ValueFactory.createValue(13.5d);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.DOUBLE))).getValue());

        v = ValueFactory.createValue(13.5f);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.FLOAT))).getValue());

        v = ValueFactory.createValue(13L);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.LONG))).getValue());

        v = ValueFactory.createValue(false);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.BOOLEAN))).getValue());

        v = ValueFactory.createValue("hola");
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.STRING))).getValue());

        Calendar c = Calendar.getInstance();

        //month is 0-based
        c.set(1980, 8, 5, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date d = c.getTime();
        v = ValueFactory.createValue(d);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.DATE))).getValue());

        v = ValueFactory.createValue(15);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.INT))).getValue());

        v = ValueFactory.createValue((short) 13);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.SHORT))).getValue());

        v = ValueFactory.createValue((byte) 5);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.BYTE))).getValue());

        v = ValueFactory.createValue(new byte[]{4,5,7,8,3,8});
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.BINARY))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 0);

        Time t = new Time(c.getTime().getTime());
        v = ValueFactory.createValue(t);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.TIME))).getValue());

        v = ValueFactory.createValue(new Timestamp(2465));
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Value.TIMESTAMP))).getValue());
    }

    public void testDecimalDigits() throws Exception {
        assertTrue(((NumericValue)ValueFactory.createValue(2.3d)).getDecimalDigitsCount() == 1);
        assertTrue(((NumericValue)ValueFactory.createValue(2d)).getDecimalDigitsCount() == 0);
        assertTrue(((NumericValue)ValueFactory.createValue(23)).getDecimalDigitsCount() == 0);
        assertTrue(((NumericValue)ValueFactory.createValue(2.030f)).getDecimalDigitsCount() == 2);
        assertTrue(((NumericValue)ValueFactory.createValue(2.00000000002d)).getDecimalDigitsCount() == 11);
    }

    public void testValuesTypes() throws Exception {
        assertTrue(ValueFactory.createValue(false).getType() == Value.BOOLEAN);
        assertTrue(ValueFactory.createValue(new byte[]{2, 3}).getType() == Value.BINARY);
        assertTrue(ValueFactory.createValue(new Date()).getType() == Value.DATE);
        assertTrue(ValueFactory.createValue(3.0d).getType() == Value.DOUBLE);
        assertTrue(ValueFactory.createValue(3.5f).getType() == Value.FLOAT);
        assertTrue(ValueFactory.createValue(4).getType() == Value.INT);
        assertTrue(ValueFactory.createValue(4L).getType() == Value.LONG);
        assertTrue(ValueFactory.createValue("").getType() == Value.STRING);
        assertTrue(ValueFactory.createValue(new Time(1)).getType() == Value.TIME);
        assertTrue(ValueFactory.createValue(new Timestamp(1)).getType() == Value.TIMESTAMP);
    }

	@Override
	protected void setUp() throws Exception {
		setWritingTests(false);
		super.setUp();
	}
}
