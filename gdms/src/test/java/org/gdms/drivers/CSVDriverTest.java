package org.gdms.drivers;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

public class CSVDriverTest extends TestCase {

	private File file;
	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		File file1 = new File("src/test/resources/backup/csvdrivertest.csv");
		if (file1.exists()) {
			if (!file1.delete()) {
				throw new IOException("Cannot delete file " + file1);
			}
		}
		file = file1;

		dsf = new DataSourceFactory();
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.STRING);
		metadata.addField("f2", Type.STRING);
		FileSourceCreation fsc = new FileSourceCreation(file, metadata);
		dsf.createDataSource(fsc);
	}

	public void testScapeSemiColon() throws Exception {
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createValue("a;b"));
		ds.setFieldValue(0, 1, ValueFactory.createValue("c\\d"));
		ds.commit();

		ds.open();
		assertTrue(ds.getString(0, 0).equals("a;b"));
		assertTrue(ds.getString(0, 1).equals("c\\d"));
		ds.cancel();
	}

	public void testNullValues() throws Exception {
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createNullValue());
		ds.setFieldValue(0, 1, ValueFactory.createNullValue());
		ds.commit();

		ds.open();
		assertTrue(ds.isNull(0, 0));
		assertTrue(ds.isNull(0, 1));
		ds.cancel();
	}
}
