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
package org.gdms.newFunctionalities;

import junit.framework.TestCase;

/**
 * Shows how the DataSources can be accessed from the "from" clause in a SQL.
 */
public class DataSourceReferencesInSQLTest extends TestCase {
	// private DataSourceFactory dsf = new DataSourceFactory();
	//
	// /**
	// * The user retrieves a file DataSource without associating it with a
	// * String.
	// *
	// * @throws Exception
	// */
	// public void testSQLUponAlreadyRetrievedFileDataSource() throws Exception
	// {
	// DataSource ds = dsf.getDataSource(new File("test.shp"));
	// getSecondaryDataSource(ds);
	// }
	//
	// /**
	// * The user retrieves a data base DataSource without associating it with a
	// * String.
	// *
	// * @throws Exception
	// */
	// public void testSQLUponAlreadyRetrievedDBDataSource() throws Exception {
	// DataSource ds = dsf.getDataSource(new DBSource("127.0.0.1", "user",
	// "password", "tableName"));
	// getSecondaryDataSource(ds);
	// }
	//
	// /**
	// * Executes a query upon an already retrieved DataSource. It doesn't
	// matter
	// * where the DataSource accesses (file, data base, ...)
	// *
	// * @param ds
	// * @throws Exception
	// */
	// private void getSecondaryDataSource(DataSource ds) throws Exception {
	// String sql = "SELECT * FROM " + ds.getName() + ";";
	// checkSQLExecution(sql);
	// }
	//
	// /**
	// * Executes a sql and checks that all the data in the DataSource is
	// * accessible, calling the convenience method
	// * ExtendedDataSource.getAsString()
	// *
	// * @see SecondarySpatialDataSourceTest
	// *
	// * @param sql
	// * @throws NoSuchTableException
	// * @throws ExecutionException
	// */
	// private void checkSQLExecution(String sql) throws Exception {
	// SpatialDataSource secondaryDS = (SpatialDataSource) dsf.executeSQL(sql);
	// secondaryDS.start();
	// secondaryDS.getAsString();
	// secondaryDS.stop();
	// }
	//
	// /**
	// * Test the direct data source references in a SQL
	// *
	// * @throws Exception
	// */
	// public void testFileDataSourceReferenceInSQL() throws Exception {
	// String sql = "SELECT * FROM file('test.shp');";
	// checkSQLExecution(sql);
	// }
	//
	// /**
	// * Test the direct data source references in a SQL
	// *
	// * @throws Exception
	// */
	// public void testDBDataSourceReferenceInSQL() throws Exception {
	// String sql = "SELECT * FROM db('127.0.0.1', 'user', 'password',
	// 'tableName');";
	// checkSQLExecution(sql);
	// }
	//
	// /**
	// * Test the aliases in field references and table references.
	// *
	// * @throws Exception
	// */
	// public void testFieldAndTableAlias() throws Exception {
	// String sql = "SELECT tableInLocal.the_geom AS g FROM"
	// + " db('127.0.0.1', 'user', 'password', 'tableName') AS tableInLocal;";
	// DataSource secondaryDS = dsf.executeSQL(sql);
	// secondaryDS.start();
	// assertTrue(secondaryDS.getDataSourceMetadata().getFieldName(0).equals(
	// "g"));
	// secondaryDS.stop();
	// }
}
