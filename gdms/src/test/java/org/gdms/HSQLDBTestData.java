package org.gdms;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;

public class HSQLDBTestData extends TestData {

	private DBSource dbInfo;

	public HSQLDBTestData(String name, long rowCount, boolean isDB,
			String noPKField, boolean hasRepeatedRows, DBSource source) {
		super(name, true, TestData.HSQLDB, rowCount, isDB, noPKField,
				hasRepeatedRows);
		this.dbInfo = source;
	}

	@Override
	public String backup(DataSourceFactory dsf) throws Exception {
		File dbFile = new File(dbInfo.getDbName());
		copyGroup(dbFile, SourceTest.backupDir);

		DBSource dbs = new DBSource(dbInfo.getHost(), dbInfo.getPort(),
				new File(SourceTest.backupDir, dbFile.getName())
						.getAbsolutePath(), dbInfo.getUser(), dbInfo
						.getPassword(), dbInfo.getTableName(), dbInfo
						.getPrefix());
		DBTableSourceDefinition backupDef = new DBTableSourceDefinition(dbs);
		String backupName = name + "backup";
		dsf.registerDataSource(backupName, backupDef);

		return backupName;
	}
}