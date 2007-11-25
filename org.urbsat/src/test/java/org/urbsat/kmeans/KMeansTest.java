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
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT) });
		addDriverValue(driver, 0, 3, 30);
		addDriverValue(driver, 1, 2, 31);
		addDriverValue(driver, 2, 1, 32);
		addDriverValue(driver, 3, 4, 33);
		addDriverValue(driver, 4, 0, 34);
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
		final long rowCount = outDs.getRowCount();
		final int fieldCount = outDs.getFieldCount();
		outDs.cancel();
	}
}