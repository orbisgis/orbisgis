package org.gdms.data.edition;

import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

public class RemoveFieldInfo implements EditionInfo {

	private String fieldName;

	public RemoveFieldInfo(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		return driver.getDeleteFieldSQL(tableName, fieldName);
	}

}
