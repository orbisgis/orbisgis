package org.gdms.drivers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class DriversTest extends SourceTest {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public void testReadAndWriteHSQLDB() throws Exception {
		createTable("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:",
				"hsqldbSample", new String[] { "INTEGER PRIMARY KEY", "DOUBLE",
						"VARCHAR", "VARCHAR_IGNORECASE", "CHAR", "LONGVARCHAR",
						"DATE", "TIME", "TIMESTAMP", "DECIMAL", "NUMERIC",
						"BOOLEAN", "TINYINT", "SMALLINT", "BIGINT", "REAL",
						"BINARY", "VARBINARY", "LONGVARBINARY", "OTHER" });

		DBSource source = new DBSource(null, 0, SourceTest.backupDir
				+ File.separator + "hsqldbSample", null, null, "alltypes",
				"jdbc:hsqldb:file");

		SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

		DataSource ds = dsf.getDataSource(source);
		ds.open();
		ds.insertFilledRow(new Value[] {
				ValueFactory.createValue(1),
				ValueFactory.createValue(2.04d),
				ValueFactory.createValue("r"),
				ValueFactory.createValue("r"),
				ValueFactory.createValue("c"),
				ValueFactory.createValue("longvarchar"),
				ValueFactory.createValue(sdf.parse("1980-7-23")),
				ValueFactory.createValue(new Time(stf.parse("15:34:40")
						.getTime())),
				ValueFactory.createValue(Timestamp
						.valueOf("1980-07-23 15:34:40.236472388")),
				ValueFactory.createValue((byte) 0),
				ValueFactory.createValue((short) 4),
				ValueFactory.createValue(true),
				ValueFactory.createValue(4),
				ValueFactory.createValue(5),
				ValueFactory.createValue(32466234),
				ValueFactory.createValue(345343.3453),
				ValueFactory.createValue(new byte[] { (byte) 3, (byte) 5,
						(byte) 6 }),
				ValueFactory.createValue(new byte[] { (byte) 3, (byte) 5,
						(byte) 6 }),
				ValueFactory.createValue(new byte[] { (byte) 3, (byte) 5,
						(byte) 6 }), ValueFactory.createNullValue() });
		ds.commit();
		ds.open();
		String content = ds.getAsString();
		ds.commit();
		ds.open();
		assertTrue(content.equals(ds.getAsString()));
		ds.commit();
	}

	private void createTable(String jdbcDriverName, String urlPrefix,
			String fileName, String[] types) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(jdbcDriverName).newInstance();

		Connection c = DriverManager.getConnection(urlPrefix
				+ SourceTest.backupDir + File.separator + fileName, null, "");

		Statement st = c.createStatement();
		st.execute("DROP TABLE \"alltypes\" IF EXISTS");
		String sql = "CREATE CACHED TABLE \"alltypes\" (";
		for (int i = 0; i < types.length; i++) {
			sql = sql + "\"field" + types[i] + "\" " + types[i] + ", ";
		}
		sql = sql.substring(0, sql.length() - 2);
		st.execute(sql + ");");
		st.close();
		c.close();
	}

	public void testReadAndWriteH2() throws Exception {
		createTable("org.h2.Driver", "jdbc:h2:", "h2Sample", new String[] {
				"INTEGER", "BOOLEAN", "TINYINT", "SMALLINT", "BIGINT",
				"IDENTITY", "DECIMAL", "DOUBLE", "REAL", "TIME", "DATE",
				"TIMESTAMP", "BINARY", "VARCHAR", "VARCHAR_IGNORECASE", "CHAR",
				"CLOB", "BLOB", "UUID" });

		DBSource source = new DBSource(null, 0, SourceTest.backupDir
				+ File.separator + "h2Sample", null, null, "alltypes",
				"jdbc:h2");

		SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

		DataSource ds = dsf.getDataSource(source);
		ds.open();
		ds.insertFilledRow(new Value[] {
				ValueFactory.createValue(1),
				ValueFactory.createValue(true),
				ValueFactory.createValue((byte) 0),
				ValueFactory.createValue((short) 4),
				ValueFactory.createValue(32466234),
				ValueFactory.createValue(4),
				ValueFactory.createValue(345343.3453),
				ValueFactory.createValue(2.04d),
				ValueFactory.createValue(2.04f),
				ValueFactory.createValue(new Time(stf.parse("15:34:40")
						.getTime())),
				ValueFactory.createValue(sdf.parse("1980-7-23")),
				ValueFactory.createValue(Timestamp
						.valueOf("1980-07-23 15:34:40.236472388")),
				ValueFactory.createValue(new byte[] { (byte) 3, (byte) 5,
						(byte) 6 }),
				ValueFactory.createValue("rdsgvfsag"),
				ValueFactory.createValue("SDGHdsigs"),
				ValueFactory.createValue("c"),
				ValueFactory.createValue("CLOBVALUE"),
				ValueFactory.createValue(new byte[] { (byte) 3, (byte) 5,
						(byte) 6 }), ValueFactory.createValue("UUIDVALUE") });
		ds.commit();
		ds.open();
		String content = ds.getAsString();
		ds.commit();
		ds.open();
		assertTrue(content.equals(ds.getAsString()));
		ds.commit();
	}

}
