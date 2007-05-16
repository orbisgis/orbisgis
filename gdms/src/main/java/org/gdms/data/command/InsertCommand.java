package org.gdms.data.command;

import org.gdms.data.edition.EditableDataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


public class InsertCommand extends AbstractCommand implements Command {

    private Value[] insertedRow;

    public InsertCommand(int index, EditableDataSource dataSource, Value[] insertedRow, CommandStack cs) {
        super(index, dataSource, cs);
        this.insertedRow = insertedRow;
    }

    public void redo() throws DriverException {
        try {
            dataSource.insertFilledRow(insertedRow);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

    public void undo() throws DriverException {
        try {
        	dataSource.deleteRow(dataSource.getRowCount() - 1);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

}
