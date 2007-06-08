package org.gdms.data.edition;

import org.gdms.data.DataSource;

/**
 * This class stores information about the change in the contents of a
 * DataSource. It stores the row index where the change was made and which the
 * action done was. If the type is MODIFY then rowIndex and field index store
 * where the modification was done. If the type is DELETE or INSERT then
 * fieldIndex is set to -1
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class EditionEvent extends FieldEditionEvent {
	private long rowIndex;

	public static final int MODIFY = 0;

	public static final int DELETE = 1;

	public static final int INSERT = 2;

	private int type;

	private boolean undoRedo;

	public EditionEvent(long rowIndex, int fieldIndex, int type, DataSource ds,
			boolean undoRedo) {
		super(fieldIndex, ds);
		this.rowIndex = rowIndex;
		this.type = type;
		this.undoRedo = undoRedo;
	}

	public long getRowIndex() {
		return rowIndex;
	}

	public int getType() {
		return type;
	}

	public boolean isUndoRedo() {
		return undoRedo;
	}

}
