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
package org.gdms.source;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.strategies.SumQuery;

import static org.junit.Assert.*;

public class SourceManagementTest {

        private static final String SOURCE = "source";
        private SourceManager sm;
        private SQLDataSourceFactory dsf;
        private File testFile;
        private DBSource testDB;
        private String sql = "select count(the_geom) from myfile;";

        @Test
        public void testPersistence() throws Exception {
                sm.removeAll();
                sm.register("myfile", new File(SQLBaseTest.internalData, "landcover2000.shp"));
                dsf.register("sql", sql);

                String sqlContent = getContent("sql");

                sm.saveStatus();
                instantiateDSF();

                assertEquals(sqlContent, getContent("sql"));

        }

        private String getContent(String name) throws Exception {
                DataSource ds = dsf.getDataSource(name);
                ds.open();
                String ret = ds.getAsString();
                ds.close();

                return ret;
        }

        @Test
        public void testSelectDependencies() throws Exception {
                sm.removeAll();
                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*(file.id :: int) from db, file "
                        + "where (file.id :: int) <> 234;";
                dsf.register("sql", sql);
                DataSource ds = dsf.getDataSource("sql");
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"db", "file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"db", "file"}));
                sql = "file union file;";
                dsf.register("sql2", sql);
                ds = dsf.getDataSource("sql2");
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));

                String[] srcDeps = dsf.getDataSource("file").getReferencedSources();
                assertEquals(srcDeps.length, 0);
        }

        @Test
        public void testCannotDeleteDependedSource() throws Exception {
                sm.removeAll();
                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';";
                sm.remove("file");
                sm.remove("db");

                sm.register("db", testDB);
                sm.register("file", testFile);
                dsf.register("sql", sql);

                try {
                        sm.remove("file");
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        sm.remove("db");
                        fail();
                } catch (IllegalStateException e) {
                }

                sm.remove("sql");
                sm.remove("file");
                sm.remove("db");
        }

        @Test
        public void testCanDeleteIfDependentSourceIsNotWellKnown() throws Exception {
                sm.removeAll();
                sm.register("db", testDB);
                sm.register("file", testFile);
                dsf.executeSQL("select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';");
                sm.remove("file");
                sm.remove("db");
        }

        @Test
        public void testDependentDependingSync() throws Exception {
                sm.removeAll();
                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';";
                dsf.register("sql", sql);
                sql = "select * from sql, file;";
                dsf.register("sql2", sql);
                // Anonimous ds should not been taken into account for dependencies
                dsf.executeSQL(sql);
                Source src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql",
                                "sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql",
                                "sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db"}));
                src = sm.getSource("sql2");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db", "sql"}));

                sm.remove("sql2");
                src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db"}));
                src = sm.getSource("sql2");
                assertNull(src);

                sm.remove("sql");
                src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertNull(src);
        }

        private boolean setIs(String[] referencingSources, String[] test) {
                if (referencingSources.length != test.length) {
                        return false;
                } else {
                        ArrayList<String> set = new ArrayList<String>();
                        for (String string : referencingSources) {
                                set.add(string);
                        }
                        for (String string : test) {
                                set.remove(string);
                        }

                        return set.isEmpty();
                }
        }

        @Test
        public void testGetAlreadyRegisteredSourceAnonimously() throws Exception {
                sm.removeAll();
                sm.register("myfile", new File(SQLBaseTest.internalData, "landcover2000.shp"));
                dsf.register("mySQL", sql);


                DataSource ds = dsf.getDataSourceFromSQL(sql);
                assertEquals(ds.getName(), "mySQL");
        }

        @Test
        public void testCannotRegisterTwice() throws Exception {
                sm.removeAll();
                sm.register("myfile", new File(SQLBaseTest.internalData, "landcover2000.shp"));
                dsf.register("mySQL", sql);

                try {
                        dsf.register("d", sql);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }

                try {
                        dsf.nameAndRegister(sql);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
        }

        @Test
        public void testSQLSourceType() throws Exception {
                dsf.register("alphasql", "select * from " + SOURCE + ";");
                assertEquals((sm.getSource("alphasql").getType() & SourceManager.SQL), SourceManager.SQL);
        }

        @Test
        public void testCustomQueryDependences() throws Exception {
                SumQuery sq = new SumQuery();
                if (dsf.getFunctionManager().getFunction(sq.getName()) == null) {
                        dsf.getFunctionManager().addFunction(SumQuery.class);
                }
                dsf.register("sum", "select * from sumquery(" + SOURCE + ", 'id');");
                String[] deps = sm.getSource("sum").getReferencedSources();
                assertEquals(deps.length, 1);
                assertEquals(deps[0], SOURCE);
        }

        @Test
        public void testDependingNotWellKnownSourcesRemoved() throws Exception {
                DataSource ds = dsf.getDataSourceFromSQL("select * from " + SOURCE + ";");
                assertEquals(ds.getReferencedSources().length, 1);

                String nwkn = ds.getName();
                dsf.getSourceManager().remove(SOURCE);
                assertFalse(dsf.getSourceManager().exists(nwkn));
        }

        @Before
        public void setUp() throws Exception {
                instantiateDSF();
                try {
                        sm.removeAll();
                } catch (IOException e) {
                }
                testFile = new File(SQLBaseTest.internalData + "test.csv");
                testDB = new DBSource(null, 0, SQLBaseTest.backupDir
                        + "testhsqldb", "sa", "", "gisapps", "jdbc:hsqldb:file");
                sm.remove(SOURCE);
                sm.register(SOURCE, testFile);
        }

        private void instantiateDSF() {
                dsf = new SQLDataSourceFactory(SQLBaseTest.backupDir
                        + "../backupsource-management");
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                sm = dsf.getSourceManager();

        }
}
