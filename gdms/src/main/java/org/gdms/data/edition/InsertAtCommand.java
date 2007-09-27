package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class InsertAtCommand extends AbstractCommand implements Command {

	private Value[] insertedRow;

	public InsertAtCommand(int index, EditionDecorator dataSource, Value[] row) {
		super(index, dataSource);
		this.insertedRow = row;
	}

	public void redo() throws DriverException {
		dataSource.doInsertAt(index, insertedRow);
	}

	public void undo() throws DriverException {
		dataSource.undoInsertAt(index);
	}

}
