package org.gdms.sql.strategies;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class LimitOffsetDriver extends AbstractMetadataSQLDriver implements
		ObjectDriver {

	private int limit;
	private int offset;
	private ObjectDriver driver;

	public LimitOffsetDriver(int limit, int offset, ObjectDriver driver)
			throws DriverException {
		super(driver.getMetadata());
		this.driver = driver;
		this.limit = limit;
		this.offset = offset;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (offset != -1) {
			return driver.getFieldValue(offset + rowIndex, fieldId);
		} else {
			return driver.getFieldValue(rowIndex, fieldId);
		}
	}

	public long getRowCount() throws DriverException {
		if (limit != -1) {
			return limit;
		} else {
			if (offset != -1) {
				return driver.getRowCount() - offset;
			} else {
				throw new RuntimeException("bug!");
			}
		}
	}
}
