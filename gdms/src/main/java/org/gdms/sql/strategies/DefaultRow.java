package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.edition.OriginalDirection;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

class DefaultRow implements Row {

	private DataSource ds;

	private int row;

	public DefaultRow(DataSource ds, long l) {
		this.ds = ds;
		this.row = (int) l;
	}

	/**
	 * @see org.gdms.sql.strategies.Row#getFieldValue(java.lang.String)
	 */
	public Value getFieldValue(String fieldId) throws DriverException {
		return ds.getFieldValue(row, ds.getFieldIndexByName(fieldId));
	}

	/**
	 * @see org.gdms.sql.strategies.Row#getIndex()
	 */
	public PhysicalDirection getPhysicalDirection() {
		return new OriginalDirection(ds, row);
	}

	/**
	 * @see org.gdms.sql.strategies.Row#getFieldValue(java.lang.String)
	 */
	public Value getFieldValue(int fieldId) throws DriverException {
		return ds.getFieldValue(row, fieldId);
	}

}
