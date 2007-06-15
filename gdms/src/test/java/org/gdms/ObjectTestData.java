package org.gdms;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.ObjectDriver;

public class ObjectTestData extends TestData {

	private ObjectDriver driver;

	public ObjectTestData(String name, boolean write, int driver,
			long rowCount, String noPKField, boolean hasRepeatedRows,
			ObjectDriver def) {
		super(name, write, driver, rowCount, false, noPKField, hasRepeatedRows);
		this.driver = def;
	}

	@Override
	public String backup(DataSourceFactory dsf)
			throws Exception {
		return dsf.nameAndRegisterDataSource(new ObjectSourceDefinition(driver));
	}

}
