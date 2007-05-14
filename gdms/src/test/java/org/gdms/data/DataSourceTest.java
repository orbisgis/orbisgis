package org.gdms.data;

import org.gdms.SourceTest;
import org.gdms.data.ClosedDataSourceException;
import org.gdms.data.DataSource;
import org.gdms.data.OutOfTransactionException;
import org.gdms.data.values.Value;

public class DataSourceTest extends SourceTest {

	public void testAccessInClosedDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();
		ds.rollBackTrans();

	}

	public void testReadWriteAccessInDataSourceOutOfTransaction() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();

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
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.deleteRow(0);
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.insertEmptyRow();
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.insertEmptyRowAt(0);
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.insertFilledRow(new Value[0]);
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.insertFilledRowAt(0, new Value[0]);
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.isModified();
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.redo();
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.setFieldValue(0, 0, null);
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		try {
			ds.undo();
			assertTrue(false);
		} catch (OutOfTransactionException e) {
		}
		ds.rollBackTrans();
	}

	public void testSaveDataWithOpenDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.beginTrans();
		try {
			ds.saveData(null);
		} catch (ClosedDataSourceException e) {
			assertTrue(true);
		}
	}

	public void testTwoStartsException() throws Exception {
//		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());
//		ds.beginTrans();
//		try {
//			ds.beginTrans();
//			assertTrue(false);
//		} catch (AlreadyOpenedException e) {
//			assertTrue(true);
//		}
	}

}
