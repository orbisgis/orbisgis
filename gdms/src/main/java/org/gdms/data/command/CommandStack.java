package org.gdms.data.command;

import java.util.ArrayList;

import org.gdms.driver.DriverException;

/**
 * This class stores commands executed against the current edition theme. It
 * stores a limited number of commands throwing away the first command that were
 * stored in the stack when this limit is reached
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class CommandStack {
	private ArrayList<Command> commands = new ArrayList<Command>();

	private int position = 0;

	private boolean useLimit = true;

	private int limit = 40;

	public void put(Command c) throws DriverException {
		c.redo();
		commands.add(position, c);
		position++;
		for (int i = position; i < commands.size();) {
			commands.remove(i);
		}

		if (useLimit && (position > limit)) {
			position--;
			commands.remove(0);
		}
	}

	public Command redo() throws DriverException {
		Command ret = commands.get(position);
		ret.redo();
		position++;

		return ret;
	}

	public Command undo() throws DriverException {
		position--;
		Command ret = commands.get(position);
		ret.undo();

		return ret;
	}

	public boolean canUndo() {
		return position != 0;
	}

	public boolean canRedo() {
		return position < commands.size();
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isUseLimit() {
		return useLimit;
	}

	public void setUseLimit(boolean limited) {
		this.useLimit = limited;
	}

	public void clear() {
		position = 0;
		commands = new ArrayList<Command>();
	}
}
