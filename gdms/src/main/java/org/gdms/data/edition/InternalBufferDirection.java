package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public class InternalBufferDirection implements PhysicalDirection {

	private InternalBuffer buffer;

	private int row;

	private ValueCollection pk;

	public InternalBufferDirection(ValueCollection pk, InternalBuffer buffer,
			int row) {
		this.row = row;
		this.buffer = buffer;
		this.pk = pk;
	}

	public Value getFieldValue(int fieldId) throws DriverException {
		return buffer.getFieldValue(row, fieldId);
	}

	public void setFieldValue(int fieldId, Value value) {
		buffer.setFieldValue(row, fieldId, value);
	}

	public ValueCollection getPK() throws DriverException {
		return pk;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InternalBufferDirection) {
			InternalBufferDirection od = (InternalBufferDirection) obj;
			return (od.buffer == buffer) && (od.row == row) && (od.pk == pk);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return buffer.hashCode() + row + pk.hashCode();
	}

}
