package org.gdms.data.edition;

import org.gdms.data.InnerDBUtils;
import org.gdms.data.values.Value;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

public class InsertEditionInfo extends BaseEditionInfo {

	private PhysicalDirection dir;

	public InsertEditionInfo(PhysicalDirection dir) {
		this.dir = dir;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver) throws DriverException {
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = dir.getFieldValue(i);
		}

		return InnerDBUtils.createInsertStatement(super.getReferenceExpression(
				driver, tableName), row, super.getReferenceExpression(driver,
				fieldNames), driver);
	}

}
