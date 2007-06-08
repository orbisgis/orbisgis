package org.gdms.data.command;

import org.gdms.data.DataSource;

public class AbstractCommand {
	protected int index;

	protected DataSource dataSource;

	protected CommandStack commandStack;

	public AbstractCommand(int index, DataSource dataSource,
			CommandStack commandStack) {
		super();
		this.index = index;
		this.dataSource = dataSource;
		this.commandStack = commandStack;
	}

	protected int getIndex() {
		return index;
	}

	protected void setIndex(int index) {
		this.index = index;
	}
}
