package org.gdms.data.command;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


public class ModifyCommand extends AbstractCommand implements Command {

    private int fieldIndex;
    private Value oldValue;
    private Value newValue;

    public ModifyCommand(int index, DataSource dataSource, 
            Value oldValue, Value newValue, int fieldIndex, CommandStack cs) {
        super(index, dataSource, cs);
        this.fieldIndex = fieldIndex;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void redo() throws DriverException {
        try {
            dataSource.setFieldValue(index, fieldIndex, newValue);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

    public void undo() throws DriverException {
        try {
            dataSource.setFieldValue(index, fieldIndex, oldValue);
        } catch (DriverException e) {
            throw new DriverException(e);
        }
    }

}
