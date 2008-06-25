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

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class UndoRedoTests extends SourceTest {

	public void testUndoRedoMetadata() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.EDITABLE);

		d.open();
		Value[][] content = super.getDataSourceContents(d);
		d.removeField(2);
		d.undo();
		assertTrue(super.equals(content, super.getDataSourceContents(d)));
		d.commit();
		d.close();
		d.open();
		assertTrue(super.equals(content, super.getDataSourceContents(d)));
		d.close();
	}

	public void testAlphanumericModifyUndoRedo() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.EDITABLE);

		d.open();
		Value v2 = d.getFieldValue(1, 0);
		Value v1 = d.getFieldValue(0, 0);
		d.setFieldValue(0, 0, v2);
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(equals(d.getFieldValue(0, 0), v1));
			d.redo();
			assertTrue(equals(d.getFieldValue(0, 0), v2));
		}
		d.undo();
		d.commit();
		d.close();
	}

	public void testAlphanumericDeleteUndoRedo() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.EDITABLE);

		d.open();
		Value v1 = d.getFieldValue(1, 0);
		Value v2 = d.getFieldValue(2, 0);
		d.deleteRow(1);
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(equals(d.getFieldValue(1, 0), v1));
			d.redo();
			assertTrue(equals(d.getFieldValue(1, 0), v2));
		}
		d.undo();
		d.commit();
		d.close();
	}

	public void testAlphanumericInsertUndoRedo() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.EDITABLE);

		d.open();
		Value v1 = d.getFieldValue(1, 0);
		d.insertEmptyRowAt(1);
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(equals(d.getFieldValue(1, 0), v1));
			d.redo();
			assertTrue(d.getFieldValue(1, 0).isNull());
		}
		d.undo();
		d.commit();
		d.close();
	}

	private void testSpatialModifyUndoRedo(SpatialDataSourceDecorator d)
			throws Exception {
		Value v2 = d.getFieldValue(1, 0);
		Value v1 = d.getFieldValue(0, 0);
		d.setFieldValue(0, 0, v2);
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(equals(d.getFieldValue(0, 0), v1));
			d.redo();
			assertTrue(equals(d.getFieldValue(0, 0), v2));
		}
	}

	public void testSpatialModifyUndoRedo() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		testSpatialModifyUndoRedo(d);
		d.undo();
		d.commit();
		d.close();
	}

	private void testSpatialDeleteUndoRedo(SpatialDataSourceDecorator d)
			throws Exception {
		long rc = d.getRowCount();
		Value v1 = d.getFieldValue(1, 0);
		Value v2 = d.getFieldValue(2, 0);
		d.deleteRow(1);
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(equals(d.getFieldValue(1, 0), v1));
			d.redo();
			assertTrue(equals(d.getFieldValue(1, 0), v2));
			assertTrue(rc - 1 == d.getRowCount());
		}
	}

	public void testSpatialDeleteUndoRedo() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		testSpatialDeleteUndoRedo(d);
		d.undo();
		d.commit();
		d.close();
	}

	private void testSpatialInsertUndoRedo(SpatialDataSourceDecorator d)
			throws Exception {
		long rc = d.getRowCount();
		d.insertEmptyRow();
		for (int i = 0; i < 100; i++) {
			d.undo();
			assertTrue(rc == d.getRowCount());
			d.redo();
			assertTrue(rc == d.getRowCount() - 1);
		}
	}

	public void testSpatialInsertUndoRedo() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		testSpatialInsertUndoRedo(d);
		d.undo();
		d.commit();
		d.close();
	}

	public void testAlphanumericEditionUndoRedo(DataSource d) throws Exception {
		byte[] snapshot1 = DigestUtilities.getDigest(d);
		d.setFieldValue(0, 0, d.getFieldValue(1, 0));
		byte[] snapshot2 = DigestUtilities.getDigest(d);
		d.setFieldValue(0, 0, d.getFieldValue(2, 0));
		byte[] snapshot3 = DigestUtilities.getDigest(d);
		d.deleteRow(0);
		byte[] snapshot4 = DigestUtilities.getDigest(d);
		d.setFieldValue(0, 1, d.getFieldValue(1, 1));
		byte[] snapshot5 = DigestUtilities.getDigest(d);
		d.insertEmptyRowAt(0);
		byte[] snapshot6 = DigestUtilities.getDigest(d);
		d.setFieldName(1, "newName");
		byte[] snapshot7 = DigestUtilities.getDigest(d);
		d.removeField(1);
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot7, DigestUtilities.getDigest(d)));
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot6, DigestUtilities.getDigest(d)));
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot5, DigestUtilities.getDigest(d)));
		d.redo();
		assertTrue(DigestUtilities.equals(snapshot6, DigestUtilities.getDigest(d)));
		d.undo();
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot4, DigestUtilities.getDigest(d)));
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot3, DigestUtilities.getDigest(d)));
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot2, DigestUtilities.getDigest(d)));
		d.redo();
		assertTrue(DigestUtilities.equals(snapshot3, DigestUtilities.getDigest(d)));
		d.undo();
		d.undo();
		assertTrue(DigestUtilities.equals(snapshot1, DigestUtilities.getDigest(d)));
	}

	public void testAlphanumericEditionUndoRedo() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnyNonSpatialResource(),
				DataSourceFactory.EDITABLE);

		d.open();
		testAlphanumericEditionUndoRedo(d);
		d.commit();
		d.close();
	}

	public void testSpatialEditionUndoRedo() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		testAlphanumericEditionUndoRedo(d);
		d.commit();
		d.close();
	}

	public void testAddTwoRowsAndUndoBoth() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		Value[] row = d.getRow(0);
		long rc = d.getRowCount();
		d.insertFilledRow(row);
		d.insertFilledRow(row);
		d.undo();
		d.undo();
		assertTrue(d.getRowCount() == rc);
		d.close();
	}

	public void testInsertModify() throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource(),
						DataSourceFactory.EDITABLE));

		d.open();
		int ri = (int) d.getRowCount();
		d.insertEmptyRow();
		Value v1 = d.getFieldValue(0, 0);
		Value v2 = d.getFieldValue(0, 1);
		d.setFieldValue(ri, 0, v1);
		d.setFieldValue(ri, 1, v2);
		d.undo();
		d.undo();
		d.undo();
		d.redo();
		d.redo();
		d.redo();
		assertTrue(equals(d.getFieldValue(ri, 0), v1));
		assertTrue(equals(d.getFieldValue(ri, 1), v2));
	}
}
