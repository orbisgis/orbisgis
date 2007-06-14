package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.Row;

public class PhysicalDirectionRow implements Row {

	private PhysicalDirection direction;
	private DataSource dataSource;

	public PhysicalDirectionRow(PhysicalDirection direction, DataSource dataSource) {
		this.dataSource = dataSource;
		this.direction = direction;
	}

	public Value getFieldValue(String fieldId) throws DriverException {
		return direction.getFieldValue(dataSource.getFieldIndexByName(fieldId));
	}

	public Value getFieldValue(int fieldId) throws DriverException {
		return direction.getFieldValue(fieldId);
	}

	public PhysicalDirection getPhysicalDirection() {
		return direction;
	}

}
