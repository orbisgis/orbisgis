package org.gdms.data.command;

import org.gdms.data.driver.DriverException;
import org.gdms.data.edition.EditableDataSource;
import org.gdms.data.values.Value;


public class InsertAtCommand extends AbstractCommand implements Command {

    private Value[] insertedRow;

    public InsertAtCommand(int index, EditableDataSource dataSource, Value[] row, CommandStack cs) {
        super(index, dataSource, cs);
        this.insertedRow = row;
    }

    public void redo() throws DriverException {
        try {
            dataSource.insertFilledRowAt(index, insertedRow);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

    public void undo() throws DriverException {
        try {
        	dataSource.deleteRow(index);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

}
