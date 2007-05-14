package org.gdms;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverUtilities;

public class FileTestData extends TestData {

	private FileSourceDefinition def;

	public FileTestData(String name, boolean write, int driver, long rowCount,
			boolean isDB, String noPKField, boolean hasRepeatedRows,
			FileSourceDefinition def) {
		super(name, write, driver, rowCount, isDB, noPKField, hasRepeatedRows,
				def);
		this.def = def;
	}

	@Override
	public String backup(File backupDir, DataSourceFactory dsf)
			throws Exception {
		File backupFile = new File(backupDir, def.file.getName());
		DriverUtilities.copy(def.file, backupFile);
		if (def.file.getName().toLowerCase().trim().endsWith(".shp")) {
			String prefix = def.file.getAbsolutePath();
			prefix = prefix.substring(0, prefix.length() - 4);

			File dbf = new File(prefix + ".dbf");
			File backup = new File(backupDir, dbf.getName());
			DriverUtilities.copy(dbf, backup);
			File shx = new File(prefix + ".shx");
			backup = new File(backupDir, shx.getName());
			DriverUtilities.copy(shx, backup);
		}

		FileSourceDefinition backupDef = new FileSourceDefinition(backupFile);
		return dsf.nameAndRegisterDataSource(backupDef);

	}

}
