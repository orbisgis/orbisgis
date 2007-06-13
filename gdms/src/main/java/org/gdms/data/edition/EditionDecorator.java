package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.Commiter;
import org.gdms.data.DataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteDriver;

public class EditionDecorator extends AbstractDataSourceDecorator {
	private List<PhysicalDirection> rowsDirections;

	private EditionListenerSupport editionListenerSupport;

	private boolean dirty;

	private boolean undoRedo;

	private ArrayList<EditionInfo> editionActions;

	private ArrayList<DeleteEditionInfo> deletedPKs;

	private InternalBuffer internalBuffer;

	private MetadataEditionListenerSupport mdels;

	private List<Field> fields;

	private ModifiedMetadata modifiedMetadata;

	private List<String> fieldsToDelete;

	private Commiter commiter;

	public EditionDecorator(DataSource internalDataSource, Commiter commiter) {
		super(internalDataSource);
		this.editionListenerSupport = new EditionListenerSupport(this);
		mdels = new MetadataEditionListenerSupport(this);
		this.commiter = commiter;
	}

	public void deleteRow(long rowId) throws DriverException {
		dirty = true;
		rowsDirections.remove((int) rowId);
		EditionInfo ei = editionActions.get((int) rowId);
		if (ei instanceof OriginalEditionInfo) {
			DeleteEditionInfo dei = new DeleteEditionInfo(
					((OriginalEditionInfo) ei).getPK());
			deletedPKs.add(dei);
		}
		editionActions.remove((int) rowId);

		editionListenerSupport.callDeleteRow(rowId, undoRedo);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		dirty = true;

		PhysicalDirection dir = internalBuffer.insertRow(null, values);
		rowsDirections.add(dir);
		InsertEditionInfo iei = new InsertEditionInfo(dir);
		editionActions.add(iei);

		editionListenerSupport.callInsert(getRowCount() - 1, undoRedo);
	}

	public void insertEmptyRow() throws DriverException {
		insertFilledRow(getEmptyRow());
	}

	private Value[] getEmptyRow() throws DriverException {
		Value[] row = new Value[getDataSource().getFieldCount()];

		for (int i = 0; i < row.length; i++) {
			row[i] = ValueFactory.createNullValue();
		}

		return row;
	}

	/**
	 * Gets the values of the original row
	 *
	 * @param rowIndex
	 *            index of the row to be retrieved
	 *
	 * @return Row values
	 *
	 * @throws DriverException
	 *             if the operation fails
	 */
	private Value[] getOriginalRow(PhysicalDirection dir)
			throws DriverException {
		ArrayList<Value> ret = new ArrayList<Value>();

		for (int i = 0; i < getFields().size(); i++) {
			int originalIndex = getFields().get(i).getOriginalIndex();
			if (originalIndex == -1) {
				ret.add(ValueFactory.createNullValue());
			} else {
				ret.add(dir.getFieldValue(originalIndex));
			}
		}

		return ret.toArray(new Value[0]);
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		PhysicalDirection dir = rowsDirections.get((int) row);
		if (dir instanceof OriginalDirection) {
			Value[] original = getOriginalRow(dir);
			original[fieldId] = value;
			PhysicalDirection newDirection = internalBuffer.insertRow(dir
					.getPK(), original);
			rowsDirections.set((int) row, newDirection);
			UpdateEditionInfo info = new UpdateEditionInfo(dir.getPK(),
					rowsDirections.get((int) row));
			editionActions.set((int) row, info);
		} else {
			((InternalBufferDirection) dir).setFieldValue(fieldId, value);
			/*
			 * We don't modify the EditionInfo because is an insertion that
			 * already points to the internal buffer
			 */
		}

		editionListenerSupport.callSetFieldValue(row, fieldId, undoRedo);
		dirty = true;
	}

	public void insertEmptyRowAt(long rowIndex) throws DriverException {
		insertFilledRowAt(rowIndex, getEmptyRow());
	}

	public void insertFilledRowAt(long rowIndex, Value[] values)
			throws DriverException {
		dirty = true;

		PhysicalDirection dir = internalBuffer.insertRow(null, values);
		rowsDirections.add((int) rowIndex, dir);
		InsertEditionInfo iei = new InsertEditionInfo(dir);
		editionActions.add((int) rowIndex, iei);

		editionListenerSupport.callInsert(rowIndex, undoRedo);
	}

