package org.gdms.data.edition;

import org.gdms.driver.DriverException;

public class DeleteCommand extends AbstractCommand implements Command {

	private DeleteCommandInfo info;

	public DeleteCommand(int index, EditionDecorator dataSource) {
		super(index, dataSource);
	}

	public void redo() throws DriverException {
		info = dataSource.doDeleteRow(index);
	}

	public void undo() throws DriverException {
		dataSource.undoDeleteRow(info.dir, info.rowId, info.dei, info.ei);
	}

	public static class DeleteCommandInfo {
		public PhysicalDirection dir;

		public long rowId;

		public DeleteEditionInfo dei;

		public EditionInfo ei;

		public DeleteCommandInfo(PhysicalDirection dir, long rowId,
				DeleteEditionInfo dei, EditionInfo ei) {
			super();
			this.dir = dir;
			this.rowId = rowId;
			this.dei = dei;
			this.ei = ei;
		}

	}
}
