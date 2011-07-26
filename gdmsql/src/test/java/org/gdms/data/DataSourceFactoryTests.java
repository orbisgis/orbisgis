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

import java.io.File;

import org.gdms.SQLBaseTest;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceFactoryTests extends SQLBaseTest {

        private SourceManager sm;

        @Before
        @Override
        public void setUp() throws Exception {
                super.setUp();
                sm = dsf.getSourceManager();
        }

        /**
         * Tests the naming of operation layer datasource
         *
         * @throws Throwable
         *             DOCUMENT ME!
         */
        @Test
        public void testOperationDataSourceName() throws Throwable {
                dsf.getSourceManager().remove("big");
                dsf.getSourceManager().register(
                        "big",
                        new FileSourceCreation(new File(SQLBaseTest.internalData
                        + "hedgerow.shp"), null));
                DataSource d = dsf.getDataSourceFromSQL("select * from big;");
                assertNotNull(dsf.getDataSource(d.getName()));
        }

        @Test
        public void testSeveralNames() throws Exception {
                String dsName = super.getNonDBSmallResources()[0];
                testSeveralNames(dsName);
                testSeveralNames(dsf.getDataSourceFromSQL("select * from " + dsName + ";").getName());
        }

        private void testSeveralNames(String dsName) throws
                SourceAlreadyExistsException, DriverLoadException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException, NoSuchTableException {
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName(dsName, secondName);
                checkNames(dsName, secondName);
                try {
                        sm.addName("e" + System.currentTimeMillis(), "qosgsdq");
                        fail();
                } catch (NoSuchTableException ex) {
                }

        }

        private void checkNames(String dsName, String secondName)
                throws DriverLoadException, NoSuchTableException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException {
                assertTrue(dsf.getSourceManager().getSource(dsName) == dsf.getSourceManager().getSource(secondName));
                DataSource ds1 = dsf.getDataSource(dsName);
                DataSource ds2 = dsf.getDataSource(secondName);
                ds1.open();
                ds2.open();
                assertTrue(equals(getDataSourceContents(ds1),
                        getDataSourceContents(ds2)));
                ds1.close();
                ds2.close();
        }

        @Test
        public void testChangeNameOnExistingDataSources() throws Exception {
                SQLDataSourceFactory dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                dsf.getSourceManager().removeAll();
                dsf.getSourceManager().register("file",
                        new File(SQLBaseTest.internalData, "test.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select * from file;");
                dsf.getSourceManager().rename(ds.getName(), "sql");
                DataSource ds2 = dsf.getDataSource("sql");
                assertEquals(ds.getName(), ds2.getName());
        }

        @Test
        public void testSQLSources() throws Exception {
                dsf.getSourceManager().register("testH", new File(SQLBaseTest.internalData, "hedgerow.shp"));
                dsf.register("sql",
                        "select * from testH;");
                DataSource ds = dsf.getDataSource("sql");
                assertEquals((ds.getSource().getType() & SourceManager.SQL), SourceManager.SQL);
                assertFalse(ds.isEditable());
        }
}
