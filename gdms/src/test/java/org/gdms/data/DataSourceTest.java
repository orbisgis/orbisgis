package org.gdms.data;

import org.gdms.SourceTest;
import org.gdms.data.values.Value;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;

public class DataSourceTest extends SourceTest {

	public void testReadWriteAccessInDataSourceOutOfTransaction() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		try {
			ds.getFieldValue(0, 0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getDataSourceMetadata();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getDriverMetadata();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getFieldIndexByName("");
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getRowCount();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getScope(DataSource.X, "");
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.isNull(0, 0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.addField("", ds.getDriver().getAvailableTypes()[0]);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.deleteRow(0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertEmptyRow();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertEmptyRowAt(0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertFilledRow(new Value[0]);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertFilledRowAt(0, new Value[0]);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.isModified();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.redo();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.setFieldValue(0, 0, null);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.undo();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
	}

	public void testSaveDataWithOpenDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();
		try {
			ds.saveData(null);
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
		ds.rollBackTrans();
	}

	public void testOpenDataSourceSpatialDecoration() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();
		SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
		sds.getFID(0);
		ds.rollBackTrans();
	}

	public void testRemovedDataSource() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);

		ds.beginTrans();
		ds.rollBackTrans();
		ds.remove();

		try {
			dsf.getDataSource(dsName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
			assertTrue(true);
		}
		ds.beginTrans();
		ds.getFieldNames();
		ds.rollBackTrans();
	}

	public void testAlreadyClosed() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();
		ds.rollBackTrans();
		try {
			ds.rollBackTrans();
			assertFalse(true);
		} catch (AlreadyClosedException e) {
			assertTrue(true);
		}
	}

}
