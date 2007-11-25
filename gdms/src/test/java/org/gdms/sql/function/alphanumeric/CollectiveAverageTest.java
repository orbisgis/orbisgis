package org.gdms.sql.function.alphanumeric;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class CollectiveAverageTest extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	public static void addDriverValue(final ObjectMemoryDriver driver,
			double... doubleValues) {
		final Value[] values = new Value[doubleValues.length];
		for (int i = 0; i < doubleValues.length; i++) {
			values[i] = ValueFactory.createValue(doubleValues[i]);
		}
		driver.addValues(values);
	}

	protected void setUp() throws Exception {
		super.setUp();

		final ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
				"myField1", "myField2" }, new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT) });
		addDriverValue(driver, 3, 30);
		addDriverValue(driver, 2, 31);
		addDriverValue(driver, 1, 32);
		addDriverValue(driver, 4, 33);
		addDriverValue(driver, 0, 34);
		dsf.getSourceManager().register("inDs", driver);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (dsf.getSourceManager().exists("outDs")) {
			dsf.getSourceManager().remove("outDs");
		}
		if (dsf.getSourceManager().exists("inDs")) {
			dsf.getSourceManager().remove("inDs");
		}
	}

	public final void testEvaluate() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException {
		dsf.getSourceManager().register("outDs",
				"select CollectiveAvg(myField1,myField2) from inDs;");

		final DataSource outDs = dsf.getDataSource("outDs");
		outDs.open();
		final long rowCount = outDs.getRowCount();
		final int fieldCount = outDs.getFieldCount();
		assertTrue(1 == rowCount);
		assertTrue(1 == fieldCount);
		assertTrue(2 == ((DoubleValue) ((ValueCollection) outDs.getFieldValue(
				0, 0)).get(0)).getValue());
		assertTrue(32 == ((DoubleValue) ((ValueCollection) outDs.getFieldValue(
				0, 0)).get(1)).getValue());
		outDs.cancel();
	}
}