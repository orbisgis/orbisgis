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
package org.gdms.data;

import junit.framework.TestCase;

import org.gdms.data.edition.FakeDBTableSourceDefinition;
import org.gdms.data.edition.FakeFileSourceDefinition;
import org.gdms.data.edition.ReadAndWriteDriver;
import org.gdms.data.edition.ReadDriver;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

public class ListenerTest extends TestCase {

	public ListenerCounter listener = new ListenerCounter();
	private DataSourceFactory dsf;

	private void editDataSource(DataSource d) throws DriverException {
		d.deleteRow(0);
		d.insertEmptyRow();
		d.insertEmptyRowAt(0);
		Value[] row = d.getRow(0);
		d.insertFilledRow(row);
		d.insertFilledRowAt(0, row);
		d.setFieldValue(0, 0, d.getFieldValue(1, 0));
	}

	public void testEditionNotification() throws Exception {
		DataSource d = dsf.getDataSource("object");

		d.addEditionListener(listener);
		d.open();
		editDataSource(d);
		assertTrue(listener.deletions == 1);
		assertTrue(listener.insertions == 4);
		assertTrue(listener.modifications == 1);
		assertTrue(listener.total == 6);
		d.close();
	}

	public void testComplexChange() throws Exception {
		DataSource d = dsf.getDataSource("object");

		d.addEditionListener(listener);
		d.open();
		d.setDispatchingMode(DataSource.STORE);
		editDataSource(d);
		d.setDispatchingMode(DataSource.DISPATCH);
		assertTrue(listener.deletions == 1);
		assertTrue(listener.insertions == 4);
		assertTrue(listener.modifications == 1);
		assertTrue(listener.total == 6);
		d.close();
	}

	public void testUndoRedoChanges() throws Exception {
		DataSource d = dsf.getDataSource("object", DataSourceFactory.EDITABLE);

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
		d.close();
	}

	public void testIgnoreChanges() throws Exception {
		DataSource d = dsf.getDataSource("object", DataSourceFactory.EDITABLE);

		d.addEditionListener(listener);
		d.open();
		d.setDispatchingMode(DataSource.IGNORE);
		editDataSource(d);
		for (int i = 0; i < 6; i++) {
			d.undo();
		}
		d.redo();
		d.undo();
		assertTrue(listener.total == 0);
		d.close();
	}

	public void testOpen() throws Exception {
		testOpen(dsf.getDataSource("object", DataSourceFactory.EDITABLE));
		testOpen(dsf.getDataSource("object", DataSourceFactory.NORMAL));
		testOpen(dsf.getDataSource("file", DataSourceFactory.EDITABLE));
		testOpen(dsf.getDataSource("file", DataSourceFactory.NORMAL));
		testOpen(dsf.getDataSource("db", DataSourceFactory.EDITABLE));
		testOpen(dsf.getDataSource("db", DataSourceFactory.NORMAL));
	}

	private void testOpen(DataSource d) throws Exception {
		listener = new ListenerCounter();
		d.addDataSourceListener(listener);
		d.open();
		assertTrue(listener.total == 1);
		assertTrue(listener.open == 1);
		d.close();
	}

	public void testOpenTwice() throws Exception {
		testOpenTwice(dsf.getDataSource("object", DataSourceFactory.EDITABLE));
		testOpenTwice(dsf.getDataSource("object", DataSourceFactory.NORMAL));
		testOpenTwice(dsf.getDataSource("file", DataSourceFactory.EDITABLE));
		testOpenTwice(dsf.getDataSource("file", DataSourceFactory.NORMAL));
		testOpenTwice(dsf.getDataSource("db", DataSourceFactory.EDITABLE));
		testOpenTwice(dsf.getDataSource("db", DataSourceFactory.NORMAL));

	}

	private void testOpenTwice(DataSource d) throws Exception {
		listener = new ListenerCounter();
		d.addDataSourceListener(listener);
		d.open();
		d.open();
		assertTrue(listener.total == 1);
		assertTrue(listener.open == 1);
		d.close();
		d.close();
	}

