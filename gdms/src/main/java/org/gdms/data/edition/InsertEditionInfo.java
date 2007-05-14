package org.gdms.data.edition;

import org.gdms.data.InnerDBUtils;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.values.Value;

public class InsertEditionInfo extends BaseEditionInfo implements EditionInfo {

	private String tableName;

	private String[] fieldNames;

	private long internalBufferIndex;

	private InternalBuffer internalBuffer;

	public InsertEditionInfo(String tableName, InternalBuffer internalBuffer,
			long internalBufferIndex, String[] fieldNames, DBDriver driver) {
		super(driver);
		this.tableName = tableName;
		this.internalBuffer = internalBuffer;
		this.internalBufferIndex = internalBufferIndex;
		this.fieldNames = fieldNames;
	}

	public String getSQL() throws DriverException {
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = internalBuffer.getFieldValue(internalBufferIndex, i);
		}

		return InnerDBUtils.createInsertStatement(super
				.getReferenceExpression(tableName), row, super
				.getReferenceExpression(fieldNames), driver);
	}

}
