package org.gdms;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;

public class SQLTestData extends TestData {

	public SQLTestData(String name, boolean write, int driver, long rowCount,
			boolean isDB, String noPKField, boolean hasRepeatedRows) {
		super(name, write, driver, rowCount, isDB, noPKField, hasRepeatedRows);
	}

	@Override
	public String backup(DataSourceFactory dsf)
			throws Exception {
		dsf.registerDataSource("test", new FileSourceDefinition(new File(
				SourceTest.internalData + "test.csv")));
		DataSource ret = dsf.executeSQL("select * from test");

		return ret.getName();
	}

}
