package org.orbisgis.editors.map.tools;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.ui.sif.AskValue;

public class AskValidValue extends AskValue {

	private DataSource ds;
	private int fieldIndex;
	private int fieldType;

	public AskValidValue(DataSource ds, int fieldIndex) throws DriverException {
		super("Field '"
				+ ds.getFieldName(fieldIndex) + "'", null, null);
		this.ds = ds;
		this.fieldIndex = fieldIndex;
		this.fieldType = ds.getFieldType(fieldIndex).getTypeCode();
	}

	@Override
	public String validateInput() {
		try {
			Value value = getUserValue();
			String error = ds.check(fieldIndex, value);
			if (error != null) {
				return error;
			}
		} catch (NumberFormatException e) {
			return "Invalid number" + e.getMessage();
		} catch (ParseException e) {
			return "Cannot parse value" + e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}
		return super.validateInput();
	}

	public Value getUserValue() throws ParseException {
		String userInput = getValue();
		Value value = ValueFactory.createValueByType(userInput, fieldType);
		return value;
	}
}
