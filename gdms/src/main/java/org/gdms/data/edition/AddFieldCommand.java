package org.gdms.data.edition;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;

public class AddFieldCommand implements Command {

	private String name;

	private EditionDecorator dataSource;

	private Type type;

	public AddFieldCommand(EditionDecorator dataSource, String name, Type type) {
		this.dataSource = dataSource;
		this.name = name;
		this.type = type;
	}

	public void redo() throws DriverException {
		dataSource.doAddField(name, type);
	}

	public void undo() throws DriverException {
		dataSource.undoAddField();
	}

}
