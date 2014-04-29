/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceIterator;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;

/**
 * 
 * 
 * @author Fernando Gonzalez Cortes
 */
public class EditionTests extends TestBase {
        
        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
        }

        @Test
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

	@Test
	public void testDelete() throws Exception {
		DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();

		Value[] sampleRow = d.getRow(1);

		int noPkFieldId = 0;
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

		d = dsf.getDataSource(d.getName());
		d.open();
		assertTrue(equals(d.getRow(0), sampleRow, d.getMetadata()));
		d.close();
	}

	@Test
	public void testSetDeletedRow() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();

		int fieldId = 0;
		Value firstRow = d.getFieldValue(0, fieldId);
		Value secondRow = d.getFieldValue(1, fieldId);

		d.setFieldValue(1, fieldId, firstRow);
		d.deleteRow(0); // 0
		d.setFieldValue(0, fieldId, secondRow);

		d.commit();
		d.close();

		d = dsf.getDataSource(d.getName());
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), secondRow));
		d.close();
	}

	@Test
	public void testSetAfterDeletedPreviousRow() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();

		int fieldId = 0;
		Value firstRow = d.getFieldValue(0, fieldId);

		d.deleteRow(0); // 0
		d.setFieldValue(0, fieldId, firstRow);

		d.commit();
		d.close();

		d = dsf.getDataSource(d.getName());
		d.open();
		assertTrue(equals(d.getFieldValue(0, fieldId), firstRow));
		d.close();
	}

        @Test
	public void testUpdate() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();

		int last = (int) (d.getRowCount() - 1);
		int fieldIndex = 0;

		Value[] firstRow = d.getRow(0);
		Value[] secondRow = d.getRow(1);
		Value[] lastRow = d.getRow(last);

		d.insertEmptyRow();
		d.setFieldValue(0, fieldIndex, secondRow[fieldIndex]);
		d.setFieldValue(1, fieldIndex, lastRow[fieldIndex]);
		d.setFieldValue(last + 1, fieldIndex, firstRow[fieldIndex]);

		d.commit();
		d.close();

		d = dsf.getDataSource(d.getName());
		d.open();
		Value[] rowToTest;
		rowToTest = firstRow.clone();
		rowToTest[fieldIndex] = secondRow[fieldIndex];
		assertTrue(equals(rowToTest, d.getRow(0)));

		rowToTest = secondRow.clone();
		rowToTest[fieldIndex] = lastRow[fieldIndex];
		assertTrue(equals(rowToTest, d.getRow(1)));

		assertTrue(equals(firstRow[fieldIndex], d.getFieldValue(last + 1,
				fieldIndex)));
		d.close();
	}

	@Test
        public void testValuesDuringEdition() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();

		int fieldId = 0;

		Value[] firstRow = d.getRow(0);

		d.setFieldValue(1, fieldId, firstRow[fieldId]);
		assertTrue(equals(d.getFieldValue(1, fieldId), firstRow[fieldId]));
		d.insertEmptyRow();
		assertTrue(d.getFieldValue(d.getRowCount() - 1, fieldId).isNull());
		d.close();
	}

	@Test
        public void testAdd() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();
		int fieldIndex = 0;

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

		d = dsf.getDataSource(d.getName());
		d.open();
		assertTrue(equals(d.getFieldValue(ds.length - 1, fieldIndex),
				ds[0][fieldIndex]));
		d.close();
	}

	@Test
        public void testRowCount() throws Exception {
                DataSource d = dsf.getDataSource(getAnyNonSpatialResource());

		d.open();

		int rc = (int) d.getRowCount();
		d.insertEmptyRow();
		assertEquals(d.getRowCount(), rc + 1);
		d.close();
	}

	@Test
        public void testInsertAt() throws Exception {
                DataSource d = dsf.getDataSource(getTempCopyOf(getAnyNonSpatialResource()));

		d.open();
		Value[] row = d.getRow(1);
		Value[] firstRow = d.getRow(0);
		d.insertFilledRowAt(0, row);
		assertTrue(equals(d.getRow(0), row));
		assertTrue(equals(d.getRow(1), firstRow));
		d.commit();
		d.close();
	}

	public void testFileCreation() throws Exception {

		String path = "persona.csv";
                File file = new File(currentWorkspace, path);

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
		dsf.createDataSource(new FileSourceCreation(file, ddm));
		dsf.getSourceManager().register("persona_created",
				new FileSourceDefinition(file, DriverManager.DEFAULT_SINGLE_TABLE_NAME));

		Value v1 = ValueFactory.createValue("Fernando");
		Value v2 = ValueFactory.createValue("Gonzalez");

		DataSource d = dsf.getDataSource("persona_created");

		d.open();
		d.insertFilledRow(new Value[] { v1, v2 });
		d.commit();
		d.close();

		d.open();
		assertEquals(d.getRowCount(),1);
		assertEquals(d.getMetadata().getFieldCount(),2);
		assertTrue(equals(d.getFieldValue(0, 0), v1));
		assertTrue(equals(d.getFieldValue(0, 1), v2));
		d.close();
	}

	@Test
        public void testCancelEdition() throws Exception {
		File ff = super.getAnySpatialResource();
		DataSource ds = dsf.getDataSource(ff);
		ds.open();
		String beforeEdition = ds.getAsString();
		long rc = ds.getRowCount();
		ds.insertEmptyRow();
		ds.close();
		ds.open();
		assertEquals(ds.getAsString(),beforeEdition);
		assertEquals(ds.getRowCount(),rc);
		ds.close();
	}

	@Test
        public void testTwoCommitsClose() throws Exception {
		twoCommitClose(true);
		twoCommitClose(false);
	}

	private void twoCommitClose(boolean openTwice) throws Exception,
			NoSuchTableException, DataSourceCreationException, DriverException,
			NonEditableDataSourceException {
		File ff = getTempCopyOf(super.getAnyNonSpatialResource());
		DataSource ds = dsf.getDataSource(ff);
		ds.open();
		if (openTwice) {
			ds.open();
		}
		long rc = ds.getRowCount();
		ds.insertFilledRow(ds.getRow(0));
		assertEquals(ds.getRowCount(),rc + 1);
		ds.commit();
		assertEquals(ds.getRowCount(),rc + 1);
		ds.deleteRow(0);
		ds.commit();
		assertEquals(ds.getRowCount(),rc);
		ds.close();
		if (openTwice) {
			ds.close();
		}
		assertFalse(ds.isOpen());
		ds.open();
		assertEquals(ds.getRowCount(),rc);
		ds.close();
	}

	@Test
        public void testSecondDSIsUpdated() throws Exception {
		String dsName = "test";
                sm.register(dsName, getTempCopyOf(super.getAnyNonSpatialResource()));
		DataSource ds1 = dsf.getDataSource(dsName);
		DataSource ds2 = dsf.getDataSource(dsName);
		ds1.open();
		ds2.open();
		long rc = ds1.getRowCount();
		ds1.deleteRow(0);
		assertEquals(ds1.getRowCount(),rc - 1);
		assertEquals(ds2.getRowCount(),rc);
		ds1.commit();
		assertEquals(ds1.getRowCount(),rc - 1);
		assertEquals(ds2.getRowCount(),rc - 1);
		ds1.close();
		assertFalse(ds1.isOpen());
		assertTrue(ds2.isOpen());
		ds1.open();
		assertEquals(ds1.getRowCount(),rc - 1);
		assertEquals(ds2.getRowCount(),rc - 1);
		ds1.close();
		ds2.close();
	}

	@Test
        public void testSyncWithSource() throws Exception {
                String dsName = "test";
                sm.register(dsName, getTempCopyOf(super.getAnyNonSpatialResource()));
		DataSource ds = dsf.getDataSource(dsName);
		ds.open();
		long rc = ds.getRowCount();
		ds.deleteRow(0);
		assertEquals(rc - 1,ds.getRowCount());
		ds.syncWithSource();
		assertEquals(rc,ds.getRowCount());
		ds.close();
	}

	@Test
        public void testInsertWrongNumberOfRows() throws Exception {
		MemoryDataSetDriver omd = new MemoryDataSetDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
		DataSource ds = dsf.getDataSource(omd,"main");
		ds.open();
		try {
			ds.insertFilledRow(new Value[] { ValueFactory.createValue(""),
					ValueFactory.createValue("hi") });
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			ds.insertFilledRow(new Value[0]);
			fail();
		} catch (IllegalArgumentException e) {
		}
		ds.commit();
		ds.close();
	}

	@Test
        public void testCheckValuesInAddedField() throws Exception {
		MemoryDataSetDriver omd = new MemoryDataSetDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
		DataSource ds = dsf.getDataSource(omd,"main");
		ds.open();
		ds.addField("newfield", TypeFactory.createType(Type.STRING));
		ds.insertEmptyRow();
		String bye = "bye";
		int fieldIndex = ds.getFieldIndexByName("newfield");
		assertNull(ds.check(fieldIndex, ValueFactory.createValue(bye)));
		ds.setFieldValue(0, fieldIndex, ValueFactory.createValue(bye));
		assertEquals(ds.getString(0, fieldIndex),bye);
		ds.close();
	}
        
        @Test
        public void testIterator() throws DriverException {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
                omd.addValues(ValueFactory.createValue("toto1"));
                omd.addValues(ValueFactory.createValue("toto2"));
                omd.addValues(ValueFactory.createValue("toto3"));
                omd.addValues(ValueFactory.createValue("toto4"));
                
                DataSource ds = dsf.getDataSource(omd,"main", DataSourceFactory.EDITABLE);
		ds.open();
                Iterator<Value[]> i = ds.iterator();
                assertTrue(i.hasNext());
                Value[] v = i.next();
                assertEquals(1, v.length);
                assertEquals("toto1", v[0].getAsString());
                v = i.next();
                assertEquals("toto2", v[0].getAsString());
                i.remove();
                v = i.next();
                assertEquals("toto3", v[0].getAsString());
                i.next();
                assertFalse(i.hasNext());
                
                assertEquals(3, ds.getRowCount());
                assertEquals("toto3", ds.getString(1, 0));
        }
        
        @Test
        public void testIteratorEdit() throws DriverException {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
                omd.addValues(ValueFactory.createValue("toto1"));
                omd.addValues(ValueFactory.createValue("toto2"));
                omd.addValues(ValueFactory.createValue("toto3"));
                omd.addValues(ValueFactory.createValue("toto4"));
                
                DataSource ds = dsf.getDataSource(omd,"main", DataSourceFactory.EDITABLE);
		ds.open();
                DataSourceIterator i = ds.iterator();
                
                Value[] v = i.next();
                v[0] = ValueFactory.createValue("toto5");
                assertEquals("toto1", ds.getString(0, 0));
                i.update(v);
                assertEquals("toto5", ds.getString(0, 0));
        }
}