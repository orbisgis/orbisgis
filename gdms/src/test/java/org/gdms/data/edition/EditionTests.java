/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.edition;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.strategies.DiskBufferDriver;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonzalez Cortes
 */
public class EditionTests extends SourceTest {

	public void testNoMemoryUntilEdition() throws Exception {
		Metadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.BOOLEAN) }, new String[] { "bool" });
		DiskBufferDriver dbd = new DiskBufferDriver(dsf, metadata);
		for (int i = 0; i < 1000000; i++) {
			dbd.addValues(new Value[] { ValueFactory.createValue(false) });
		}
		dbd.writingFinished();

		DataSource ds = dsf.getDataSource(dbd.getFile());
		ds.open();
		ds.close();
	}

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
		d.close();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getRow(0), sampleRow, d.getMetadata()));
		d.close();
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
		d.close();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), secondRow));
		d.close();
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
		d.close();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), firstRow));
		d.close();
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
		d.close();

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
		d.close();
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

		d.setFieldValue(1, fieldId, firstRow[fieldId]);
		assertTrue(equals(d.getFieldValue(1, fieldId), firstRow[fieldId]));
		d.insertEmptyRow();
		assertTrue(d.getFieldValue(d.getRowCount() - 1, fieldId).isNull());
		d.close();
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
		d.close();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(equals(d.getFieldValue(ds.length - 1, fieldIndex),
				ds[0][fieldIndex]));
		d.close();
	}

	public void testAdd() throws Exception {
		String[] resources = super.getSmallResources();
		for (String ds : resources) {
			testAdd(ds);
		}
	}

	private void testSQLInjection(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		Value value = ValueFactory.createValue("aa'aa");

		d.open();
		int fieldIndex = d.getFieldIndexByName(super.getStringFieldFor(dsName));
		d.setFieldValue(0, fieldIndex, value);
		d.commit();
		d.close();

		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldIndex), value));
		d.close();
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
		if (d.getFieldType(pkField).getBooleanConstraint(
				Constraint.AUTO_INCREMENT)) {
			row[pkField] = ValueFactory.createNullValue();
		} else {
			row[pkField] = super.getNewPKFor(dsName);
		}
		int lastRow = (int) (d.getRowCount() - 1);
		d.insertFilledRow(row);
		d.commit();
		d.close();

		d.open();
		Value[] newRow = d.getRow(lastRow + 1);
		for (int i = 0; i < newRow.length; i++) {
			if (i != pkField) {
				assertTrue(equals(newRow[i], row[i]));
			}
		}
		d.close();
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
		d.close();

		d.open();
		assertTrue(d.isNull(0, noPKIndex));
		assertTrue(d.isNull(lastRow + 1, noPKIndex));
		d.close();
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
		d.close();
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
		d.close();
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
		dsf.getSourceManager().register("persona_created",
				new FileSourceDefinition(path));

		Value v1 = ValueFactory.createValue("Fernando");
		Value v2 = ValueFactory.createValue("Gonzalez");

		DataSource d = dsf.getDataSource("persona_created");

		d.open();
		d.insertFilledRow(new Value[] { v1, v2 });
		d.commit();
		d.close();

		d.open();
		assertTrue(d.getRowCount() == 1);
		assertTrue(d.getMetadata().getFieldCount() == 2);
		assertTrue(equals(d.getFieldValue(0, 0), v1));
		assertTrue(equals(d.getFieldValue(0, 1), v2));
		d.close();
	}

	public void testCancelEdition() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);
		ds.open();
		String beforeEdition = ds.getAsString();
		long rc = ds.getRowCount();
		ds.insertEmptyRow();
		ds.close();
		ds.open();
		assertTrue(ds.getAsString().equals(beforeEdition));
		assertTrue(ds.getRowCount() == rc);
		ds.close();
	}

	public void testTwoCommitsClose() throws Exception {
		twoCommitClose(true);
		twoCommitClose(false);
	}

	private void twoCommitClose(boolean openTwice) throws Exception,
			NoSuchTableException, DataSourceCreationException, DriverException,
			NonEditableDataSourceException {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);
		ds.open();
		if (openTwice) {
			ds.open();
		}
		long rc = ds.getRowCount();
		ds.insertFilledRow(ds.getRow(0));
		assertTrue(ds.getRowCount() == rc + 1);
		ds.commit();
		assertTrue(ds.getRowCount() == rc + 1);
		ds.deleteRow(0);
		ds.commit();
		assertTrue(ds.getRowCount() == rc);
		ds.close();
		if (openTwice) {
			ds.close();
		}
		assertTrue(!ds.isOpen());
		ds.open();
		assertTrue(ds.getRowCount() == rc);
		ds.close();
	}

	public void testSecondDSIsUpdated() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds1 = dsf.getDataSource(dsName);
		DataSource ds2 = dsf.getDataSource(dsName);
		ds1.open();
		ds2.open();
		long rc = ds1.getRowCount();
		ds1.deleteRow(0);
		assertTrue(ds1.getRowCount() == rc - 1);
		assertTrue(ds2.getRowCount() == rc);
		ds1.commit();
		assertTrue(ds1.getRowCount() == rc - 1);
		assertTrue(ds2.getRowCount() == rc - 1);
		ds1.close();
		assertTrue(!ds1.isOpen());
		assertTrue(ds2.isOpen());
		ds1.open();
		assertTrue(ds1.getRowCount() == rc - 1);
		assertTrue(ds2.getRowCount() == rc - 1);
		ds1.close();
		ds2.close();
	}

	public void testSyncWithSource() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);
		ds.open();
		long rc = ds.getRowCount();
		ds.deleteRow(0);
		assertTrue(rc - 1 == ds.getRowCount());
		ds.syncWithSource();
		assertTrue(rc == ds.getRowCount());
		ds.close();
	}

	public void testInsertWrongNumberOfRows() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
		DataSource ds = dsf.getDataSource(omd);
		ds.open();
		try {
			ds.insertFilledRow(new Value[] { ValueFactory.createValue(""),
					ValueFactory.createValue("hi") });
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			ds.insertFilledRow(new Value[0]);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		ds.commit();
		ds.close();
	}

	public void testCheckValuesInAddedField() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
		DataSource ds = dsf.getDataSource(omd);
		ds.open();
		ds.addField("newfield", TypeFactory.createType(Type.STRING));
		ds.insertEmptyRow();
		String bye = "bye";
		int fieldIndex = ds.getFieldIndexByName("newfield");
		assertTrue(ds.check(fieldIndex, ValueFactory.createValue(bye)) == null);
		ds.setFieldValue(0, fieldIndex, ValueFactory.createValue(bye));
		assertTrue(ds.getString(0, fieldIndex).equals(bye));
		ds.close();
	}
}