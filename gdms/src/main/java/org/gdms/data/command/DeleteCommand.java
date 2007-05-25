package org.gdms.data.command;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


public class DeleteCommand extends AbstractCommand implements Command {
    
    private Value[] deletedRow;
    
    public DeleteCommand(int index, DataSource dataSource, CommandStack cs) {
        super(index, dataSource, cs);
    }

    public void redo() throws DriverException {
        try {
            deletedRow = dataSource.getRow(index);
            dataSource.deleteRow(index);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

    public void undo() throws DriverException {
        try {
            dataSource.insertFilledRowAt(index, deletedRow);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }
    
}
