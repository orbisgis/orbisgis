package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Field extends Operand {

	private String fieldName;
	private int index = -1;
	private String tableName;

	public Field(String fieldName) {
		this.fieldName = fieldName;
	}

	public Field(String tableName, String fieldName) {
		this.fieldName = fieldName;
		this.tableName = tableName;
	}

	public Value evaluate() throws IncompatibleTypesException, DriverException {
		Value value = ec.getDataSource().getFieldValue(ec.getRowIndex(),
				getFieldIndex());
		return value;
	}

	private int getFieldIndex() throws DriverException {
		if (index == -1) {
			index = ec.getDataSource().getFieldIndexByName(fieldName);

		}

		return index;
	}

	@Override
	public void setEvaluationContext(EvaluationContext ec) {
		this.ec = ec;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public int getType() throws DriverException {
		return ec.getDataSource().getFieldType(getFieldIndex()).getTypeCode();
	}

	public void setFieldIndex(int fieldIndex) {
		index = fieldIndex;
	}

	@Override
	public String toString() {
		String ret = "";
		if (tableName != null) {
			ret += tableName+".";
		}

		ret+=fieldName;

		return ret;
	}
}
