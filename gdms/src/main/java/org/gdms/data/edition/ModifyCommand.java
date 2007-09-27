package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class ModifyCommand extends AbstractCommand implements Command {

	private int fieldIndex;

	private Value newValue;

	private ModifyInfo info;

	public ModifyCommand(int index, EditionDecorator dataSource,
			Value newValue, int fieldIndex) {
		super(index, dataSource);
		this.fieldIndex = fieldIndex;
		this.newValue = newValue;
	}

	public void redo() throws DriverException {
		info = dataSource.doSetFieldValue(index, fieldIndex, newValue);
	}

	public void undo() throws DriverException {
		dataSource.undoSetFieldValue(info.previousDir, info.previousInfo,
				info.ibDir, info.previousValue, info.fieldId, info.row);
	}

	public static class ModifyInfo {
		public OriginalDirection previousDir;

		public EditionInfo previousInfo;

		public InternalBufferDirection ibDir;

		public Value previousValue;

		public long row;

		public int fieldId;

		public ModifyInfo(OriginalDirection previousDir,
				EditionInfo previousInfo, InternalBufferDirection ibDir,
				Value previousValue, long row, int fieldId) {
			super();
			this.previousDir = previousDir;
			this.previousInfo = previousInfo;
			this.ibDir = ibDir;
			this.previousValue = previousValue;
			this.row = row;
			this.fieldId = fieldId;
		}

	}
}