	public void testCancel() throws Exception {
		testCancel(dsf.getDataSource("object", DataSourceFactory.EDITABLE));
		testCancel(dsf.getDataSource("object", DataSourceFactory.NORMAL));
		testCancel(dsf.getDataSource("file", DataSourceFactory.EDITABLE));
		testCancel(dsf.getDataSource("file", DataSourceFactory.NORMAL));
		testCancel(dsf.getDataSource("db", DataSourceFactory.EDITABLE));
		testCancel(dsf.getDataSource("db", DataSourceFactory.NORMAL));
	}

	private void testCancel(DataSource d) throws Exception {
		listener = new ListenerCounter();
		d.addDataSourceListener(listener);
		d.open();
		d.close();
		assertTrue(listener.total == 2);
		assertTrue(listener.cancel == 1);
	}

	public void testCancelButOpenTwice() throws Exception {
		testCancelButOpenTwice(dsf.getDataSource("object",
				DataSourceFactory.EDITABLE));
		testCancelButOpenTwice(dsf.getDataSource("object",
				DataSourceFactory.NORMAL));
		testCancelButOpenTwice(dsf.getDataSource("file",
				DataSourceFactory.EDITABLE));
		testCancelButOpenTwice(dsf.getDataSource("file",
				DataSourceFactory.NORMAL));
		testCancelButOpenTwice(dsf.getDataSource("db",
				DataSourceFactory.EDITABLE));
		testCancelButOpenTwice(dsf
				.getDataSource("db", DataSourceFactory.NORMAL));
	}

	private void testCancelButOpenTwice(DataSource d) throws Exception {
		listener = new ListenerCounter();
		d.addDataSourceListener(listener);
		d.open();
		d.open();
		assertTrue(listener.total == 1);
		assertTrue(listener.open == 1);
		d.close();
		assertTrue(listener.total == 1);
		assertTrue(listener.open == 1);
		assertTrue(listener.cancel == 0);
		d.close();
		assertTrue(listener.total == 2);
		assertTrue(listener.open == 1);
		assertTrue(listener.cancel == 1);
	}

	public void testCommit() throws Exception {
		testCommit(dsf.getDataSource("object", DataSourceFactory.EDITABLE));
		testCommit(dsf.getDataSource("file", DataSourceFactory.EDITABLE));
		testCommit(dsf.getDataSource("db", DataSourceFactory.EDITABLE));
	}

	private void testCommit(DataSource d) throws Exception {
		listener = new ListenerCounter();
		d.addDataSourceListener(listener);
		d.open();
		d.commit();
		assertTrue(listener.total == 2);
		assertTrue(listener.commit == 1);
		d.close();
	}

	public void testResync() throws Exception {
		testResync(dsf.getDataSource("object"));
		testResync(dsf.getDataSource("file"));
		testResync(dsf.getDataSource("db"));
	}

	private void testResync(DataSource d1) throws Exception {
		listener = new ListenerCounter();
		d1.addDataSourceListener(listener);
		d1.addEditionListener(listener);
		d1.open();
		d1.deleteRow(0);
		d1.syncWithSource();
		assertTrue(listener.total == 3);
		assertTrue(listener.resync == 1);
		assertTrue(listener.deletions == 1);
		assertTrue(listener.open == 1);
		d1.close();
	}

	public void testResyncEventOnAnotherDSCommit() throws Exception {
		DataSource d1 = dsf.getDataSource("file");
		DataSource d2 = dsf.getDataSource("file");
		DataSource d3 = dsf.getDataSource("file");
		listener = new ListenerCounter();
		d1.addDataSourceListener(listener);
		d1.addEditionListener(listener);
		d2.addEditionListener(listener);
		d3.addEditionListener(listener);
		d1.open();
		d2.open();
		d1.deleteRow(0);
		d1.commit();
		d1.close();
		assertTrue(listener.deletions == 1);
		// Second open call doesn't actually open anything
		assertTrue(listener.open == 1);
		assertTrue(listener.commit == 1);
		// The closed one 'd3' should not receive the resync event
		assertTrue(listener.resync == 2);
		assertTrue(listener.total == 5);
	}

	@Override
	protected void setUp() throws Exception {
		ReadDriver.initialize();
		ReadDriver.isEditable = true;
		ReadDriver.pk = false;

		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		DriverManager dm = new DriverManager();
		dm.registerDriver(ReadAndWriteDriver.class);
		
		SourceManager sourceManager = dsf.getSourceManager();
		sourceManager.setDriverManager(dm);
		sourceManager.register("object", new ObjectSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("file", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("db", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:closefailing"));
	}

}