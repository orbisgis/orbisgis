package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

public class MemoryInternalBuffer implements InternalBuffer {

	private ArrayList<ArrayList<Value>> rows = new ArrayList<ArrayList<Value>>();

	private ArrayList<Value> getRow(Value[] values) {
		ArrayList<Value> row = new ArrayList<Value>();

		for (int i = 0; i < values.length; i++) {
			row.add(values[i]);
		}

		return row;
	}

	public PhysicalDirection insertRow(ValueCollection pk, Value[] newRow) {
		rows.add(getRow(newRow));
		return new InternalBufferDirection(pk, this, rows.size() - 1);
	}

	public void setFieldValue(int row, int fieldId, Value value) {
		rows.get(row).set(fieldId, value);
	}

	public Value getFieldValue(int row, int fieldId) {
		Value v =  rows.get(row).get(fieldId);
		if (v == null) {
			return ValueFactory.createNullValue();
		} else {
			return v;
		}
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
}
