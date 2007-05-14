package org.gdms.data.edition;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.driver.DriverException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;

public class FailedEditionTest extends BaseTest {

	private DataSourceFactory dsf;

	private void failedCommit(DataSource ds) throws DriverException,
			FreeingResourcesException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nouveau"));
		ds.insertFilledRow(ds.getRow(0));
		Value[][] table = super.getDataSourceContents(ds);
		try {
			FailingDriver.failOnWrite = true;
			ds.commitTrans();
		} catch (DriverException e) {
			assertTrue(equals(table, super.getDataSourceContents(ds)));
			FailingDriver.failOnWrite = false;
			ds.commitTrans();
		}
		ds.beginTrans();
		assertTrue(equals(table, super.getDataSourceContents(ds)));
		ds.rollBackTrans();

	}

	public void testAlphanumericObjectfailedCommit() throws Exception {
		DataSource ds = dsf.getDataSource("object");
		ds.beginTrans();
		failedCommit(ds);
	}

	public void testSpatialObjectfailedCommit() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("object"));
		ds.beginTrans();
		ds.buildIndex();
		failedCommit(ds);
	}

	public void testAlphanumericFileFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("writeFile");
		ds.beginTrans();
		failedCommit(ds);
	}

	private void failedClose(DataSource ds, boolean isFile) throws DriverException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException, FreeingResourcesException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		Value[][] table = super.getDataSourceContents(ds);
		try {
			FailingDriver.failOnClose = true;
			ds.commitTrans();
		} catch (FreeingResourcesException e) {
			FailingDriver.failOnClose = false;
			assertTrue(true);
			/*
			 * Check if its a file because in that case the contents have been
			 * saved to another temporal location
			 */
			if (!isFile) {
				ds = dsf.getDataSource(ds.getName());
				ds.beginTrans();
				assertTrue(equals(table, super.getDataSourceContents(ds)));
				ds.rollBackTrans();
			}
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeFile");
		ds.beginTrans();
		failedClose(ds, true);
	}

	private void failedCopy(DataSource ds, boolean isFile) throws Exception {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		super.getDataSourceContents(ds);
		try {
			FailingDriver.failOnCopy = true;
			ds.commitTrans();
		} catch (FreeingResourcesException e) {
			assertTrue(true);
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnCopy() throws Exception {
		DataSource ds = dsf.getDataSource("copyFile");
		ds.beginTrans();
		failedCopy(ds, true);
	}

	public void testSpatialFilefailedOnWrite() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("writeFile"));
		ds.beginTrans();
		ds.buildIndex();
		failedCommit(ds);
	}

	public void testSpatialFilefailedOnClose() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeFile"));
		ds.beginTrans();
		ds.buildIndex();
		failedClose(ds, true);
	}

	public void testSpatialFilefailedCopy() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("copyFile"));
		ds.beginTrans();
		ds.buildIndex();
		failedCopy(ds, true);
	}

	public void testAlphanumericDBFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("executeDB");
		ds.beginTrans();
		FailingDriver.setCurrentDataSource(ds);
		failedCommit(ds);
	}

	public void testAlphanumericDBFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeDB");
		ds.beginTrans();
		FailingDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	public void testSpatialDBfailedOnWrite() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("executeDB"));
		ds.beginTrans();
		ds.buildIndex();
		FailingDriver.setCurrentDataSource(ds);
		failedCommit(ds);
	}

	public void testSpatialDBfailedOnClose() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeDB"));
		ds.beginTrans();
		ds.buildIndex();
		FailingDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	@Override
	protected void setUp() throws Exception {
		FailingDriver.initialize();

		dsf = new DataSourceFactory();
		DriverManager dm = new DriverManager();
		dm.registerDriver("failingdriver", FailingDriver.class);
		dsf.setDriverManager(dm);

		dsf.registerDataSource("object", new ObjectSourceDefinition(
				new FailingDriver()));
		dsf.registerDataSource("writeFile", new FakeFileSourceDefinition(
				new FailingDriver()));
		dsf.registerDataSource("closeFile", new FakeFileSourceDefinition(
				new FailingDriver()));
		dsf.registerDataSource("copyFile", new FakeFileSourceDefinition(
				new FailingDriver()));
		dsf.registerDataSource("executeDB",
				new FakeDBTableSourceDefinition(new FailingDriver(),
						"jdbc:executefailing"));
		dsf.registerDataSource("closeDB", new FakeDBTableSourceDefinition(
				new FailingDriver(), "jdbc:closefailing"));
	}
}
