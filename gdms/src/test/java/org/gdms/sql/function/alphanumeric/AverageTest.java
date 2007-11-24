package org.gdms.sql.function.alphanumeric;

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

public class AverageTest extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	protected void setUp() throws Exception {
		super.setUp();

		final ObjectMemoryDriver driver = new ObjectMemoryDriver(
				new String[] { "myField" }, new Type[] { TypeFactory
						.createType(Type.INT) });
		driver.addValues(new Value[] { ValueFactory.createValue(3) });
		driver.addValues(new Value[] { ValueFactory.createValue(2) });
		driver.addValues(new Value[] { ValueFactory.createValue(1) });
		driver.addValues(new Value[] { ValueFactory.createNullValue() });
		driver.addValues(new Value[] { ValueFactory.createValue(4) });
		driver.addValues(new Value[] { ValueFactory.createValue(0) });
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
				"select Avg(myField) from inDs;");

		final DataSource outDs = dsf.getDataSource("outDs");
		outDs.open();
		final long rowCount = outDs.getRowCount();
		final int fieldCount = outDs.getFieldCount();
		assertTrue(1 == rowCount);
		assertTrue(1 == fieldCount);
		assertTrue(2 == outDs.getDouble(0, 0));
		outDs.cancel();
	}
}