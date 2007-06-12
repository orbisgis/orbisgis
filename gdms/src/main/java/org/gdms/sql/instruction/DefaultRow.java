package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class DefaultRow implements Row {

	private DataSource ds;

	private int row;

	public DefaultRow(DataSource ds, long l) {
		this.ds = ds;
		this.row = (int) l;
	}

	/**
	 * @see org.gdms.sql.instruction.Row#getFieldValue(java.lang.String)
	 */
	public Value getFieldValue(String fieldId) throws DriverException {
		return ds.getFieldValue(row, ds.getFieldIndexByName(fieldId));
	}

	/**
	 * @see org.gdms.sql.instruction.Row#getIndex()
	 */
	public int getIndex() {
		return row;
	}

	/**
	 * @see org.gdms.sql.instruction.Row#getFieldValue(java.lang.String)
	 */
	public Value getFieldValue(int fieldId) throws DriverException {
		return ds.getFieldValue(row, fieldId);
	}

}
