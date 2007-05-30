package org.gdms;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.file.FileSourceDefinition;

public class SQLTestData extends TestData {


	public SQLTestData(String name, boolean write, int driver,
			long rowCount, boolean isDB, String noPKField,
			boolean hasRepeatedRows) {
		super(name, write, driver, rowCount, isDB, noPKField, hasRepeatedRows,
				new DataSourceDefinition() {
					public void setDataSourceFactory(DataSourceFactory dsf) {
					}

					public void freeResources(String name)
							throws DataSourceFinalizationException {
					}

					public DataSource createDataSource(String tableName,
							String tableAlias, String driverName)
							throws DataSourceCreationException {
						return null;
					}
				});
	}

	@Override
	public String backup(File backupDir, DataSourceFactory dsf)
			throws Exception {
		dsf.registerDataSource("test", new FileSourceDefinition(new File(
				SourceTest.internalData + "test.csv")));
		DataSource ret = dsf.executeSQL("select * from test");

		return ret.getName();
	}

}
