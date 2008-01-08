package org.urbsat.kmeans;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.urbsat.Register;

public class KMeansTest extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();
	private double[] meansX = new double[] { 125, -34, 13 };
	private double[] meansY = new double[] { 57, 18, -123 };
	private long rowCount = 100;

	static {
		try {
			new Register().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
				"id", "myIndicator1", "myIndicator2" }, new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) });
		for (int i = 1; i <= rowCount; i++) {
			addDriverValue(driver, i,
					meansX[i % meansX.length] + Math.random(), meansY[i
							% meansY.length]
							+ Math.random());
		}
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
				"select KMeans('id',13) from inDs;");

		final DataSource outDs = dsf.getDataSource("outDs");
		outDs.open();
		assertTrue(rowCount == outDs.getRowCount());
		assertTrue(2 == outDs.getFieldCount());
		for (int i = 0; i < rowCount; i++) {
			final int pk = outDs.getFieldValue(i, 0).getAsInt();
			// why does the following instruction code throw a
			// ClassCastException ?
			// final int pk = ((IntValue) outDs.getFieldValue(i, 0)).getValue();
			final int clusterId = outDs.getFieldValue(i, 1).getAsInt();
			assertTrue((pk - 1) % 3 == clusterId);
		}
		outDs.cancel();
	}
}