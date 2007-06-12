package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;

public interface InternalBuffer {

	/**
	 * Inserts a row in the buffer and obtains a PhysicalDirection to access it
	 * in the future
	 *
	 * @param index
	 * @param newRow
	 * @return
	 */
	public PhysicalDirection insertRow(ValueCollection pk, Value[] newRow);

	/**
	 * Sets the specified field in the specified direction to the new value
	 *
	 * @param dir
	 * @param fieldId
	 * @param value
	 */
	public void setFieldValue(int row, int fieldId, Value value);

	/**
	 * Gets the value of a cell
	 *
	 * @param row
	 * @param fieldId
	 * @return
	 */
	public Value getFieldValue(int row, int fieldId);

	/**
	 * Notifies the internal buffer that a new field has been added to the
	 * DataSource
	 */
	public void addField();

	/**
	 * Notifies the internal buffer that the specified field has been removed
	 *
	 * @param index
	 */
	public void removeField(int index);
}
