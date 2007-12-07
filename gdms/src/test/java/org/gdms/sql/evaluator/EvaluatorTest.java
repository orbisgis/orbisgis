package org.gdms.sql.evaluator;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;

public class EvaluatorTest extends TestCase {

	private DataSourceFactory dsf;
	private DataSource ds1;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		ds1 = dsf.getDataSource(new File("src/test/resources/evaluator.csv"));
		ds1.open();
	}

	@Override
	protected void tearDown() throws Exception {
		ds1.cancel();
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
}
