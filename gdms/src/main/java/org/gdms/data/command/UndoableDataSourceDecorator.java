package org.gdms.data.command;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class UndoableDataSourceDecorator extends AbstractDataSourceDecorator {

	private CommandStack cs;

	public UndoableDataSourceDecorator(DataSource ds) {
		super(ds);
		cs = new CommandStack();
	}

	private Value[] getEmptyRow() throws DriverException {
		Value[] row = new Value[getDataSource().getDataSourceMetadata()
				.getFieldCount()];
		for (int i = 0; i < row.length; i++) {
			row[i] = ValueFactory.createNullValue();
		}

		return row;
	}

	public void addField(String name, String type) throws DriverException {
		getDataSource().addField(name, type);
		cs.clear();
	}

	public boolean canRedo() {
		return cs.canRedo();
	}

	public boolean canUndo() {
		return cs.canUndo();
	}

	public void deleteRow(long rowId) throws DriverException {
		DeleteCommand c = new DeleteCommand((int) rowId, getDataSource(), cs);
		
		cs.put(c);
	}

	public void insertEmptyRow() throws DriverException {
		InsertCommand c = new InsertCommand((int) getDataSource().getRowCount() - 1,
				getDataSource(), getEmptyRow(), cs);
		
		cs.put(c);
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		InsertAtCommand c = new InsertAtCommand((int) index, getDataSource(),
				getEmptyRow(), cs);
		
		cs.put(c);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		InsertCommand c = new InsertCommand((int) getDataSource().getRowCount() - 1,
				getDataSource(), values, cs);
		
		cs.put(c);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		InsertAtCommand c = new InsertAtCommand((int) index, getDataSource(), values, cs);
		
		cs.put(c);
	}

	public void redo() throws DriverException {
		getDataSource().startUndoRedoAction();
		cs.redo();
		getDataSource().endUndoRedoAction();
	}

	public void removeField(int index) throws DriverException {
		getDataSource().removeField(index);
		cs.clear();
	}

	public void cancel() throws DriverException {
		cs.clear();
		getDataSource().cancel();
	}

	public void setFieldName(int index, String name) throws DriverException {
		getDataSource().setFieldName(index, name);
		cs.clear();
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		ModifyCommand c = new ModifyCommand((int) row, getDataSource(), getDataSource()
				.getFieldValue(row, fieldId), value, fieldId, cs);
		
		cs.put(c);
	}

	public void undo() throws DriverException {
		getDataSource().startUndoRedoAction();
		cs.undo();
		getDataSource().endUndoRedoAction();
	}
}