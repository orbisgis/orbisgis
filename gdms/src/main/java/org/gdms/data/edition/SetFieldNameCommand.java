package org.gdms.data.edition;

import org.gdms.driver.DriverException;

public class SetFieldNameCommand implements Command {

	private EditionDecorator dataSource;

	private int fieldIndex;

	private String fieldName;

	private String previousName;

	public SetFieldNameCommand(EditionDecorator dataSource, int index, String name) {
		this.dataSource = dataSource;
		this.fieldIndex = index;
		this.fieldName = name;
	}

	public void redo() throws DriverException {
		previousName = dataSource.getFieldName(fieldIndex);
		dataSource.doSetFieldName(fieldIndex, fieldName);
	}

	public void undo() throws DriverException {
		dataSource.doSetFieldName(fieldIndex, previousName);
	}

}