	public void addEditionListener(EditionListener listener) {
		editionListenerSupport.addEditionListener(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		editionListenerSupport.removeEditionListener(listener);
	}

	public int getDispatchingMode() {
		return editionListenerSupport.getDispatchingMode();
	}

	public void setDispatchingMode(int dispatchingMode) {
		editionListenerSupport.setDispatchingMode(dispatchingMode);
	}

	public boolean isModified() {
		return dirty;
	}

	public void open() throws DriverException {
		dirty = false;
		getDataSource().open();
		long rowCount = getDataSource().getRowCount();
		undoRedo = false;
		fields = null;
		modifiedMetadata = null;
		internalBuffer = new MemoryInternalBuffer();
		fieldsToDelete = new ArrayList<String>();
		deletedPKs = new ArrayList<DeleteEditionInfo>();

		editionActions = new ArrayList<EditionInfo>();
		rowsDirections = new ArrayList<PhysicalDirection>();
		for (int i = 0; i < rowCount; i++) {
			PhysicalDirection dir = new OriginalDirection(getDataSource(), i);
			rowsDirections.add(dir);
			editionActions.add(new NoEditionInfo(dir.getPK(), i));
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		PhysicalDirection physicalDirection = rowsDirections
				.get((int) rowIndex);
		if (physicalDirection instanceof OriginalDirection) {
			int originalIndex = getFields().get(fieldId).getOriginalIndex();
			if (originalIndex == -1) {
				return ValueFactory.createNullValue();
			} else {
				return physicalDirection.getFieldValue(originalIndex);
			}
		} else {
			return physicalDirection.getFieldValue(fieldId);
		}
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return rowsDirections.size();
	}

	public void cancel() throws DriverException {
		internalBuffer = null;
		rowsDirections.clear();
		editionActions.clear();
		deletedPKs.clear();
		getDataSource().cancel();
	}

	@Override
	public void commit() throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		if (!(getDriver() instanceof ReadWriteDriver)) {
			throw new UnsupportedOperationException(
					"The driver has no write capabilities");
		}

		if (commiter != null) {
			commiter.commit(rowsDirections, getFieldNames(),
					getSchemaActions(), editionActions, deletedPKs, this);
		} else {
			throw new UnsupportedOperationException("DataSource not editable");
		}
		try {
			cancel();
		} catch (DriverException e) {
			throw new FreeingResourcesException(e);
		}
	}

	private ArrayList<EditionInfo> getSchemaActions() throws DriverException {
		ArrayList<EditionInfo> ret = new ArrayList<EditionInfo>();

		for (int i = 0; i < getFields().size(); i++) {
			Field f = getFields().get(i);
			if (f.getOriginalIndex() == -1) {
				ret.add(new AddFieldInfo(f.getName(), f.getType()));
			} else {
				String originalName = getDataSource().getMetadata()
						.getFieldName(f.getOriginalIndex());
				if (!f.getName().equals(originalName)) {
					ret.add(new ChangeFieldNameInfo(originalName, f.getName()));
				}
			}
		}

		for (int i = 0; i < fieldsToDelete.size(); i++) {
			ret.add(new RemoveFieldInfo(fieldsToDelete.get(i)));
		}

		return ret;
	}

	@Override
	public boolean isEditable() {
		if (commiter != null) {
			return getDataSource().isEditable();
		} else {
			return false;
		}
	}

	public void startUndoRedoAction() {
		undoRedo = true;
	}

	public void endUndoRedoAction() {
		undoRedo = false;
	}

	private List<Field> getFields() throws DriverException {
		if (null == fields) {
			fields = new ArrayList<Field>();
			Metadata metadata = getDataSource().getMetadata();
			final int fc = metadata.getFieldCount();

			for (int i = 0; i < fc; i++) {
				fields.add(new Field(i, metadata.getFieldName(i),
						getDataSource().getFieldType(i)));
			}
		}
		return fields;
	}

	public class ModifiedMetadata implements Metadata {

		public int getFieldCount() throws DriverException {
			return getFields().size();
		}

		public Type getFieldType(int fieldId) throws DriverException {
			return getFields().get(fieldId).getType();
		}

		public String getFieldName(int fieldId) throws DriverException {
			return getFields().get(fieldId).getName();
		}
	}

	@Override
	public Metadata getMetadata() throws DriverException {
		return getModifiedMetadata();
	}

	private Metadata getModifiedMetadata() {
		if (modifiedMetadata == null) {
			modifiedMetadata = new ModifiedMetadata();
		}
		return modifiedMetadata;
	}

	@Override
	public void addField(String name, Type driverType) throws DriverException {
		dirty = true;
		internalBuffer.addField();
		getFields().add(new Field(-1, name, driverType));
		mdels.callAddField(getFields().size() - 1);
	}

	@Override
	public void removeField(int index) throws DriverException {
		if (getFields().get(index).getType().isRemovable()) {
			dirty = true;

			Field toDelete = getFields().get(index);

			if (toDelete.getOriginalIndex() != -1) {
				fieldsToDelete.add(toDelete.getName());
			}

			internalBuffer.removeField(index);
			getFields().remove(index);
			mdels.callRemoveField(index);
		} else {
			throw new DriverException("The field cannot be deleted");
		}
	}

	@Override
	public void setFieldName(int index, String name) throws DriverException {
		dirty = true;
		getFields().get(index).setName(name);
		mdels.callModifyField(index);
	}

	@Override
	public void addMetadataEditionListener(MetadataEditionListener listener) {
		mdels.addEditionListener(listener);
	}

	@Override
	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		mdels.removeEditionListener(listener);
	}
}
