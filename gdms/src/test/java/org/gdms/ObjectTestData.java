package org.gdms;

import java.io.File;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;

public class ObjectTestData extends TestData {

	private DataSourceDefinition def;

	public ObjectTestData(String name, boolean write, int driver,
			long rowCount, String noPKField, boolean hasRepeatedRows,
			DataSourceDefinition def) {
		super(name, write, driver, rowCount, false, noPKField, hasRepeatedRows,
				def);
		this.def = def;
	}

	@Override
	public String backup(File backupDir, DataSourceFactory dsf)
			throws Exception {
		return dsf.nameAndRegisterDataSource(def);
	}

}
