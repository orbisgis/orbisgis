package org.gdms.data.edition;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.object.ObjectSourceDefinition;

import com.hardcode.driverManager.DriverManager;

public class IsEditableTests extends BaseTest {

	private DataSourceFactory dsf;

	public void testObject() throws Exception {
		DataSource ds = dsf.getDataSource("readObject");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteObject");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	public void testFile() throws Exception {
		DataSource ds = dsf.getDataSource("readFile");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteFile");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	public void testDB() throws Exception {
		DataSource ds = dsf.getDataSource("readDB");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteDB");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	@Override
	protected void setUp() throws Exception {
		ReadDriver.initialize();

		dsf = new DataSourceFactory();
		DriverManager dm = new DriverManager();
		dm.registerDriver("readwritedriver", ReadDriver.class);
		dsf.setDriverManager(dm);

		dsf.registerDataSource("readObject", new ObjectSourceDefinition(
				new ReadDriver()));
		dsf.registerDataSource("readWriteObject", new ObjectSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("readFile", new FakeFileSourceDefinition(
				new ReadDriver()));
		dsf.registerDataSource("readWriteFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		dsf.registerDataSource("readDB",
				new FakeDBTableSourceDefinition(new ReadDriver(),
						"jdbc:executefailing"));
		dsf.registerDataSource("readWriteDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:closefailing"));
	}
}
