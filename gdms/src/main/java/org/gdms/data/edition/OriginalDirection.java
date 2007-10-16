package org.gdms.data.edition;

import java.io.Serializable;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public class OriginalDirection implements PhysicalDirection, Serializable {

	private transient DataSource source;
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

	public int getRowIndex() {
		return row;
	}

	public void setSource(DataSource source) {
		this.source = source;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OriginalDirection) {
			OriginalDirection od = (OriginalDirection) obj;
			return (od.source.getName().equals(source.getName()))
					&& (od.row == row);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return source.hashCode() + row;
	}

	public Metadata getMetadata() throws DriverException {
		return source.getMetadata();
	}

}
