package org.gdms.data.edition;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.data.metadata.Metadata;

public class InsertEditionInfo implements EditionInfo {

	private PhysicalDirection dir;

	public InsertEditionInfo(PhysicalDirection dir)
			throws DriverException {
		this.dir = dir;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		Metadata metadata = dir.getMetadata();
		Type[] fieldTypes = new Type[metadata.getFieldCount()];
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			fieldTypes[i] = metadata.getFieldType(i);
		}
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = dir.getFieldValue(i);
		}

		return driver.getInsertSQL(tableName, fieldNames, fieldTypes, row);
	}

}
