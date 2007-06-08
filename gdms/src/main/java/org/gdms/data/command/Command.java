package org.gdms.data.command;

import org.gdms.driver.DriverException;

public interface Command {
	public void redo() throws DriverException;

	public void undo() throws DriverException;
}
