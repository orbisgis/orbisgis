package org.gdms.data.command;

import org.gdms.data.driver.DriverException;

public interface Command {
    public void redo() throws DriverException;
    public void undo() throws DriverException;
}
