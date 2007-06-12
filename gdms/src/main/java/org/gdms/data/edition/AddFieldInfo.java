package org.gdms.data.edition;

import org.gdms.data.types.Type;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

public class AddFieldInfo implements EditionInfo {

	private String fieldName;

	private Type fieldType;

	public AddFieldInfo(String fieldName, Type fieldType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		return "ALTER TABLE " + tableName + " ADD " + fieldName + " "
				+ driver.getTypeInAddColumnStatement(fieldType);
	}
}
