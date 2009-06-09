package org.orbisgis.core.ui.components.sif;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.sif.SQLUIPanel;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.StringType;

public class AskValidRow extends MultiInputPanel implements SQLUIPanel {

	private int fieldCount;
	private DataSource ds;
	private int[] types;

	public AskValidRow(String title, DataSource ds) throws DriverException {
		super(title);
		this.ds = ds;
		this.fieldCount = ds.getFieldCount();
		this.types = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			types[i] = ds.getFieldType(i).getTypeCode();
			addInput("f" + i, ds.getFieldName(i), new StringType());
		}
	}

	@Override
	public String validateInput() {
		try {
			for (int i = 0; i < fieldCount; i++) {
				String input = getInput("f" + i);
				Value inputValue = ValueFactory.createNullValue();
				if (input.length() > 0) {
					inputValue = AskValidValue.inputToValue(input, types[i]);
				}
				String error = AskValidValue.validateValue(ds, inputValue, i,
						types[i]);
				if (error != null) {
					return error;
				}
			}
		} catch (ParseException e) {
			return e.getMessage();
		}
		return super.validateInput();
	}

	public Value[] getRow() throws ParseException {
		Value[] ret = new Value[fieldCount];
		for (int i = 0; i < ret.length; i++) {
			String input = getInput("f" + i);
			if (input.length() > 0) {
				ret[i] = AskValidValue.inputToValue(input, types[i]);
			} else {
				ret[i] = ValueFactory.createNullValue();
			}
		}
		return ret;
	}

}
