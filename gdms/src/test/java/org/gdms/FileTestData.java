package org.gdms;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverUtilities;

public class FileTestData extends TestData {

	private File file;

	public FileTestData(String name, boolean write, int driver, long rowCount,
			boolean isDB, String noPKField, boolean hasRepeatedRows, File file) {
		super(name, write, driver, rowCount, isDB, noPKField, hasRepeatedRows);
		this.file = file;
	}

	@Override
	public String backup(DataSourceFactory dsf) throws Exception {
		File dest = new File(SourceTest.backupDir.getAbsolutePath() + "/"
				+ name);
		dest.mkdirs();
		File backupFile = new File(dest, file.getName());
		DriverUtilities.copy(file, backupFile);
		if (file.getName().toLowerCase().trim().endsWith(".shp")) {
			String prefix = file.getAbsolutePath();
			prefix = prefix.substring(0, prefix.length() - 4);
			copyGroup(new File(prefix), dest);
		}

		FileSourceDefinition backupDef = new FileSourceDefinition(backupFile);
		String backupName = name + "backup" + System.currentTimeMillis();
		dsf.registerDataSource(backupName, backupDef);

		return backupName;
	}

}
