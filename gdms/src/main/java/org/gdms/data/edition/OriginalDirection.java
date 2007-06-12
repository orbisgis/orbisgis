package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public class OriginalDirection implements PhysicalDirection {

	private DataSource source;
	private int row;

	public OriginalDirection(DataSource source, int row) {
		this.source = source;
		this.row = row;
	}

	public Value getFieldValue(int fieldId) throws DriverException {
		return source.getFieldValue(row, fieldId);
	}

	public ValueCollection getPK() throws DriverException {
		return source.getPK(row);
	}

}
