package org.gdms.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.source.SourceManager;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.strategies.algebraic.LogicPlanner;
import org.gdms.sql.strategies.algebraic.Operator;
import org.gdms.sql.strategies.algebraic.preprocessor.Preprocessor;

public class ProcessorTest extends TestCase {

	private String dbFile;
	private static DataSourceFactory dsf;

	@Override
	public void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir(SourceTest.internalData + "/backup");
		SourceManager sm = dsf.getSourceManager();
		sm.register("alltypes", new File("src/test/resources/alltypes.dbf"));
		sm.register("gis", new File("src/test/resources/test.csv"));
		dbFile = "src/test/resources/backup/processortest";
		sm.register("hsqldb", new DBSource(null, 0, dbFile, "", "", "alltypes",
				"jdbc:h2"));
		if (!new File(dbFile + ".data.db").exists()) {
			dsf.executeSQL("create table hsqldb as select * from alltypes;");
		}
	}

	public void testProjectionAlias() throws Exception {
		compare("select FDECIMAL as \"decimal\", "
				+ "FSTR as \"mystr\" from \"alltypes\";",
				"select FDECIMAL as decimal, " + "FSTR as mystr from alltypes;");
	}

	public void testProjectionExpression() throws Exception {
		compare("select FDECIMAL+FDECIMAL as \"decimal\", "
				+ "FSTR as \"mystr\" from \"alltypes\";",
				"select FDECIMAL+FDECIMAL as decimal, "
						+ "FSTR as mystr from alltypes;");
	}

	public void testQualifiedFieldReferences() throws Exception {
		DataSource ds = executeSQL("select t1.id as id1 , t2.id as id2 "
				+ "from gis t1, gis t2;");
		ds.open();
		System.out.println(ds.getAsString());
		boolean allEquals = true;
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (!ds.getString(i, "id1").equals(ds.getString(i, "id2"))) {
				allEquals = false;
			}
		}
		assertTrue(!allEquals);
		ds.cancel();
	}

	public void testAmbiguousName() throws Exception {
		try {
			executeSQL("select FDECIMAL " + " from alltypes t1, alltypes t2;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
		executeSQL("select t1.FDECIMAL " + " from alltypes t1, alltypes t2;");
		executeSQL("select t2.FDECIMAL " + " from alltypes t1, alltypes t2;");
	}

	public void testBooleanExpressionsInSelect() throws Exception {
		executeSQL("select FDECIMAL=FDECIMAL from alltypes;");
	}

	private void test(String sql) throws Exception {
		compare(sql, sql);
	}

	private void compare(String nativeSQL, String gdmsSQL) throws Exception {
		Class.forName("org.h2.Driver").newInstance();
		Connection c = DriverManager.getConnection("jdbc:h2:file:" + dbFile,
				null, null);
		Statement st = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet res = st.executeQuery(nativeSQL);
		DataSource ds = executeSQL(gdmsSQL);
		compare(res, ds);
		res.close();
		st.close();
		c.close();
	}

	private DataSource executeSQL(String sql) throws Exception {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));

		parser.SQLStatement();
		LogicPlanner lp = new LogicPlanner(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());

		Preprocessor p = new Preprocessor(op);
		p.validateTableReferences();
		p.resolveFieldAndTableReferences();

		return op.getDataSource();
	}

	private void compare(ResultSet res, DataSource ds) throws Exception {
		ds.open();
		ResultSetMetaData md1 = res.getMetaData();
		Metadata md2 = ds.getMetadata();
		assertTrue(md1.getColumnCount() == md2.getFieldCount());
		for (int i = 0; i < md1.getColumnCount(); i++) {
			assertTrue(md1.getColumnName(i + 1).equals(md2.getFieldName(i)));
		}
		res.last();
		assertTrue(ds.getRowCount() == res.getRow());
		for (int i = 0; i < ds.getRowCount(); i++) {
			for (int j = 0; j < md1.getColumnCount(); j++) {
				String v1 = ds.getFieldValue(i, j).toString();
				res.absolute(i + 1);
				String v2 = res.getString(j + 1);
				assertTrue(v1.trim().equals(v2.trim()));
			}
		}
		ds.cancel();
	}
}
