/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
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
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.RegisterCall;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.operators.Buffer;
import org.gdms.sql.parser.ParseException;

public class CustomQueriesTest extends TestCase {

	private DataSourceFactory dsf;

	static {
		QueryManager.registerQuery(new SumQuery());
		QueryManager.registerQuery(new FieldReferenceQuery());
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
		dsf.getSourceManager().register(
				"ds",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multipoint2d.shp")));
		DataSource d = dsf
				.getDataSourceFromSQL("select sumquery('gid') from ds;");

		d.open();
		d.cancel();
	}

	public void testFilterCustom() throws Exception {
		dsf.getSourceManager().register(
				"ds",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multipoint2d.shp")));
		DataSource d = dsf
				.getDataSourceFromSQL("select sumquery('gid') from ds where gid=3;");

		d.open();
		assertTrue(d.getInt(0, "gid") == 3);
		assertTrue(d.getRowCount() == 1);
		d.cancel();
	}

	public void testFieldTypesAndValues() throws Exception {
		dsf.getSourceManager().register(
				"ds",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multipoint2d.shp")));
		dsf.getSourceManager().register(
				"ds2",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multilinestring2d.shp")));
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds.gid,"
				+ " t2.the_geom) from ds, ds2 t2;");
		FieldReferenceQuery query = (FieldReferenceQuery) QueryManager
				.getQuery("fieldReferenceQuery");
		assertTrue(query.getValidateTypes()[0].getTypeCode() == Type.LONG);
		assertTrue(query.getValidateTypes()[1].getTypeCode() == Type.GEOMETRY);
		assertTrue(query.getEvaluateValues()[0].equals(
				ValueFactory.createValue("gid")).getAsBoolean());
		assertTrue(query.getEvaluateValues()[1].equals(
				ValueFactory.createValue("the_geom")).getAsBoolean());
	}

	public void testFieldReferences() throws Exception {
		dsf.getSourceManager().register(
				"ds",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multipoint2d.shp")));
		dsf.getSourceManager().register(
				"ds2",
				new FileSourceDefinition(new File(SourceTest.externalData
						+ "shp/smallshape2D/multilinestring2d.shp")));
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(gid) "
				+ "from ds;");
		dsf.getDataSourceFromSQL("select fieldReferenceQuery(ds.gid,"
				+ " t2.the_geom) from ds, ds2 t2;");

		try {
			dsf.getDataSourceFromSQL("select fieldReferenceQuery(ggidd)"
					+ " from ds;");
			assertTrue(false);
		} catch (DataSourceCreationException e) {
		}
	}

	public void testRegister() throws Exception {
		String path = SourceTest.externalData
				+ "shp/smallshape2D/multipoint2d.shp";
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
		ds1.cancel();
		ds2.cancel();
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

			});
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

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
	}

}
