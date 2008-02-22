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

	public void testScapeSemiColon() throws Exception {
		File file = new File("src/test/resources/backup/csvdrivertest.csv");
		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException("Cannot delete file " + file);
			}
		}

		DataSourceFactory dsf = new DataSourceFactory();
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.STRING);
		metadata.addField("f2", Type.STRING);
		FileSourceCreation fsc = new FileSourceCreation(file, metadata);
		dsf.createDataSource(fsc);

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
}
