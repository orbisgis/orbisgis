package org.gdms.sql.function;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class SqrtTest extends TestCase {
	private final static DataSourceFactory dsf = new DataSourceFactory();
	private DataSource ds;

	protected void setUp() throws Exception {
		super.setUp();

		final ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] {
				"id", "geom", "field" }, new Type[] {
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.FLOAT) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd));
		ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("13"),
				ValueFactory.createValue(Geometries.getPolygon()),
				ValueFactory.createValue(16.0f), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("13"),
				ValueFactory.createValue(Geometries.getPolygon()),
				ValueFactory.createValue(Float.NaN), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("270"),
				ValueFactory.createValue(Geometries.getPoint()),
				ValueFactory.createValue(-49.0f), });
		ds.commit();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testEvaluate() throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException,
			DriverException {
		final DataSource resultDsOne = dsf
				.executeSQL("select id, field, sqrt(sqrt(field)) from obj;");
		resultDsOne.open();
		final long rowCount = resultDsOne.getRowCount();
		final int fieldCount = resultDsOne.getFieldCount();

		assertTrue(3 == rowCount);
		assertTrue(3 == fieldCount);

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDsOne.getRow(rowIndex);
			double tmpOne = ((FloatValue) fields[1]).getValue();
			double tmpTwo = ((DoubleValue) fields[2]).getValue();
			if ((0 > tmpOne) || (Double.isNaN(tmpOne))) {
				assertTrue(Double.isNaN(tmpTwo));
			} else {
				assertTrue(tmpOne == tmpTwo * tmpTwo * tmpTwo * tmpTwo);
			}
		}
		resultDsOne.cancel();

		final DataSource resultDsTwo = dsf
				.executeSQL("select id, field, sqrt(geom) from obj;");
		resultDsTwo.open();
		boolean bug = true;
		for (long rowIndex = 0; rowIndex < resultDsTwo.getRowCount(); rowIndex++) {
			try {
				resultDsTwo.getRow(rowIndex);
			} catch (ClassCastException e) {
				bug = false;
			}
		}
		resultDsTwo.cancel();
		assertFalse(bug);
	}
}