package org.gdms.data.command;

import org.gdms.data.driver.DriverException;
import org.gdms.data.edition.EditableDataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 * @author Fernando González Cortés
 *
 */
public class CommandImpl {

	private EditableDataSource dataSource;

	private CommandStack cs;

	public CommandImpl(EditableDataSource ds) {
		this.dataSource = ds;
		cs = new CommandStack();
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		ModifyCommand c = new ModifyCommand((int) row, dataSource, dataSource
				.getFieldValue(row, fieldId), value, fieldId, cs);

		cs.put(c);
	}

	public boolean canRedo() {
		return cs.canRedo();
	}

	public boolean canUndo() {
		return cs.canUndo();
	}

	public void redo() throws DriverException {
		dataSource.startUndoRedoAction();
		cs.redo();
		dataSource.endUndoRedoAction();
	}

	public void undo() throws DriverException {
		dataSource.startUndoRedoAction();
		cs.undo();
		dataSource.endUndoRedoAction();
	}

	public void rollBackTrans() throws DriverException {
		cs.clear();
		dataSource.rollBackTrans();
	}

	public void addField(String name, String type) throws DriverException {
		dataSource.addField(name, type);
		cs.clear();
	}

	public void removeField(int index) throws DriverException {
		dataSource.removeField(index);
		cs.clear();
	}

	public void setFieldName(int index, String name) throws DriverException {
		dataSource.setFieldName(index, name);
		cs.clear();
	}

	public void deleteRow(long rowId) throws DriverException {
		DeleteCommand c = new DeleteCommand((int) rowId, dataSource, cs);

		cs.put(c);
	}

	public void insertEmptyRow() throws DriverException {
		InsertCommand c = new InsertCommand((int) dataSource.getRowCount() - 1,
				dataSource, getEmptyRow(), cs);

		cs.put(c);
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		InsertAtCommand c = new InsertAtCommand((int) index, dataSource,
				getEmptyRow(), cs);

		cs.put(c);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		InsertCommand c = new InsertCommand((int) dataSource.getRowCount() - 1,
				dataSource, values, cs);

		cs.put(c);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		InsertCommand c = new InsertCommand((int) index, dataSource, values, cs);

		cs.put(c);
	}

	private Value[] getEmptyRow() throws DriverException {
		Value[] row = new Value[dataSource.getDataSourceMetadata()
				.getFieldCount()];
		for (int i = 0; i < row.length; i++) {
			row[i] = ValueFactory.createNullValue();
		}

		return row;
	}

}
