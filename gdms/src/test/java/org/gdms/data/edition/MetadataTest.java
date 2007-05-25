package org.gdms.data.edition;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.EditionListenerCounter;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;

public class MetadataTest extends SourceTest {

	private void testAddField(String dsName, String type) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Metadata m = d.getDataSourceMetadata();
		int fc = m.getFieldCount();
		d.addField("extra", type);
		m = d.getDataSourceMetadata();
		assertTrue(fc + 1 == m.getFieldCount());
		assertTrue(m.getFieldName(fc).equals("extra"));
		assertTrue(m.getFieldType(fc) == Value.STRING);
		assertTrue(!in(m.getPrimaryKey(), "extra"));
		assertTrue(!m.isReadOnly(fc));
		d.cancel();
	}

	public void testAddField() throws Exception {
		testAddField("persona", "STRING");
		testAddField("objectpersona", "STRING");
		testAddField("hsqldbpersona", "CHAR");
	}

	private boolean in(String[] primaryKey, String string) {
		for (int i = 0; i < primaryKey.length; i++) {
			if (primaryKey[i].equals(string)) {
				return true;
			}
		}

		return false;
	}

	private void testDeleteField(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Metadata m = d.getDataSourceMetadata();
		String fieldName = m.getFieldName(2);
		int fc = m.getFieldCount();
		d.removeField(1);
		assertTrue(fc - 1 == m.getFieldCount());
		assertTrue(fieldName.equals(m.getFieldName(1)));
		d.cancel();
	}

	public void testDeleteField() throws Exception {
		testDeleteField("persona");
		testDeleteField("objectpersona");
		testDeleteField("hsqldbpersona");
	}

	private void testModifyField(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		d.getDataSourceMetadata();
		d.setFieldName(1, "nuevo");
		assertTrue(d.getDataSourceMetadata().getFieldName(1).equals("nuevo"));
		d.cancel();
	}

	public void testModifyField() throws Exception {
		testModifyField("persona");
		testModifyField("objectpersona");
		testModifyField("hsqldbpersona");
	}

	private void testMetadataEditionListenerTest(String dsName, String type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		EditionListenerCounter elc = new EditionListenerCounter();
		d.addMetadataEditionListener(elc);
		d.removeField(1);
		d.addField("nuevo", type);
		d.setFieldName(1, "jjjj");
		assertTrue(elc.fieldDeletions == 1);
		assertTrue(elc.fieldInsertions == 1);
		assertTrue(elc.fieldModifications == 1);
		assertTrue(elc.total == 3);
		d.cancel();
	}

	public void testMetadataEditionListenerTest() throws Exception {
		testMetadataEditionListenerTest("persona", "STRING");
		testMetadataEditionListenerTest("objectpersona", "STRING");
		testMetadataEditionListenerTest("hsqldbpersona", "CHAR");
	}

	private void testEditionWithFieldAdded(String dsName, String type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.UNDOABLE);
		d.open();
		d.addField("extra", type);
		int fi = d.getFieldIndexByName("extra");
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		d.setFieldValue(0, fi, ValueFactory.createValue(true));
		assertTrue(((BooleanValue) d.getFieldValue(0, fi)).getValue());
		d.undo();
		assertTrue(d.getFieldValue(0, fi) instanceof NullValue);
		d.cancel();
	}

	public void testEditionWithFieldAdded() throws Exception {
		testEditionWithFieldAdded("persona", "");
		testEditionWithFieldAdded("objectpersona", "BOOLEAN");
		testEditionWithFieldAdded("hsqldbpersona", "BIT");
	}

	private void testEditionWithFieldRemoved(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.UNDOABLE);
		d.open();
		d.removeField(1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("gonzalez"))).getValue());
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		d.cancel();
	}

	public void testEditionWithFieldRemoved() throws Exception {
		testEditionWithFieldRemoved("persona");
		testEditionWithFieldRemoved("objectpersona");
		testEditionWithFieldRemoved("hsqldbpersona");
	}

	public void testRemovePK() throws Exception {
		DataSource d = dsf.getDataSource("hsqldbpersona",
				DataSourceFactory.UNDOABLE);
		d.open();
		try {
			d.removeField(0);
			assertTrue(false);
		} catch (DriverException e) {
			assertTrue(true);
		}
		try {
			d.setFieldName(0, "sd");
			assertTrue(false);
		} catch (DriverException e) {
			assertTrue(true);
		}
		d.cancel();
	}

	private void testUndoRedoClearedAfterEdition(String dsName)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.UNDOABLE);
		d.open();
		d.deleteRow(0);
		assertTrue(d.canUndo());
		d.removeField(1);
		assertTrue(!d.canRedo());
		assertTrue(!d.canUndo());
		d.cancel();
	}

	public void testUndoRedoClearedAfterEdition() throws Exception {
		testUndoRedoClearedAfterEdition("persona");
		testUndoRedoClearedAfterEdition("objectpersona");
		testUndoRedoClearedAfterEdition("hsqldbpersona");
	}

	private void testObjectFieldDeletionEditionWhileEdition(String dsName)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		Value v1 = ValueFactory.createValue("freestyle");
		Value v2 = ValueFactory.createValue(9);
		d.open();
		d.deleteRow(0);
		d.setFieldValue(0, 2, v1);
		d.removeField(1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		d.setFieldValue(0, 0, v2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v2)).getValue());
		d.commit();
	}

	private void testFieldDeletionEditionWhileEdition(String dsName, String id)
			throws Exception {

		Value v1 = ValueFactory.createValue("freestyle");
		Value v2 = ValueFactory.createValue(9);
		testObjectFieldDeletionEditionWhileEdition(dsName);

		DataSource newd = dsf.executeSQL("select * from " + dsName + " where "
				+ id + " = 9;");
		newd.open();
		assertTrue(newd.getDataSourceMetadata().getFieldName(0).toLowerCase()
				.equals("id"));
		assertTrue(newd.getDataSourceMetadata().getFieldName(1).toLowerCase()
				.equals("apellido"));
		assertTrue(((BooleanValue) newd.getFieldValue(0, 0).equals(v2))
				.getValue());
		assertTrue(((BooleanValue) newd.getFieldValue(0, 1).equals(v1))
				.getValue());
		newd.cancel();
	}

	public void testFieldDeletionEditionWhileEdition() throws Exception {
		testFieldDeletionEditionWhileEdition("persona", "id");
		testFieldDeletionEditionWhileEdition("hsqldbpersona", "ID");
		testObjectFieldDeletionEditionWhileEdition("objectpersona");
	}

	private void testFieldInsertionEditionWhileEdition(String dsName,
			String type) throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		Value v1 = ValueFactory.createValue("freestyle");
		Value v2 = ValueFactory.createValue(9);
		d.open();
		int lastField = d.getDataSourceMetadata().getFieldCount();
		d.deleteRow(0);
		d.setFieldValue(0, 2, v1);
		d.addField("nuevo", type);
		d.setFieldValue(0, lastField, v2);
		assertTrue(((BooleanValue) d.getFieldValue(0, lastField).equals(v2))
				.getValue());
		d.commit();

		d.open();
		assertTrue(d.getDataSourceMetadata().getFieldName(lastField)
				.toLowerCase().equals("nuevo"));
		assertTrue(((BooleanValue) d.getFieldValue(0, lastField).equals(v2))
				.getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v1)).getValue());
		d.cancel();
	}

	public void testFieldInsertionEditionWhileEdition() throws Exception {
		testFieldInsertionEditionWhileEdition("persona", "");
		testFieldInsertionEditionWhileEdition("objectpersona", "INT");
		testFieldInsertionEditionWhileEdition("hsqldbpersona", "INTEGER");
	}

	public void testSpatialFieldEdition() throws Exception {
		SpatialDataSource d = (SpatialDataSource) dsf
				.getDataSource("spatialobjectpersona");

		d.open();
		int sfi = d.getSpatialFieldIndex();
		try {
			d.removeField(sfi);
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
			assertTrue(true);
		}
		d.cancel();
	}
}
