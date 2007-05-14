package org.gdms.data;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.BinaryValue;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.ByteValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.ShortValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.TimeValue;
import org.gdms.data.values.TimestampValue;
import org.gdms.data.values.ValueFactory;

public class GettersTest extends TestCase {

	private DataSourceFactory dsf;

	public void testAllGeters() throws Exception {
		AllTypesObjectDriver test = new AllTypesObjectDriver();
		DataSource d = dsf.getDataSource("alltypes");
		d.beginTrans();
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getBinary(0, 0)).equals(test.getFieldValue(0, 0))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getBinary(0, "binary")).equals(test.getFieldValue(0, 0))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getBoolean(0, 1)).equals(test.getFieldValue(0, 1))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getBoolean(0, "boolean")).equals(test.getFieldValue(0, 1))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getByte(0, 2)).equals(test.getFieldValue(0, 2))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getByte(0, "byte")).equals(test.getFieldValue(0, 2))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getDate(0, 3)).equals(test.getFieldValue(0, 3))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getDate(0, "date")).equals(test.getFieldValue(0, 3))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getDouble(0, 4)).equals(test.getFieldValue(0, 4))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getDouble(0, "double")).equals(test.getFieldValue(0, 4))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getFloat(0, 5)).equals(test.getFieldValue(0, 5))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getFloat(0, "float")).equals(test.getFieldValue(0, 5))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getInt(0, 6)).equals(test.getFieldValue(0, 6))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getInt(0, "int")).equals(test.getFieldValue(0, 6))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getLong(0, 7)).equals(test.getFieldValue(0, 7))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getLong(0, "long")).equals(test.getFieldValue(0, 7))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getShort(0, 8)).equals(test.getFieldValue(0, 8))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getShort(0, "short")).equals(test.getFieldValue(0, 8))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getString(0, 9)).equals(test.getFieldValue(0, 9))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getString(0, "string")).equals(test.getFieldValue(0, 9))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getTimestamp(0, 10)).equals(test.getFieldValue(0, 10))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getTimestamp(0, "timestamp")).equals(test.getFieldValue(0, 10))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getTime(0, 11)).equals(test.getFieldValue(0, 11))).getValue());
		assertTrue(((BooleanValue)ValueFactory.createValue(d.getTime(0, "time")).equals(test.getFieldValue(0, 11))).getValue());
		d.rollBackTrans();
	}

	public void testSetters() throws Exception {
		DataSource d = dsf.getDataSource("alltypes");
		d.beginTrans();
		d.setBinary(0, 0, ((BinaryValue) d.getFieldValue(1, 0)).getValue());
		d.setBinary(0, "binary", ((BinaryValue) d.getFieldValue(1, 0)).getValue());
		d.setBoolean(0, 1, ((BooleanValue) d.getFieldValue(1, 1)).getValue());
		d.setBoolean(0, "boolean", ((BooleanValue) d.getFieldValue(1, 1)).getValue());
		d.setByte(0, 2, ((ByteValue) d.getFieldValue(1, 2)).getValue());
		d.setByte(0, "byte", ((ByteValue) d.getFieldValue(1, 2)).getValue());
		d.setDate(0, 3, ((DateValue) d.getFieldValue(1, 3)).getValue());
		d.setDate(0, "date", ((DateValue) d.getFieldValue(1, 3)).getValue());
		d.setDouble(0, 4, ((DoubleValue) d.getFieldValue(1, 4)).getValue());
		d.setDouble(0, "double", ((DoubleValue) d.getFieldValue(1, 4)).getValue());
		d.setFloat(0, 5, ((FloatValue) d.getFieldValue(1, 5)).getValue());
		d.setFloat(0, "float", ((FloatValue) d.getFieldValue(1, 5)).getValue());
		d.setInt(0, 6, ((IntValue) d.getFieldValue(1, 6)).getValue());
		d.setInt(0, "int", ((IntValue) d.getFieldValue(1, 6)).getValue());
		d.setLong(0, 7, ((LongValue) d.getFieldValue(1, 7)).getValue());
		d.setLong(0, "long", ((LongValue) d.getFieldValue(1, 7)).getValue());
		d.setShort(0, 8, ((ShortValue) d.getFieldValue(1, 8)).getValue());
		d.setShort(0, "short", ((ShortValue) d.getFieldValue(1, 8)).getValue());
		d.setString(0, 9, ((StringValue) d.getFieldValue(1, 9)).getValue());
		d.setString(0, "string", ((StringValue) d.getFieldValue(1, 9)).getValue());
		d.setTimestamp(0, 10, ((TimestampValue) d.getFieldValue(1, 10)).getValue());
		d.setTimestamp(0, "timestamp", ((TimestampValue) d.getFieldValue(1, 10)).getValue());
		d.setTime(0, 11, ((TimeValue) d.getFieldValue(1, 11)).getValue());
		d.setTime(0, "time", ((TimeValue) d.getFieldValue(1, 11)).getValue());

		for (int i = 0; i < d.getDataSourceMetadata().getFieldCount(); i++) {
			assertTrue(((BooleanValue)d.getFieldValue(0, i).equals(d.getFieldValue(1, i))).getValue());
		}
		d.rollBackTrans();
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.registerDataSource("alltypes", new ObjectSourceDefinition
				(new AllTypesObjectDriver()));
		super.setUp();
	}
}
