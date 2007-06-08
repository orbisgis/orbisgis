package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class MemoryInternalBuffer implements InternalBuffer {

	private ArrayList<ArrayList<Value>> rows = new ArrayList<ArrayList<Value>>();

	private ArrayList<ArrayList<Value>> backRows;

	public void deleteRow(long rowId) throws DriverException {
		rows.remove(rowId);
	}

	private ArrayList<Value> getRow(Value[] values) {
		ArrayList<Value> row = new ArrayList<Value>();

		for (int i = 0; i < values.length; i++) {
			row.add(values[i]);
		}

		return row;
	}

	public long insertRow(Value[] values) throws DriverException {
		ArrayList<Value> row = getRow(values);

		rows.add(row);
		return rows.size() - 1;
	}

	public void setRow(long row, Value[] modifiedRow) throws DriverException {
		ArrayList<Value> valueArray = getRow(modifiedRow);

		rows.set((int) row, valueArray);
	}

	public void setFieldValue(long row, int modifiedField, Value modifiedValue)
			throws DriverException {
		rows.get((int) row).set(modifiedField, modifiedValue);
	}

	public void start() throws DriverException {
	}

	public void stop() throws DriverException {
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return rows.get((int) rowIndex).get(fieldId);
	}

	public void removeField(int index) {
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<Value> row = rows.get(i);
			row.remove(index);
		}
	}

	public void addField() {
		Value nullValue = ValueFactory.createNullValue();
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<Value> row = rows.get(i);
			row.add(nullValue);
		}
	}

	@SuppressWarnings("unchecked")
	public void saveStatus() {
		backRows = (ArrayList<ArrayList<Value>>) rows.clone();
	}

	public void restoreStatus() {
		rows = backRows;
	}
}
