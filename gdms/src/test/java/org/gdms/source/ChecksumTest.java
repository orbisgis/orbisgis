package org.gdms.source;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.DBTestSource;
import org.gdms.FileTestSource;
import org.gdms.SQLTestSource;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;

public class ChecksumTest extends TestCase {

	private DataSourceFactory dsf;
	private SourceManager sm;

	public void testModifyingSourceOutsideFactory() throws Exception {
		File testFile = new File(SourceTest.internalData + "test.csv");
		String name = "file";
		FileTestSource fts = new FileTestSource(name, testFile
				.getAbsolutePath());
		fts.backup();
		sm.register(name, fts.getBackupFile());
		testModifyingSourceOutsideFactory(name, false);

		name = "db";
		DBSource testDB = new DBSource(null, 0, SourceTest.internalData
				+ "backup/testhsqldb", "sa", "", "gisapps", "jdbc:hsqldb:file");
		DBTestSource dbTestSource = new DBTestSource(name,
				"org.hsqldb.jdbcDriver", SourceTest.internalData
						+ "testhsqldb.sql", testDB);
		dbTestSource.backup();
		sm.register(name, testDB);
		testModifyingSourceOutsideFactory(name, false);

		name = "sql";
		String sql = "select count(id) from file;";
		SQLTestSource sts = new SQLTestSource(name, sql);
		sts.backup();
		sm.register(name, sql);
		testModifyingSourceOutsideFactory(name, true);

	}

	private synchronized void testModifyingSourceOutsideFactory(String name,
			boolean outdatedValue) throws Exception {
		assertTrue(sm.getSource(name).isUpToDate() == null);
		sm.saveStatus();
		assertTrue(sm.getSource(name).isUpToDate().booleanValue() == true);

		DataSource ds = SourceTest.dsf.getDataSource(name);
		ds.open();
		ds.deleteRow(0);
		if (outdatedValue) {
			ds.cancel();
		} else {
			// To change modification time
			wait(2000);
			ds.commit();
		}

		instantiateDSF();
		assertTrue(sm.getSource(name).isUpToDate().booleanValue() == outdatedValue);
	}

	public void testUpdateOnSave() throws Exception {
		File testFile = new File(SourceTest.internalData + "test.csv");
		String name = "file";
		FileTestSource fts = new FileTestSource(name, testFile
				.getAbsolutePath());
		fts.backup();
		sm.register(name, fts.getBackupFile());
		sm.saveStatus();

		modificationWithOtherFactory(fts.getBackupFile());

		instantiateDSF();
		assertTrue(sm.getSource(name).isUpToDate() == false);
		sm.saveStatus();
		instantiateDSF();
		assertTrue(sm.getSource(name).isUpToDate() == true);
	}

	private synchronized void modificationWithOtherFactory(File file)
			throws Exception {
		// Modification with another factory
		DataSource ds = SourceTest.dsf.getDataSource(file);
		ds.open();
		ds.deleteRow(0);
		wait(2000);
		ds.commit();
	}

	@Override
	protected void setUp() throws Exception {
		SourceTest.dsf.getSourceManager().removeAll();
		instantiateDSF();
		sm.removeAll();
	}

	private void instantiateDSF() {
		dsf = new DataSourceFactory(SourceTest.internalData
				+ "source-management");
		sm = dsf.getSourceManager();
	}
}
