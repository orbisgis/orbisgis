package org.gdms.newFunctionalities;

import junit.framework.TestCase;

/**
 * Shows how the DataSources can be accessed from the "from" clause in a SQL.
 */
public class DataSourceReferencesInSQLTest extends TestCase {
//	private DataSourceFactory dsf = new DataSourceFactory();
//
//	/**
//	 * The user retrieves a file DataSource without associating it with a
//	 * String.
//	 *
//	 * @throws Exception
//	 */
//	public void testSQLUponAlreadyRetrievedFileDataSource() throws Exception {
//		DataSource ds = dsf.getDataSource(new File("test.shp"));
//		getSecondaryDataSource(ds);
//	}
//
//	/**
//	 * The user retrieves a data base DataSource without associating it with a
//	 * String.
//	 *
//	 * @throws Exception
//	 */
//	public void testSQLUponAlreadyRetrievedDBDataSource() throws Exception {
//		DataSource ds = dsf.getDataSource(new DBSource("127.0.0.1", "user",
//				"password", "tableName"));
//		getSecondaryDataSource(ds);
//	}
//
//	/**
//	 * Executes a query upon an already retrieved DataSource. It doesn't matter
//	 * where the DataSource accesses (file, data base, ...)
//	 *
//	 * @param ds
//	 * @throws Exception
//	 */
//	private void getSecondaryDataSource(DataSource ds) throws Exception {
//		String sql = "SELECT * FROM " + ds.getName() + ";";
//		checkSQLExecution(sql);
//	}
//
//	/**
//	 * Executes a sql and checks that all the data in the DataSource is
//	 * accessible, calling the convenience method
//	 * ExtendedDataSource.getAsString()
//	 *
//	 * @see SecondarySpatialDataSourceTest
//	 *
//	 * @param sql
//	 * @throws NoSuchTableException
//	 * @throws ExecutionException
//	 */
//	private void checkSQLExecution(String sql) throws Exception {
//		SpatialDataSource secondaryDS = (SpatialDataSource) dsf.executeSQL(sql);
//		secondaryDS.start();
//		secondaryDS.getAsString();
//		secondaryDS.stop();
//	}
//
//	/**
//	 * Test the direct data source references in a SQL
//	 *
//	 * @throws Exception
//	 */
//	public void testFileDataSourceReferenceInSQL() throws Exception {
//		String sql = "SELECT * FROM file('test.shp');";
//		checkSQLExecution(sql);
//	}
//
//	/**
//	 * Test the direct data source references in a SQL
//	 *
//	 * @throws Exception
//	 */
//	public void testDBDataSourceReferenceInSQL() throws Exception {
//		String sql = "SELECT * FROM db('127.0.0.1', 'user', 'password', 'tableName');";
//		checkSQLExecution(sql);
//	}
//
//	/**
//	 * Test the aliases in field references and table references.
//	 *
//	 * @throws Exception
//	 */
//	public void testFieldAndTableAlias() throws Exception {
//		String sql = "SELECT tableInLocal.the_geom AS g FROM"
//				+ " db('127.0.0.1', 'user', 'password', 'tableName') AS tableInLocal;";
//		DataSource secondaryDS = dsf.executeSQL(sql);
//		secondaryDS.start();
//		assertTrue(secondaryDS.getDataSourceMetadata().getFieldName(0).equals(
//				"g"));
//		secondaryDS.stop();
//	}
}
