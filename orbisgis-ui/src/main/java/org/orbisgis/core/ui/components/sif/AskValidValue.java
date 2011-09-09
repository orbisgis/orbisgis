package org.orbisgis.core.ui.components.sif;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class AskValidValue extends AskValue {

	private DataSource ds;
	private int fieldIndex;
	private int fieldType;

	public AskValidValue(DataSource ds, int fieldIndex) throws DriverException {
		super("Field '" + ds.getFieldName(fieldIndex) + "'", null, null);
		this.ds = ds;
		this.fieldIndex = fieldIndex;
		this.fieldType = ds.getFieldType(fieldIndex).getTypeCode();
	}

	@Override
	public String validateInput() {
		try {
			return validateValue(ds, inputToValue(getValue(), fieldType),
					fieldIndex, fieldType);
		} catch (ParseException e) {
			return e.getMessage();
		}
	}

	public static String validateValue(DataSource ds, Value inputValue,
			int fieldIndex, int fieldType) {
		try {
			String error = ds.check(fieldIndex, inputValue);
			if (error != null) {
				return error;
			}
		} catch (NumberFormatException e) {
			return "Invalid number" + e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}

		return null;
	}

	public Value getUserValue() throws ParseException {
		String userInput = getValue();
		return inputToValue(userInput, fieldType);
	}

	public static Value inputToValue(String userInput, int fieldType)
			throws ParseException {
		Value value = ValueFactory.createValueByType(userInput, fieldType);
		return value;
	}
}
