package org.gdms.data;

import org.gdms.SourceTest;
import org.gdms.data.edition.ReadDriver;
import org.gdms.data.values.Value;

public class DataSourceTest extends SourceTest {

	public void testReadWriteAccessInDataSourceOutOfTransaction()
			throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		try {
			ds.getFieldValue(0, 0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getMetadata();
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

		ds.open();
		try {
			ds.saveData(null);
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
		ds.cancel();
	}

	public void testRemovedDataSource() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);

		ds.open();
		ds.cancel();
		ds.remove();

		try {
			dsf.getDataSource(dsName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
			assertTrue(true);
		}
		ds.open();
		ds.getFieldNames();
		ds.cancel();
	}

	public void testAlreadyClosed() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.open();
		ds.cancel();
		try {
			ds.cancel();
			assertFalse(true);
		} catch (AlreadyClosedException e) {
			assertTrue(true);
		}
	}

	public void testCommitNonEditableDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(new ReadDriver());

		ds.open();
		try {
			ds.commit();
			assertFalse(true);
		} catch (NonEditableDataSourceException e) {
		}
	}

}
