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

import org.gdms.TestBase;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import org.gdms.data.edition.FakeDBTableSourceDefinition;
import org.gdms.data.edition.FakeFileSourceDefinition;
import org.gdms.data.edition.ReadAndWriteDriver;
import org.gdms.data.edition.ReadDriver;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

import static org.junit.Assert.*;

public class ListenerTest {

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

        @Test
        public void testEditionNotification() throws Exception {
                DataSource d = dsf.getDataSource("object");

                d.addEditionListener(listener);
                d.open();
                editDataSource(d);
                assertEquals(listener.deletions, 1);
                assertEquals(listener.insertions, 4);
                assertEquals(listener.modifications, 1);
                assertEquals(listener.total, 6);
                d.close();
        }

        @Test
        public void testComplexChange() throws Exception {
                DataSource d = dsf.getDataSource("object");

                d.addEditionListener(listener);
                d.open();
                d.setDispatchingMode(DataSource.STORE);
                editDataSource(d);
                d.setDispatchingMode(DataSource.DISPATCH);
                assertEquals(listener.deletions, 1);
                assertEquals(listener.insertions, 4);
                assertEquals(listener.modifications, 1);
                assertEquals(listener.total, 6);
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 15);
                assertEquals(listener.undoRedo, 8);
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 0);
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 1);
                assertEquals(listener.open, 1);
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 1);
                assertEquals(listener.open, 1);
                d.close();
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 2);
                assertEquals(listener.cancel, 1);
        }

        @Test
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
                testCancelButOpenTwice(dsf.getDataSource("db", DataSourceFactory.NORMAL));
        }

        private void testCancelButOpenTwice(DataSource d) throws Exception {
                listener = new ListenerCounter();
                d.addDataSourceListener(listener);
                d.open();
                d.open();
                assertEquals(listener.total, 1);
                assertEquals(listener.open, 1);
                d.close();
                assertEquals(listener.total, 1);
                assertEquals(listener.open, 1);
                assertEquals(listener.cancel, 0);
                d.close();
                assertEquals(listener.total, 2);
                assertEquals(listener.open, 1);
                assertEquals(listener.cancel, 1);
        }

        @Test
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
                assertEquals(listener.total, 2);
                assertEquals(listener.commit, 1);
                d.close();
        }

        @Test
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
                assertEquals(listener.total, 3);
                assertEquals(listener.resync, 1);
                assertEquals(listener.deletions, 1);
                assertEquals(listener.open, 1);
                d1.close();
        }

        @Test
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
                assertEquals(listener.deletions, 1);
                // Second open call doesn't actually open anything
                assertEquals(listener.open, 1);
                assertEquals(listener.commit, 1);
                // The closed one 'd3' should not receive the resync event
                assertEquals(listener.resync, 2);
                assertEquals(listener.total, 5);
        }

        @Before
        public void setUp() throws Exception {
                ReadDriver.initialize();
                ReadDriver.isEditable = true;
                ReadDriver.pk = false;

                dsf = new DataSourceFactory();
                dsf.setTempDir(TestBase.backupDir.getAbsolutePath());
                dsf.setResultDir(TestBase.backupDir);
                DriverManager dm = new DriverManager();
                dm.registerDriver(ReadAndWriteDriver.class);

                SourceManager sourceManager = dsf.getSourceManager();
                sourceManager.setDriverManager(dm);
                sourceManager.register("object", new MemorySourceDefinition(
                        new ReadAndWriteDriver(), "main"));
                final ReadAndWriteDriver fileReadAndWriteDriver = new ReadAndWriteDriver();
                fileReadAndWriteDriver.setFile(new File("."));
                sourceManager.register("file", new FakeFileSourceDefinition(
                        fileReadAndWriteDriver));
                sourceManager.register("db", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:closefailing"));
        }
}