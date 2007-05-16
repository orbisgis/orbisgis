package org.gdms.data.edition;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ObjectDriver;

import com.hardcode.driverManager.DriverManager;

public class IsEditableTests extends BaseTest {

	private DataSourceFactory dsf;

	public void testObject() throws Exception {
		DataSource ds = dsf.getDataSource("readObject");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteObject");
		assertFalse(ds.isEditable());
		ReadWriteDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	public void testFile() throws Exception {
		DataSource ds = dsf.getDataSource("readFile");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteFile");
		assertFalse(ds.isEditable());
		ReadWriteDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	public void testObject() throws Exception {
		DataSource ds = dsf.getDataSource("readDB");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteDB");
		assertFalse(ds.isEditable());
		ReadWriteDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	@Override
	protected void setUp() throws Exception {
		ReadWriteDriver.initialize();

		dsf = new DataSourceFactory();
		DriverManager dm = new DriverManager();
		dm.registerDriver("readwritedriver", ReadWriteDriver.class);
		dsf.setDriverManager(dm);

		dsf.registerDataSource("readObject", new ObjectSourceDefinition(
				new ReadDriver()));
		dsf.registerDataSource("readWriteObject", new ObjectSourceDefinition(
				new ReadWriteDriver()));
		dsf.registerDataSource("readFile", new FakeFileSourceDefinition(
				new ReadDriver()));
		dsf.registerDataSource("readWriteFile", new FakeFileSourceDefinition(
				new ReadWriteDriver()));
		dsf.registerDataSource("readDB",
				new FakeDBTableSourceDefinition(new ReadDriver(),
						"jdbc:executefailing"));
		dsf.registerDataSource("readWriteDB", new FakeDBTableSourceDefinition(
				new ReadWriteDriver(), "jdbc:closefailing"));
	}
}
