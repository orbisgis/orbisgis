package org.gdms.data;

import org.gdms.SourceTest;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class EditionListenerTest extends SourceTest {

	public EditionListenerCounter listener = new EditionListenerCounter();

	private void editDataSource(InternalDataSource d) throws DriverException {
		d.deleteRow(0);
		d.insertEmptyRow();
		d.insertEmptyRowAt(0);
		Value[] row = d.getRow(0);
		d.insertFilledRow(row);
		d.insertFilledRowAt(0, row);
		d.setFieldValue(0, 0, d.getFieldValue(1, 0));
	}

	public void testEditionNotification() throws Exception {
		InternalDataSource d = dsf.getDataSource(super.getAnyNonSpatialResource());

		d.addEditionListener(listener);
		d.open();
		editDataSource(d);
		assertTrue(listener.deletions == 1);
		assertTrue(listener.insertions == 4);
		assertTrue(listener.modifications == 1);
		assertTrue(listener.total == 6);
		d.cancel();
	}

	public void testComplexChange() throws Exception {
		InternalDataSource d = dsf.getDataSource(super.getAnyNonSpatialResource());

		d.addEditionListener(listener);
		d.open();
		d.setDispatchingMode(InternalDataSource.STORE);
		editDataSource(d);
		d.setDispatchingMode(InternalDataSource.DISPATCH);
		assertTrue(listener.deletions == 1);
		assertTrue(listener.insertions == 4);
		assertTrue(listener.modifications == 1);
		assertTrue(listener.total == 6);
		d.cancel();
	}

	public void testUndoRedoChanges() throws Exception {
		InternalDataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.UNDOABLE);

		d.addEditionListener(listener);
		d.open();
		editDataSource(d);
		for (int i = 0; i < 6; i++) {
			d.undo();
		}
		d.redo();
		d.undo();
		d.deleteRow(0);
		assertTrue(listener.total == 15);
		assertTrue(listener.undoRedo == 8);
		d.cancel();
	}

	public void testIgnoreChanges() throws Exception {
		InternalDataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.UNDOABLE);

		d.addEditionListener(listener);
		d.open();
		d.setDispatchingMode(InternalDataSource.IGNORE);
		editDataSource(d);
		for (int i = 0; i < 6; i++) {
			d.undo();
		}
		d.redo();
		d.undo();
		assertTrue(listener.total == 0);
		d.cancel();
	}
}
