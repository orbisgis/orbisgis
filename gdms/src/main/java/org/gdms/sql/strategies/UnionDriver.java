package org.gdms.sql.strategies;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class UnionDriver extends AbstractSQLDriver implements ObjectDriver {

	private ObjectDriver table1;
	private ObjectDriver table2;

	public UnionDriver(ObjectDriver table1, ObjectDriver table2,
			Metadata metadata) {
		super(metadata);
		this.table1 = table1;
		this.table2 = table2;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		long tamTabla1 = table1.getRowCount();

		if (rowIndex < tamTabla1) {
			return table1.getFieldValue(rowIndex, fieldId);
		} else {
			return table2.getFieldValue(rowIndex - tamTabla1, fieldId);
		}
	}

	public long getRowCount() throws DriverException {
		return table1.getRowCount() + table2.getRowCount();
	}
}