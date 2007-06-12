package org.gdms.data.edition;

import java.util.Iterator;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.instruction.Row;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;

public class FailedEditionTest extends BaseTest {

	private static final String SPATIAL_FIELD_NAME = "geom";

	private DataSourceFactory dsf;

	private void failedCommit(DataSource ds, IndexQuery query)
			throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nouveau"));
		ds.insertFilledRow(ds.getRow(0));
		Value[][] table = super.getDataSourceContents(ds);
		Iterator<Row> it = ds.queryIndex(query);
		try {
			ReadDriver.failOnWrite = true;
			ds.commit();
		} catch (DriverException e) {
			assertTrue(equals(table, super.getDataSourceContents(ds)));
			if (it != null) {
				assertTrue(ds.queryIndex(query) != null);
			} else {
				assertTrue(ds.queryIndex(query) == null);
			}
			ReadDriver.failOnWrite = false;
			ds.commit();
		}
		ds.open();
		assertTrue(equals(table, super.getDataSourceContents(ds)));
		ds.cancel();

	}

	public void testAlphanumericObjectfailedCommit() throws Exception {
		DataSource ds = dsf.getDataSource("object");
		ds.open();
		failedCommit(ds, new FooQuery());
	}

	public void testSpatialObjectfailedCommit() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("object"));
		ds.open();
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testAlphanumericFileFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("writeFile");
		ds.open();
		failedCommit(ds, new FooQuery());
	}

	private void failedClose(DataSource ds, boolean isFile)
			throws DriverException, DriverLoadException, NoSuchTableException,
			DataSourceCreationException, FreeingResourcesException,
			NonEditableDataSourceException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		Value[][] table = super.getDataSourceContents(ds);
		try {
			ReadDriver.failOnClose = true;
			ds.commit();
		} catch (FreeingResourcesException e) {
			ReadDriver.failOnClose = false;
			assertTrue(true);
			/*
			 * Check if its a file because in that case the contents have been
			 * saved to another temporal location
			 */
			if (!isFile) {
				ds = dsf.getDataSource(ds.getName());
				ds.open();
				assertTrue(equals(table, super.getDataSourceContents(ds)));
				ds.cancel();
			}
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeFile");
		ds.open();
		failedClose(ds, true);
	}

	private void failedCopy(DataSource ds, boolean isFile) throws Exception {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		super.getDataSourceContents(ds);
		try {
			ReadDriver.failOnCopy = true;
			ds.commit();
		} catch (FreeingResourcesException e) {
			assertTrue(true);
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnCopy() throws Exception {
		DataSource ds = dsf.getDataSource("copyFile");
		ds.open();
		failedCopy(ds, true);
	}

	public void testSpatialFilefailedOnWrite() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("writeFile"));
		ds.open();
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testSpatialFilefailedOnClose() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeFile"));
		ds.open();
		failedClose(ds, true);
	}

	public void testSpatialFilefailedCopy() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("copyFile"));
		ds.open();
		failedCopy(ds, true);
	}

	public void testAlphanumericDBFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("executeDB");
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new FooQuery());
	}

	public void testAlphanumericDBFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeDB");
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	public void testSpatialDBfailedOnWrite() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("executeDB"));
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testSpatialDBfailedOnClose() throws Exception {
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeDB"));
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	@Override
	protected void setUp() throws Exception {
		ReadDriver.initialize();
		ReadDriver.isEditable = true;

		dsf = new DataSourceFactory();
		DriverManager dm = new DriverManager();
		dm.registerDriver("failingdriver", ReadAndWriteDriver.class);
		dsf.setDriverManager(dm);

		dsf.registerDataSource("object", new ObjectSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("writeFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("closeFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("copyFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("executeDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:executefailing"));
		dsf.registerDataSource("closeDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:closefailing"));
		dsf.getIndexManager().buildIndex("object", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("writeFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("executeDB", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("closeDB", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("copyFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("closeFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
	}

	private class FooQuery implements IndexQuery {

		public String getFieldName() {
			return "";
		}

		public String getIndexId() {
			return "";
		}

	}
}
