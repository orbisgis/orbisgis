package org.gdms.sql.evaluator;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class EvaluatorTest extends TestCase {

	private DataSourceFactory dsf;
	private DataSource ds1;
	private DataSource dsMemory;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		ds1 = dsf.getDataSource(new File("src/test/resources/evaluator.csv"));
		ds1.open();

		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });
		omd.addValues(new Value[] { ValueFactory.createValue(4.7) });
		dsMemory = dsf.getDataSource(omd);
		dsMemory.open();
	}

	@Override
	protected void tearDown() throws Exception {
		ds1.cancel();
		dsMemory.cancel();
	}

	public void testFilter() throws Exception {
		Property p = new Property("name");
		Literal l = new Literal("b");
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(ds1, gte);
		filtered.open();
		System.out.println(filtered.getAsString());
		for (int i = 0; i < filtered.getRowCount(); i++) {
			assertTrue(filtered.getString(i, "name").compareTo("b") >= 0);
		}
		filtered.cancel();
	}

	public void testFilterDoubles() throws Exception {
		Property p = new Property("field");
		Literal l = new Literal("2");
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(dsMemory, gte);
		filtered.open();
		assertTrue(filtered.getRowCount() == 1);
		filtered.cancel();

	}

	public void testFilterString() throws Exception {
		Property p = new Property("name");
		Literal l = new Literal("1");
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(ds1, gte);
		filtered.open();
		System.out.println(filtered.getAsString());
		for (int i = 0; i < filtered.getRowCount(); i++) {
			assertTrue(filtered.getString(i, "name").compareTo("1") >= 0);
		}
		filtered.cancel();
	}
}
