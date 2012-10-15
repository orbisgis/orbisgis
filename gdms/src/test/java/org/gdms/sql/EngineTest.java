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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLScript;
import org.gdms.sql.engine.SQLStatement;

/**
 *
 * @author Antoine Gourlay
 */
public class EngineTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        @Test
        public void testSerializationOfQuery() throws Exception {
                final String sql = "SELECT abs(42), 'toto';";

                SQLStatement c = Engine.parse(sql, dsf.getProperties());
                File a = File.createTempFile("sql", null);
                a.delete();

                FileOutputStream s = new FileOutputStream(a);
                c.save(s);
                s.close();

                FileInputStream is = new FileInputStream(a);
                SQLStatement st = Engine.load(is, dsf.getProperties());
                assertEquals(sql, st.getSQL());
                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(42, d.getInt(0, 0));
                assertEquals("toto", d.getString(0, 1));
                d.close();

                a.delete();
        }

        @Test
        public void testSerializationOfScript() throws Exception {
                SQLScript s = Engine.parseScript(EngineTest.class.getResourceAsStream("test-script-serialization.sql"));

                File a = File.createTempFile("gdms-", ".bsql");
                a.delete();

                FileOutputStream out = new FileOutputStream(a);
                s.save(out);
                out.close();

                s = Engine.loadScript(a);
                s.setDataSourceFactory(dsf);
                s.setValueParameter("othervalue", ValueFactory.createValue(12));
                s.execute();

                assertTrue(sm.exists("tata"));
                DataSource d = dsf.getDataSource("tata");
                d.open();
                assertEquals(54, d.getInt(0, 0));
                assertEquals(8, d.getInt(0, 1));
                d.close();

                sm.delete("tata");
                a.delete();
        }

        @Test
        public void testSimpleParametrizedQuery() throws Exception {
                final String sql = "SELECT abs(@{myParam}), 'toto';";

                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                st.setValueParameter("myParam", ValueFactory.createValue(42));

                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(42, d.getInt(0, 0));
                assertEquals("toto", d.getString(0, 1));
                d.close();

                sm.delete(d.getName());
                st.setValueParameter("myParam", ValueFactory.createValue(12));
                d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(12, d.getInt(0, 0));
                assertEquals("toto", d.getString(0, 1));
                d.close();

                sm.delete(d.getName());
        }
        
       

        @Test
        public void testFieldAndTableParametrizedQuery() throws Exception {
                sm.register("toto", "SELECT 42 as hi, 'hello!' AS bonjour;");

                final String sql = "SELECT @{myfield} FROM @{mytable};";

                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                st.setFieldParameter("myfield", "hi");
                st.setTableParameter("mytable", "toto");

                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals(42, d.getInt(0, 0));
                d.close();

                sm.delete(d.getName());

                st.setFieldParameter("myfield", "bonjour");
                st.setTableParameter("mytable", "toto");

                d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals("hello!", d.getString(0, 0));
                d.close();

                sm.delete(d.getName());
        }
        
        @Test
        public void testOperatorTableParametrizedQuery() throws Exception {
                sm.register("toto", "SELECT 42+1 as hi, 'hello!' AS bonjour;");

                final String sql = "SELECT @{myfield} FROM @{mytable};";

                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                st.setFieldParameter("myfield", "hi");
                st.setTableParameter("mytable", "toto");

                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals(43, d.getInt(0, 0));
                d.close();

                sm.delete(d.getName());

                st.setFieldParameter("myfield", "bonjour");
                st.setTableParameter("mytable", "toto");

                d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals("hello!", d.getString(0, 0));
                d.close();

                sm.delete(d.getName());
        }
        
        
        @Test
        public void testFieldFunctionAndOperatorTableParametrizedQuery() throws Exception {
                sm.register("toto", "SELECT autonumeric()+1 as hi, 'hello!' AS bonjour;");

                final String sql = "SELECT @{myfield} FROM @{mytable};";

                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                st.setFieldParameter("myfield", "hi");
                st.setTableParameter("mytable", "toto");

                DataSource d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals(1, d.getInt(0, 0));
                d.close();

                sm.delete(d.getName());

                st.setFieldParameter("myfield", "bonjour");
                st.setTableParameter("mytable", "toto");

                d = dsf.getDataSource(st, DataSourceFactory.DEFAULT, new NullProgressMonitor());

                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals("hello!", d.getString(0, 0));
                d.close();

                sm.delete(d.getName());
        }

        @Test
        public void testParamInExcept() throws Exception {
                sm.register("toto", "SELECT 42 as hi, 'hello!' AS bonjour;");
                
                String sql = "SELECT * EXCEPT (@{myfield}) FROM toto;";
                // should not fail
                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                
                st.setDataSourceFactory(dsf);
                st.setFieldParameter("myfield", "bonjour");
                st.prepare();
                DataSet d = st.execute();
                
                assertEquals(1, d.getMetadata().getFieldCount());
                assertEquals(42, d.getInt(0, 0));
                
                st.cleanUp();
                
                st.setDataSourceFactory(dsf);
                st.setFieldParameter("myfield", "hi");
                st.prepare();
                d = st.execute();
                
                assertEquals(1, d.getMetadata().getFieldCount());
                assertEquals("hello!", d.getString(0, 0));
                
                st.cleanUp();
        }
        
        @Test
        public void testParamInAlias() throws Exception {
                String sql = "SELECT 42 as @{hi}, 'hello!' AS bonjour;";
                // should not fail
                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                
                st.setDataSourceFactory(dsf);
                st.setFieldParameter("hi", "hello");
                st.prepare();
                DataSet d = st.execute();
                
                assertEquals("hello", d.getMetadata().getFieldName(0));
                
                st.cleanUp();
                
                st.setDataSourceFactory(dsf);
                st.setFieldParameter("hi", "hi");
                st.prepare();
                d = st.execute();
                
                assertEquals("hi", d.getMetadata().getFieldName(0));
                
                st.cleanUp();
                
                st.setDataSourceFactory(dsf);
                try {
                        // should fail
                        st.prepare();
                        fail();
                } catch (Exception e) {
                        // ok
                }
        }
        
        @Test
        public void testParamInExecutorPresent() throws Exception {
                String sql = "EXECUTE Register(@{hi});";
                // should not fail
                SQLStatement st = Engine.parse(sql, dsf.getProperties());
                
                st.setDataSourceFactory(dsf);
                st.setValueParameter("hi", ValueFactory.createValue("hello"));
                st.prepare();
                st.cleanUp();
        }
}
