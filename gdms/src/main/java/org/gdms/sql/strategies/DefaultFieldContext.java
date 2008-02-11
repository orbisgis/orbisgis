/**
 *
 */
package org.gdms.sql.strategies;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.FieldContext;

class DefaultFieldContext implements FieldContext {

	private long index;
	private ObjectDriver ds;

	public DefaultFieldContext(ObjectDriver ds) {
		this.ds = ds;
	}

	public void setIndex(long rowIndex) {
		this.index = rowIndex;
	}

	public Type getFieldType(int fieldId) throws DriverException {
		return ds.getMetadata().getFieldType(fieldId);
	}

	public Value getFieldValue(int fieldId) throws DriverException {
		return ds.getFieldValue(index, fieldId);
	}

}