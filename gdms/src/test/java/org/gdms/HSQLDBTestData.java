package org.gdms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DriverUtilities;

public class HSQLDBTestData extends TestData {

	private DBTableSourceDefinition def;

	public HSQLDBTestData(String name, long rowCount, boolean isDB,
			String noPKField, boolean hasRepeatedRows,
			DBTableSourceDefinition def) {
		super(name, true, TestData.HSQLDB, rowCount, isDB, noPKField, hasRepeatedRows, def);
		this.def = def;
	}

	@Override
	public String backup(File backupDir, DataSourceFactory dsf)
			throws Exception {
		DBSource dbInfo = def.getSourceDefinition();
		File dbFile = new File(dbInfo.getDbName());
		copyGroup(dbFile, backupDir);

		DBSource dbs = new DBSource(dbInfo.getHost(), dbInfo.getPort(),
				new File(backupDir, dbFile.getName()).getAbsolutePath(), dbInfo
						.getUser(), dbInfo.getPassword(),
				dbInfo.getTableName(), dbInfo.getPrefix());
		DBTableSourceDefinition backupDef = new DBTableSourceDefinition(dbs);
		return dsf.nameAndRegisterDataSource(backupDef);
	}

	public void copyGroup(final File prefix, File dir) throws IOException {
		File[] dbFiles = prefix.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(prefix.getName());
			}
		});

		for (int i = 0; i < dbFiles.length; i++) {
			DriverUtilities.copy(dbFiles[i], new File(dir, dbFiles[i].getName()));
		}
	}
}