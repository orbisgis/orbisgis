package org.gdms.sql.strategies;

import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public interface Row {

	public abstract Value getFieldValue(String fieldId) throws DriverException;

	public abstract Value getFieldValue(int fieldId) throws DriverException;

	public PhysicalDirection getPhysicalDirection();
}