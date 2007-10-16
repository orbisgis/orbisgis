package org.gdms.data.edition;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public interface PhysicalDirection {

	public Value getFieldValue(int fieldId) throws DriverException;

	public ValueCollection getPK() throws DriverException;

	public Metadata getMetadata() throws DriverException;
}
