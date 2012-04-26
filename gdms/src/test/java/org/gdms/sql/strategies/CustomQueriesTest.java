/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.sql.strategies;

import org.junit.Before;
import org.junit.Test;
import org.gdms.data.types.IncompatibleTypesException;
import java.io.File;

import org.gdms.TestBase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.system.RegisterCall;
import org.gdms.sql.function.ScalarFunction;
import org.gdms.sql.function.executor.ExecutorFunction;
import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.orbisgis.progress.ProgressMonitor;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;

public class CustomQueriesTest extends TestBase {

        @Test
        public void testCustomQuery() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("select * from sumquery(ds, 'gid');");

                d.open();
                d.close();
        }

        @Test
        public void testFilterCustom() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("select * from sumquery(select * from ds where gid = 3, 'gid');");

                d.open();
                assertEquals(d.getInt(0, "sum"), 3);
                assertEquals(d.getRowCount(), 1);
                d.close();
        }

        @Test
        public void testFieldTypesAndValues() throws Exception {
                dsf.getSourceManager().register("ds2", new File(TestResourceHandler.TESTRESOURCES,
                        "multilinestring2d.shp"));
                dsf.getDataSourceFromSQL("select * from fieldReferenceQuery(ds, ds2, 15, 'the_geom');");

                try {
                        dsf.getDataSourceFromSQL("select * from fieldReferenceQuery(ds, ds2, 'the_geom',"
                                + " 'the_geom');");
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        @Test
        public void testFieldReferences() throws Exception {
                dsf.getSourceManager().register("ds2", new File(TestResourceHandler.TESTRESOURCES,
                        "multilinestring2d.shp"));
                dsf.getDataSourceFromSQL("select * from fieldReferenceQuery(ds, ds2, 'gid');");
                dsf.getDataSourceFromSQL("select * from fieldReferenceQuery(ds, ds2, 'gid',"
                        + " 'the_geom');");

                try {
                        dsf.getDataSourceFromSQL("select * from fieldReferenceQuery(ds, ds2, ggidd);");
                        fail();
                } catch (Exception e) {
                }

        }

        @Test
        public void testRegister() throws Exception {
                dsf.getSourceManager().remove("ds");
                String path = TestResourceHandler.TESTRESOURCES.getAbsolutePath() + "/points.shp";
                dsf.executeSQL("CALL register ('" + path + "', 'myshape');");
                DataSource ret = dsf.getDataSource("myshape");
                assertTrue(ret != null);

//		Class.forName("org.h2.Driver");
//		Connection c = DriverManager.getConnection("jdbc:h2:"
//				+ SQLSourceTest.backupDir + File.separator + "h2db", "sa", "");
//		Statement st = c.createStatement();
//		st.execute("DROP TABLE h2table IF EXISTS");
//		st.execute("CREATE TABLE h2table "
//				+ "(id IDENTITY PRIMARY KEY, nom VARCHAR(10))");
//		st.close();
//		c.close();
//
//		dsf.executeSQL("select register "
//				+ "('h2', '127.0.0.1', '0', 'h2db', '', '', "
//				+ "'h2table', 'myh2table');");
//		ret = dsf.getDataSource("myh2table");
//		assertTrue(ret != null);

                // dsf.executeSQL("select register ('memory')");
                dsf.executeSQL("create table memory as select * from myshape;");
                DataSource ds1 = dsf.getDataSource("myshape");
                DataSource ds2 = dsf.getDataSource("memory");
                ds1.open();
                ds2.open();
//                System.out.println("'" + ds2.getAsString() + "'");
                assertEquals(ds1.getAsString(), ds2.getAsString());
                ds1.close();
                ds2.close();
        }

        @Test
        public void testRegisterValidation() throws Exception {
                // from clause
                String path = TestResourceHandler.TESTRESOURCES + "points.shp";
                executeSuccess("CALL register('" + path + "', 'name');");
                executeFail("CALL register('toto', '') from ds;");

                // // parameters
                executeFail("CALL register();");
                path = TestResourceHandler.TESTRESOURCES + "toto.shp";
                executeFail("CALL register('" + path + "');");
                dsf.getSourceManager().remove("toto");
                executeSuccess("CALL register('" + path + "', 'file');");
                executeSuccess("CALL register('postgresql', 'file', '23' , 'file', 'as', 'file', 'as', 'file2');");
                executeFail("CALL register('as', 'file', 'as');");
                executeFail("CALL register('as', 'file', 'as', 'as2');");
                executeFail("CALL register('as', 'file', 'as', 'as', 'as3');");
        }

        private void executeFail(String string) throws Exception {
                try {
                        dsf.executeSQL(string);
                        fail();
                } catch (Exception e) {
                }
        }

        private void executeSuccess(String string) throws Exception {
                dsf.executeSQL(string);
        }

        @Test
        public void testFunctionQueryCollission() throws Exception {
                final ST_Buffer buffer = new ST_Buffer();
                final RegisterCall register = new RegisterCall();
                try {
                        dsf.getFunctionManager().addFunction(new ExecutorFunction() {

                                @Override
                                public String getName() {
                                        return buffer.getName();
                                }

                                @Override
                                @Test
                                public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] args, ProgressMonitor pm) throws FunctionException {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public String getDescription() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public FunctionSignature[] getFunctionSignatures() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public String getSqlOrder() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public boolean isScalar() {
                                        return false;
                                }

                                @Override
                                public boolean isTable() {
                                        return false;
                                }

                                @Override
                                public boolean isAggregate() {
                                        return false;
                                }

                                @Override
                                public boolean isExecutor() {
                                        return true;
                                }
                        }.getClass());
                        fail();
                } catch (Exception e) {
                }
                try {
                        dsf.getFunctionManager().addFunction(new ScalarFunction() {

                                @Override
                                public String getName() {
                                        return register.getName();
                                }

                                @Override
                                public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public Type getType(Type[] argsTypes) {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public String getDescription() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public FunctionSignature[] getFunctionSignatures() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public String getSqlOrder() {
                                        throw new UnsupportedOperationException();
                                }

                                @Override
                                public boolean isScalar() {
                                        return false;
                                }

                                @Override
                                public boolean isTable() {
                                        return false;
                                }

                                @Override
                                public boolean isAggregate() {
                                        return false;
                                }

                                @Override
                                public boolean isExecutor() {
                                        return true;
                                }
                        }.getClass());
                        fail();
                } catch (Exception e) {
                }
        }

        @Test
        public void testGigaQuery() throws Exception {
                String sql = "select * from gigaquery();";
                DataSource ds = dsf.getDataSourceFromSQL(sql, DataSourceFactory.NORMAL);
                ds.open();
                ds.close();
                dsf.executeSQL("create table toto as select * from gigaquery();");
                ds = dsf.getDataSource("toto", DataSourceFactory.NORMAL);
                ds.open();
                ds.close();
        }

        @Test
        public void testFunctionRegister() throws Exception {
                dsf.executeSQL("CREATE FUNCTION sumquery2 AS 'org.gdms.sql.strategies.SumQuery2' LANGUAGE 'java'; ");
                DataSource d = dsf.getDataSourceFromSQL("select * from sumquery2(ds, 'gid');");
                d.open();
                d.close();
        }

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
                dsf.getSourceManager().register("ds", getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp")));
                dsf.getFunctionManager().addFunction(SumQuery.class);
                dsf.getFunctionManager().addFunction(FieldReferenceQuery.class);
                dsf.getFunctionManager().addFunction(GigaCustomQuery.class);
        }
}
