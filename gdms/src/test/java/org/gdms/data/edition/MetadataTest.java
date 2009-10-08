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
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ListenerCounter;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteDriver;

public class MetadataTest extends SourceTest {

	private void testAddField(String dsName, Type type) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		Metadata m = d.getMetadata();
		int fc = m.getFieldCount();
		d.addField("extra", type);
		m = d.getMetadata();
		d.commit();
		d.close();
		d.open();
		assertTrue(fc + 1 == d.getMetadata().getFieldCount());
		assertTrue(m.getFieldName(fc).equals("extra"));
		assertTrue(m.getFieldType(fc).getTypeCode() == Type.STRING);

		assertTrue(m.getFieldType(fc).getConstraintValue(Constraint.PK) == null);
		assertTrue(m.getFieldType(fc).getConstraintValue(Constraint.READONLY) == null);
		d.close();
	}

	public void testAddFieldExistsMetadata() {
		DefaultMetadata metadata = new DefaultMetadata();
		try {
			// Add a String field
			metadata.addField("name", Type.STRING);
			// Add a second string field
			metadata.addField("surname", Type.STRING);
			// Add an existing field name
			metadata.addField("name", Type.STRING);
		} catch (DriverException e) {
			assertTrue(true);
		}

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
		d.close();
		d.open();
		assertTrue(fc - 1 == m.getFieldCount());
		d.close();
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
		d.close();
		d.open();
		assertTrue(d.getMetadata().getFieldName(1).equals("nuevo"));
		d.close();
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
		ListenerCounter elc = new ListenerCounter();
		d.addMetadataEditionListener(elc);
		d.removeField(1);
		d.addField("nuevo", type);
		d.setFieldName(1, "jjjj");
		assertTrue(elc.fieldDeletions == 1);
		assertTrue(elc.fieldInsertions == 1);
		assertTrue(elc.fieldModifications == 1);
		assertTrue(elc.total == 3);
		d.close();
	}

	public void testMetadataEditionListenerTest() throws Exception {
		testMetadataEditionListenerTest(super.getAnyNonSpatialResource(),
				TypeFactory.createType(Type.STRING, "STRING"));
	}

	private void testEditionWithFieldAdded(String dsName, Type type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.EDITABLE);
		d.open();
		d.addField("extra", type);
		int fi = d.getFieldIndexByName("extra");
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		Value newValue = ValueFactory.createValue("hi");
		d.setFieldValue(0, fi, newValue);
		d.undo();
		d.redo();
		d.commit();
		d.close();
		d.open();
		assertTrue(super.equals(d.getFieldValue(0, d
				.getFieldIndexByName("extra")), newValue));
		d.close();
	}

	public void testEditionWithFieldAdded() throws Exception {
		testEditionWithFieldAdded(super.getAnyNonSpatialResource(), TypeFactory
				.createType(Type.STRING, "STRING"));
	}

	private void testEditionWithFieldRemoved(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName, DataSourceFactory.EDITABLE);
		d.open();
		String fieldName = d.getFieldName(1);
		Value testValue = d.getFieldValue(0, 2);
		d.removeField(1);
		assertTrue(super.equals(testValue, d.getFieldValue(0, 1)));
		new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
		d.commit();
		d.close();

		d.open();
		assertTrue(d.getFieldIndexByName(fieldName) == -1);
		d.close();
	}

	public void testEditionWithFieldRemoved() throws Exception {
		testEditionWithFieldRemoved(super.getAnyNonSpatialResource());
	}

	private void testRemovePK(String dsName) throws Exception {
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
			d.setFieldName(pkIndex, "s1234d");
			d.commit();
			d.close();
			d.open();
			assertTrue(d.getFieldIndexByName("s1234d") != -1);
			d.close();
		} catch (DriverException e) {
			assertTrue(true);
		}
	}

	public void testRemovePK() throws Exception {
		String[] resources = super.getResourcesWithPK();
		for (String resource : resources) {
			testRemovePK(resource);
		}

	}

	private void testFieldDeletionEditionWhileEdition(String dsName)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		Value[][] content = super.getDataSourceContents(d);
		d.deleteRow(0);
		d.setFieldValue(0, 2, d.getFieldValue(1, 2));
		d.removeField(1);
		assertTrue(super.equals(d.getFieldValue(0, 1), content[2][2]));
		d.setFieldValue(0, 0, d.getFieldValue(1, 0));
		assertTrue(super.equals(d.getFieldValue(0, 0), content[2][0]));
		d.close();
	}

	public void testFieldDeletionEditionWhileEdition() throws Exception {
		testFieldDeletionEditionWhileEdition(super.getAnyNonSpatialResource());
	}

	private void testFieldInsertionEditionWhileEdition(String dsName, Type type)
			throws Exception {
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		String nouveau = "nouveau";
		Value newValue = ValueFactory.createValue(nouveau);
		Value testValue = d.getFieldValue(2, 2);
		int lastField = d.getMetadata().getFieldCount();
		d.deleteRow(0);
		d.setFieldValue(0, 2, d.getFieldValue(1, 2));
		d.addField(nouveau, type);
		d.setFieldValue(0, lastField, newValue);
		assertTrue(super.equals(d.getFieldValue(0, lastField), newValue));
		d.commit();
		d.close();

		d.open();
		assertTrue(d.getMetadata().getFieldName(lastField).toLowerCase()
				.equals(nouveau));
		assertTrue(super.equals(d.getFieldValue(0, lastField), newValue));
		assertTrue(super.equals(d.getFieldValue(0, 2), testValue));
		d.close();
	}

	public void testFieldInsertionEditionWhileEdition() throws Exception {
		testFieldInsertionEditionWhileEdition(super.getAnyNonSpatialResource(),
				TypeFactory.createType(Type.STRING, "String"));
	}

	private void testTypeInAddField(String dsName) throws Exception {
		DataSource d = dsf.getDataSource(dsName);

		d.open();
		int fc = d.getMetadata().getFieldCount();
		Type type = ((ReadWriteDriver) d.getDriver()).getTypesDefinitions()[0]
				.createType();
		d.addField("new", type);
		assertTrue(d.getMetadata().getFieldType(fc).getTypeCode() == type
				.getTypeCode());
		d.commit();
		d.close();

		d = dsf.getDataSource(dsName);
		d.open();
		assertTrue(d.getMetadata().getFieldCount() == fc + 1);
		assertTrue(d.getMetadata().getFieldType(fc).getTypeCode() == type
				.getTypeCode());
		d.close();
	}

	public void testTypeInAddField() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testTypeInAddField(resource);
		}
	}

}
