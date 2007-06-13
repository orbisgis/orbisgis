package org.gdms.data.edition;

import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

public class ChangeFieldNameInfo extends BaseEditionInfo {

	private String oldName;

	private String newName;

	public ChangeFieldNameInfo(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		return driver.getChangeFieldNameStatement(tableName, oldName, newName);
	}
}
