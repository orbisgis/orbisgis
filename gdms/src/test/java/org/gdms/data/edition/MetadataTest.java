package org.gdms.data.edition;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.EditionListenerCounter;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class MetadataTest extends SourceTest {

	private void testAddField(String dsName, Type type) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Metadata m = d.getMetadata();
		int fc = m.getFieldCount();
		d.addField("extra", type);
		m = d.getMetadata();
		d.commit();
		d.open();
		assertTrue(fc + 1 == m.getFieldCount());
		assertTrue(m.getFieldName(fc).equals("extra"));
		assertTrue(m.getFieldType(fc).getTypeCode() == Type.STRING);

		assertTrue(m.getFieldType(fc).getConstraintValue(ConstraintNames.PK) == null);
		assertTrue(m.getFieldType(fc).getConstraintValue(
				ConstraintNames.READONLY) == null);
		d.cancel();
	}

	public void testAddField() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testAddField(resource, TypeFactory
					.createType(Type.STRING, "STRING"));
		}
	}

	private void testDeleteField(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Metadata m = d.getMetadata();
		int fc = m.getFieldCount();
		d.removeField(1);
		d.commit();
		d.open();
		assertTrue(fc - 1 == m.getFieldCount());
		d.cancel();
	}

	public void testDeleteField() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testDeleteField(resource);
		}
	}

	private void testModifyField(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		d.getMetadata();
		d.setFieldName(1, "nuevo");
		d.commit();
		d.open();
		assertTrue(d.getMetadata().getFieldName(1).equals("nuevo"));
		d.cancel();
	}

	public void testModifyField() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testModifyField(resource);
		}
	}

	private void testMetadataEditionListenerTest(String dsName, Type type)
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
		testMetadataEditionListenerTest(super.getAnyNonSpatialResource(),
				TypeFactory.createType(Type.STRING, "STRING"));
	}

	private void testEditionWithFieldAdded(String dsName, Type type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.UNDOABLE);
		d.open();
		d.addField("extra", type);
		int fi = d.getFieldIndexByName("extra");
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		d.setFieldValue(0, fi, ValueFactory.createValue(true));
		assertTrue(((BooleanValue) d.getFieldValue(0, fi)).getValue());
		d.undo();
		d.commit();
		d.open();
		assertTrue(d.getFieldValue(0, d.getFieldIndexByName("extra")) instanceof NullValue);
		d.cancel();
	}

	public void testEditionWithFieldAdded() throws Exception {
		testEditionWithFieldAdded(super.getAnyNonSpatialResource(), TypeFactory
				.createType(Type.STRING, "STRING"));
	}

	private void testEditionWithFieldRemoved(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.UNDOABLE);
		d.open();
		String fieldName = d.getFieldName(1);
		Value testValue = d.getFieldValue(0, 2);
		d.removeField(1);
		assertTrue(super.equals(testValue, d.getFieldValue(0, 1)));
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		d.commit();

		d.open();
		assertTrue(d.getFieldIndexByName(fieldName) == -1);
		d.cancel();
	}

	public void testEditionWithFieldRemoved() throws Exception {
		testEditionWithFieldRemoved(super.getAnyNonSpatialResource());
	}

	public void testRemovePK() throws Exception {
		String dsName = super.getResourcesWithPK()[0];
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		int pkIndex = d.getFieldIndexByName(super.getPKFieldFor(dsName));
		try {
			d.removeField(pkIndex);
			assertTrue(false);
		} catch (DriverException e) {
			assertTrue(true);
		}
		try {
			d.setFieldName(pkIndex, "sd");
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
		testUndoRedoClearedAfterEdition(super.getAnyNonSpatialResource());
	}

	private void testFieldDeletionEditionWhileEdition(String dsName)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		Value[][] content = super.getDataSourceContents(d);
		d.deleteRow(0);
		d.setFieldValue(0, 2, d.getFieldValue(1, 2));
		d.removeField(1);
		assertTrue(super.equals(d.getFieldValue(0, 1), content[1][2]));
		d.setFieldValue(0, 0, d.getFieldValue(1, 0));
		assertTrue(super.equals(d.getFieldValue(0, 0), content[2][0]));
		d.commit();
	}

	public void testFieldDeletionEditionWhileEdition() throws Exception {
		testFieldDeletionEditionWhileEdition(super.getAnyNonSpatialResource());
	}

	private void testFieldInsertionEditionWhileEdition(String dsName, Type type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		String nouveau = "nouveau";
		Value newValue = ValueFactory.createValue(nouveau);
		Value testValue = d.getFieldValue(2, 2);
		d.open();
		int lastField = d.getMetadata().getFieldCount();
		d.deleteRow(0);
		d.setFieldValue(0, 2, d.getFieldValue(1, 2));
		d.addField(nouveau, type);
		d.setFieldValue(0, lastField, newValue);
		assertTrue(super.equals(d.getFieldValue(0, lastField), newValue));
		d.commit();

		d.open();
		assertTrue(d.getMetadata().getFieldName(lastField).toLowerCase()
				.equals(nouveau));
		assertTrue(super.equals(d.getFieldValue(0, lastField), newValue));
		assertTrue(super.equals(d.getFieldValue(0, 2), testValue));
		d.cancel();
	}

	public void testFieldInsertionEditionWhileEdition() throws Exception {
		testFieldInsertionEditionWhileEdition(super.getAnyNonSpatialResource(),
				TypeFactory.createType(Type.STRING, "String"));
	}
}
