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
package org.gdms.sql.strategies;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.source.Source;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.RegisterCall;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.geometry.operators.Buffer;
import org.gdms.sql.parser.ParseException;

public class CustomQueriesTest extends TestCase {

	private DataSourceFactory dsf;

	static {
		QueryManager.registerQuery(SumQuery.class);
		QueryManager.registerQuery(FieldReferenceQuery.class);
		QueryManager.registerQuery(GigaCustomQuery.class);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws DriverLoadException
	 *             DOCUMENT ME!
	 * @throws ParseException
	 *             DOCUMENT ME!
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws SemanticException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void testCustomQuery() throws Exception {
		DataSource d = dsf
				.getDataSourceFromSQL("select sumquery('gid') from ds;");

		d.open();
		d.close();
	}

	public void testFilterCustom() throws Exception {
		DataSource d = dsf
				.getDataSourceFromSQL("select sumquery('gid') from ds where gid=3;");

		d.open();
		assertTrue(d.getInt(0, "sum") == 3);
		assertTrue(d.getRowCount() == 1);
		d.close();
	}

	public void testFieldTypesAndValues() throws Exception {
		dsf.getSourceManager().register(
				"ds2",
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "multilinestring2d.shp")));
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds.gid,"
				+ " t2.the_geom) from ds, ds2 t2;");

		try {
			dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds2.the_geom,"
					+ " t2.the_geom) from ds, ds2 t2;");
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testFieldReferences() throws Exception {
		dsf.getSourceManager().register(
				"ds2",
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "multilinestring2d.shp")));
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds.gid) "
				+ "from ds, ds2;");
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds.gid,"
				+ " t2.the_geom) from ds, ds2 t2;");

		try {
			dsf.getDataSourceFromSQL("select fieldReferenceQuery(ggidd)"
					+ " from ds, ds2;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	public void testRegister() throws Exception {
		dsf.getSourceManager().remove("ds");
		String path = SourceTest.internalData
				+ "points.shp";
		dsf.executeSQL("select register ('" + path + "', 'myshape');");
		DataSource ret = dsf.getDataSource("myshape");
		assertTrue(ret != null);

		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection("jdbc:h2:"
				+ SourceTest.backupDir + File.separator + "h2db", "sa", "");
		Statement st = c.createStatement();
		st.execute("DROP TABLE h2table IF EXISTS");
		st.execute("CREATE TABLE h2table "
				+ "(id IDENTITY PRIMARY KEY, nom VARCHAR(10))");
		st.close();
		c.close();

		dsf.executeSQL("select register "
				+ "('h2', '127.0.0.1', '0', 'h2db', '', '', "
				+ "'h2table', 'myh2table');");
		ret = dsf.getDataSource("myh2table");
		assertTrue(ret != null);

		dsf.executeSQL("select register ('memory')");
		dsf.executeSQL("create table memory as select * from myshape");
		DataSource ds1 = dsf.getDataSource("myshape");
		DataSource ds2 = dsf.getDataSource("memory");
		ds1.open();
		ds2.open();
		assertTrue(ds1.getAsString().equals(ds2.getAsString()));
		ds1.close();
		ds2.close();
	}

	public void testRegisterValidation() throws Exception {
		// from clause
		executeSuccess("select register('', '');");
		executeFail("select register('', '') from ds;");

		// // parameters
		executeFail("select register();");
		executeSuccess("select register('a');");
		executeSuccess("select register('as', 'file');");
		executeSuccess("select register('as', 'file', '23' , 'file', 'as', 'file', 'as', 'file2');");
		executeFail("select register('as', 'file', 'as');");
		executeFail("select register('as', 'file', 'as', 'as2');");
		executeFail("select register('as', 'file', 'as', 'as', 'as3');");
	}

	private void executeFail(String string) throws Exception {
		try {
			dsf.executeSQL(string);
			assertTrue(false);
		} catch (SemanticException e) {

		} catch (IncompatibleTypesException e) {

		}
	}

	private void executeSuccess(String string) throws Exception {
		dsf.executeSQL(string);
	}

	public void testRegisterDefaultSource() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		File resultDir = new File("src/test/resources/backup");
		dsf.setResultDir(resultDir);
		dsf.executeSQL("select register('toto')");
		Source src = dsf.getSourceManager().getSource("toto");
		assertTrue(src.getDriverId().equals(new GdmsDriver().getDriverId()));
		assertTrue(src.getFile().getParentFile().equals(resultDir));
	}

	public void testFunctionQueryCollission() throws Exception {
		final Buffer buffer = new Buffer();
		final RegisterCall register = new RegisterCall();
		try {
			QueryManager.registerQuery(new RegisterCall() {

				@Override
				public String getName() {
					return buffer.getName();
				}

			}.getClass());
			assertTrue(false);
		} catch (Exception e) {
		}
		try {
			FunctionManager.addFunction(new Buffer() {

				@Override
				public String getName() {
					return register.getName();
				}

			}.getClass());
			assertTrue(false);
		} catch (Exception e) {
		}
	}

	public void testGigaQuery() throws Exception {
		String sql = "select gigaquery();";
		DataSource ds = dsf.getDataSourceFromSQL(sql, DataSourceFactory.NORMAL);
		ds.open();
		ds.close();
		ds = null;

		dsf.executeSQL("create table toto as select gigaquery();");
		ds = dsf.getDataSource("toto", DataSourceFactory.NORMAL);
		ds.open();
		ds.close();
		ds = null;
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory("src/test/resources/backup",
				"src/test/resources/backup");
		dsf.getSourceManager().register(
				"ds",
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "multipoints.shp")));
	}

}
