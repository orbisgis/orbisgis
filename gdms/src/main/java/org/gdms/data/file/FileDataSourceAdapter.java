package org.gdms.data.file;

import java.io.File;
import java.io.IOException;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DriverDataSource;
import org.gdms.data.DriverDataSourceImpl;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.OpenCloseCounter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MetadataEditionSupport;
import org.gdms.data.edition.RowOrientedEditionDataSourceImpl;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileReadWriteDriver;

/**
 * Adapta la interfaz FileDriver a la interfaz DataSource
 * 
 * @author Fernando Gonzalez Cortes
 */
@DriverDataSource
public class FileDataSourceAdapter extends DataSourceCommonImpl {
	private RowOrientedEditionDataSourceImpl rowOrientedEdition;

	private FileDataSourceSupport fileDataSource;

	private MetadataEditionSupport metadataEdition;

	private DriverDataSourceImpl driverDataSourceSupport;

	private OpenCloseCounter ocCounter;

	private FileDriver driver;

	private File file;

	public FileDataSourceAdapter(String name, String alias, File file,
			FileDriver driver) {
		super(name, alias);
		ocCounter = new OpenCloseCounter(this);
		fileDataSource = new FileDataSourceSupport(this, file, driver);
		metadataEdition = new MetadataEditionSupport(this);
		rowOrientedEdition = new RowOrientedEditionDataSourceImpl(this,
				metadataEdition);
		driverDataSourceSupport = new DriverDataSourceImpl(driver);
		this.driver = driver;
		this.file = file;
	}

	public FileDriver getDriver() {
		return fileDataSource.getDriver();
	}

	/**
	 * @see org.gdms.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws DriverException {
		return new int[0];
	}

	public void commit() throws DriverException, FreeingResourcesException {
		if (ocCounter.stop()) {
			File temp = new File(fileDataSource.getDriver().completeFileName(
					getDataSourceFactory().getTempFile()));
			try {
				((FileReadWriteDriver) getDriver()).writeFile(temp, this);
			} catch (DriverException e) {
				ocCounter.start();
				throw e;
			}
			try {
				driver.close();
				rowOrientedEdition.commitTrans();
			} catch (DriverException e) {
				throw new FreeingResourcesException(
						"Cannot free resources: data writen in "
								+ temp.getAbsolutePath(), e, temp);
			}
			try {
				((FileReadWriteDriver) getDriver()).copy(temp, fileDataSource
						.getFile());
			} catch (IOException e) {
				throw new FreeingResourcesException(
						"Cannot copy file: data writen in "
								+ temp.getAbsolutePath(), e, temp);
			}
		}
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return metadataEdition.getFieldCount();
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return getDataSourceMetadata().getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getFieldType(int)
	 */
	public Type getFieldType(final int i) throws DriverException {
		return getDataSourceMetadata().getFieldType(i);
	}

	/**
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource ds) throws DriverException {
		if (ocCounter.isOpen()) {
			throw new RuntimeException(
					"Cannot invoke saveData of an opened DataSource");
		}
		ds.open();
		((FileReadWriteDriver) driver).writeFile(file, ds);
		ds.cancel();
	}

	public void deleteRow(long rowId) throws DriverException {
		rowOrientedEdition.deleteRow(rowId);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		rowOrientedEdition.insertFilledRowAt(index, values);
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		rowOrientedEdition.insertEmptyRowAt(index);
	}

	public void open() throws DriverException {
		if (ocCounter.start()) {
			driver.open(file);
			metadataEdition.start();
			rowOrientedEdition.beginTrans();
		}
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		if (ocCounter.stop()) {
			try {
				driver.close();
				rowOrientedEdition.rollBackTrans();
			} catch (DriverException e) {
				ocCounter.start();
				throw e;
			}
		}
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return metadataEdition.getFieldIndexByName(fieldName);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		rowOrientedEdition.insertFilledRow(values);
	}

	public void insertEmptyRow() throws DriverException {
		rowOrientedEdition.insertEmptyRow();

	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		rowOrientedEdition.setFieldValue(row, fieldId, value);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return rowOrientedEdition.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return rowOrientedEdition.getRowCount();
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return fileDataSource.getDriver().getFieldValue(rowIndex, fieldId);
	}

	public void addEditionListener(EditionListener listener) {
		rowOrientedEdition.addEditionListener(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		rowOrientedEdition.removeEditionListener(listener);
	}

	public void setDispatchingMode(int dispatchingMode) {
		rowOrientedEdition.setDispatchingMode(dispatchingMode);
	}

	public int getDispatchingMode() {
		return rowOrientedEdition.getDispatchingMode();
	}

	public void addField(String name, Type type) throws DriverException {
		metadataEdition.addField(name, type);
		rowOrientedEdition.addField();
	}

	public void removeField(int index) throws DriverException {
		metadataEdition.removeField(index);
		rowOrientedEdition.removeField(index);
	}

	public void setFieldName(int index, String name) throws DriverException {
		metadataEdition.setFieldName(index, name);
		rowOrientedEdition.setFieldName();
	}

	public int getOriginalFieldCount() throws DriverException {
		return metadataEdition.getOriginalFieldCount();
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		metadataEdition.addMetadataEditionListener(listener);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		metadataEdition.removeMetadataEditionListener(listener);
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return metadataEdition.getDataSourceMetadata();
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return fileDataSource.getMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return MetadataUtilities.check(getDataSourceMetadata(), fieldId, value);
	}

	public void endUndoRedoAction() {
		rowOrientedEdition.endUndoRedoAction();
	}

	public void startUndoRedoAction() {
		rowOrientedEdition.startUndoRedoAction();
	}

	public boolean isModified() {
		return rowOrientedEdition.isModified();
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public boolean isOpen() {
		return ocCounter.isOpen();
	}

	public long getOriginalRowCount() throws DriverException {
		return driver.getRowCount();
	}
}