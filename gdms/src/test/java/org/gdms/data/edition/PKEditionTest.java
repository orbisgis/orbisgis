package org.gdms.data.edition;

import junit.framework.TestCase;

import org.gdms.DBTestSource;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;

public class PKEditionTest extends TestCase {

	public void testUpdatePK() throws Exception {
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "gisapps", "jdbc:postgresql");
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "postgresEditablePK.sql", dbSource);
		src.backup();
		DataSource d = SourceTest.dsf.getDataSource("source");

		d.open();
		d.setInt(0, "id", 7);
		d.setString(0, "gis", "gisberto");
		d.commit();

		d = d.getDataSourceFactory().executeSQL("select * from source where id = 7;");
		d.open();
		assertTrue(d.getRowCount() == 1);
		assertTrue(d.getInt(0, "id") == 7);
		assertTrue(d.getString(0, "gis").equals("gisberto"));
		d.cancel();
	}

	public void testDeleteUpdatedPK() throws Exception {
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "gisapps", "jdbc:postgresql");
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "postgresEditablePK.sql", dbSource);
		src.backup();
		DataSource d = SourceTest.dsf.getDataSource("source");

		d.open();
		d.setInt(2, "id", 9);
		d.deleteRow(2);
		d.commit();

		d = d.getDataSourceFactory().executeSQL("select * from source where id = 9;");
		d.open();
		assertTrue(0 == d.getRowCount());
		d.cancel();
	}

	@Override
	protected void setUp() throws Exception {
		SourceTest.dsf.removeAllDataSources();
	}


}