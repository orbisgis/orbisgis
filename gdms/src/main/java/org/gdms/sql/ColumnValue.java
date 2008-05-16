package org.gdms.sql;

import org.gdms.data.values.Value;

public class ColumnValue {

	private Value value;
	private int typeCode;

	public ColumnValue(int typeCode, Value value) {
		super();
		this.value = value;
		this.typeCode = typeCode;
	}

	public Value getValue() {
		return value;
	}

	public int getTypeCode() {
		return typeCode;
	}

}
