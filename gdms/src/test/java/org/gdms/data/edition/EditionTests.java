package org.gdms.data.edition;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class EditionTests extends SourceTest {

	/**
	 * Test the deletion of a row
	 */
	private void testDelete(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		Value[] sampleRow = d.getRow(1);

		int noPkFieldId = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));
		for (int i = 0; i < sampleRow.length; i++) {
			if (MetadataUtilities.isWritable(d.getMetadata().getFieldType(i))) {
				d.setFieldValue(2, i, sampleRow[i]);
			}
		}

		d.insertEmptyRow();
		d.setFieldValue(3, noPkFieldId, sampleRow[noPkFieldId]);
		d.deleteRow(0); // 0
		d.deleteRow(0); // 1
		d.deleteRow(1); // 3

		d.commit();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getRow(0), sampleRow, d.getMetadata()));
		d.cancel();
	}

	public void testDelete() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testDelete(ds);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param mode
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testSetDeletedRow(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		int fieldId = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));
		Value firstRow = d.getFieldValue(0, fieldId);
		Value secondRow = d.getFieldValue(1, fieldId);

		d.setFieldValue(1, fieldId, firstRow);
		d.deleteRow(0); // 0
		d.setFieldValue(0, fieldId, secondRow);

		d.commit();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), secondRow));
		d.cancel();
	}

	public void testSetDeletedRow() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testSetDeletedRow(ds);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param mode
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testSetAfterDeletedPreviousRow(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		int fieldId = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));
		Value firstRow = d.getFieldValue(0, fieldId);

		d.deleteRow(0); // 0
		d.setFieldValue(0, fieldId, firstRow);

		d.commit();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), firstRow));
		d.cancel();
	}

	public void testSetAfterDeletedPreviousRow() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testSetAfterDeletedPreviousRow(ds);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testUpdate(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		int last = (int) (d.getRowCount() - 1);
		int fieldIndex = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));

		Value[] firstRow = d.getRow(0);
		Value[] secondRow = d.getRow(1);
		Value[] lastRow = d.getRow(last);

		d.insertEmptyRow();
		d.setFieldValue(0, fieldIndex, secondRow[fieldIndex]);
		d.setFieldValue(1, fieldIndex, lastRow[fieldIndex]);
		d.setFieldValue(last + 1, fieldIndex, firstRow[fieldIndex]);

		d.commit();

		d = dsf.getDataSource(dsName);
		d.open();
		Value[] rowToTest;
		rowToTest = firstRow.clone();
		rowToTest[fieldIndex] = secondRow[fieldIndex];
		assertTrue(super.equals(rowToTest, d.getRow(0)));

		rowToTest = secondRow.clone();
		rowToTest[fieldIndex] = lastRow[fieldIndex];
		assertTrue(super.equals(rowToTest, d.getRow(1)));

		assertTrue(super.equals(firstRow[fieldIndex], d.getFieldValue(last + 1,
				fieldIndex)));
		d.cancel();
	}

	public void testUpdate() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testUpdate(ds);
		}
	}

	private void testValuesDuringTransaction(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		int fieldId = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));

		Value[] firstRow = d.getRow(0);

		d.setFieldValue(1, fieldId, firstRow[0]);
		assertTrue(equals(d.getFieldValue(1, fieldId), firstRow[0]));
		d.insertEmptyRow();
		assertTrue(d.getFieldValue(d.getRowCount() - 1, fieldId) instanceof NullValue);
		d.cancel();
	}

	public void testValuesDuringEdition() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testValuesDuringTransaction(ds);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testAdd(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		int fieldIndex = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));

		Value[][] ds = new Value[(int) d.getRowCount() + 1][d.getMetadata()
				.getFieldCount()];
		for (int i = 0; i < ds.length - 1; i++) {
			for (int j = 0; j < ds[i].length; j++) {
				ds[i][j] = d.getFieldValue(i, j);
			}
		}

		for (int j = 0; j < ds[ds.length - 1].length; j++) {
			ds[ds.length - 1][j] = ValueFactory.createNullValue();
		}
		ds[ds.length - 1][fieldIndex] = ds[0][fieldIndex];

		d.insertEmptyRow();
		d.setFieldValue(ds.length - 1, fieldIndex, ds[0][fieldIndex]);
		d.commit();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(ds.length - 1, fieldIndex),
				ds[0][fieldIndex]));
		d.cancel();
	}

	public void testAdd() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testAdd(ds);
		}
	}

	private void testSQLInjection(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		Value value = ValueFactory.createValue("aaa'aaa");

		d.open();
		int fieldIndex = d.getFieldIndexByName(super.getStringFieldFor(dsName));
		d.setFieldValue(0, fieldIndex, value);
		d.commit();

		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldIndex), value));
		d.cancel();
	}

	public void testSQLInjection() throws Exception {
		String[] resources = super.getDBResources();
		for (String ds : resources) {
			testSQLInjection(ds);
		}
	}

	private void testInsertFilledRow(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Value[] row = d.getRow(0);
		int pkField = d.getFieldIndexByName(super.getPKFieldFor(dsName));
		row[pkField] = super.getNewPKFor(dsName);
		int lastRow = (int) (d.getRowCount() - 1);
		d.insertFilledRow(row);
		d.commit();

		d.open();
		Value[] newRow = d.getRow(lastRow + 1);
		assertTrue(equals(newRow, row));
		d.cancel();
	}

	public void testInsertFilled() throws Exception {
		String[] resources = super.getResourcesWithPK();
		for (String ds : resources) {
			testInsertFilledRow(ds);
		}
	}

	private void testEditingNullValues(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		Value[] row = d.getRow(0);
		int noPKIndex = d.getFieldIndexByName(super.getNoPKFieldFor(dsName));
		row[noPKIndex] = ValueFactory.createNullValue();
		int lastRow = (int) (d.getRowCount() - 1);
		String pkField = super.getPKFieldFor(dsName);
		row[d.getFieldIndexByName(pkField)] = ValueFactory.createNullValue();

		d.insertFilledRow(row);
		d.setFieldValue(0, noPKIndex, ValueFactory.createNullValue());
		d.commit();

		d.open();
		assertTrue(d.isNull(0, noPKIndex));
		assertTrue(d.isNull(lastRow + 1, noPKIndex));
		d.cancel();
	}

	public void testEditingNullValues() throws Exception {
		String[] resources = super.getResourcesWithPK();
		for (String ds : resources) {
			testEditingNullValues(ds);
		}
	}

	private void testRowCount(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();

		int rc = (int) d.getRowCount();
		d.insertEmptyRow();
		assertTrue(d.getRowCount() == rc + 1);
		d.cancel();
	}

	public void testRowCount() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testRowCount(ds);
		}
	}

	private void testInsertAt(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Value[] row = d.getRow(1);
		String pkField = super.getPKFieldFor(dsName);
		if (pkField != null) {
			row[d.getFieldIndexByName(pkField)] = ValueFactory
					.createNullValue();
		}
		Value[] firstRow = d.getRow(0);
		d.insertFilledRowAt(0, row);
		assertTrue(equals(d.getRow(0), row));
		assertTrue(equals(d.getRow(1), firstRow));
		d.commit();
	}

	public void testInsertAt() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testInsertAt(ds);
		}
	}

	public void testFileCreation() throws Exception {

		String path = "src/test/resources/backup/persona.csv";
		new File(path).delete();

		final int fc = 2;
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];
		final TypeDefinition csvTypeDef = new DefaultTypeDefinition("STRING",
				Type.STRING, null);
		fieldsNames[0] = "id";
		fieldsTypes[0] = csvTypeDef.createType(null);
		fieldsNames[1] = "nombre";
		fieldsTypes[1] = csvTypeDef.createType(null);

		Metadata ddm = new DefaultMetadata(fieldsTypes, fieldsNames);
		dsf.createDataSource(new FileSourceCreation(new File(path), ddm));
		dsf.registerDataSource("persona_created",
				new FileSourceDefinition(path));

		Value v1 = ValueFactory.createValue("Fernando");
		Value v2 = ValueFactory.createValue("Gonzalez");

		DataSource d = dsf.getDataSource("persona_created");

		d.open();
		d.insertFilledRow(new Value[] { v1, v2, ValueFactory.createValue(0L) });
		d.commit();

		d.open();
		assertTrue(d.getRowCount() == 1);
		assertTrue(d.getMetadata().getFieldCount() == 2);
		assertTrue(equals(d.getFieldValue(0, 0), v1));
		assertTrue(equals(d.getFieldValue(0, 1), v2));
		d.cancel();
	}

	public void testCancelEdition() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);
		ds.open();
		String beforeEdition = ds.getAsString();
		long rc = ds.getRowCount();
		ds.insertEmptyRow();
		ds.cancel();
		ds.open();
		assertTrue(ds.getAsString().equals(beforeEdition));
		assertTrue(ds.getRowCount() == rc);
		ds.cancel();
	}
}