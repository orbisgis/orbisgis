package org.gdms.data.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.Commiter;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DriverDataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
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
public class FileDataSourceAdapter extends DataSourceCommonImpl implements
		Commiter {

	private FileDriver driver;

	private File file;

	public FileDataSourceAdapter(String name, String alias, File file,
			FileDriver driver) {
		super(name, alias);
		this.driver = driver;
		this.file = file;
	}

	public FileDriver getDriver() {
		return driver;
	}

	public void commit() throws DriverException, FreeingResourcesException {
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return getMetadata().getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.data.edition.DataSource#getFieldType(int)
	 */
	public Type getFieldType(final int i) throws DriverException {
		return getMetadata().getFieldType(i);
	}

	/**
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource ds) throws DriverException {
		ds.open();
		((FileReadWriteDriver) driver).writeFile(file, ds);
		ds.cancel();
	}

	public void open() throws DriverException {
		driver.open(file);
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		driver.close();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return driver.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return driver.getRowCount();
	}

	public Metadata getMetadata() throws DriverException {
		return driver.getMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return MetadataUtilities.check(getMetadata(), fieldId, value);
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public void commit(List<PhysicalDirection> rowsDirections,
			String[] fieldNames, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException, FreeingResourcesException {
		File temp = new File(driver.completeFileName(getDataSourceFactory()
				.getTempFile()));
		((FileReadWriteDriver) driver).writeFile(temp, modifiedSource);
		try {
			driver.close();
		} catch (DriverException e) {
			throw new FreeingResourcesException(
					"Cannot free resources: data writen in "
							+ temp.getAbsolutePath(), e, temp);
		}
		try {
			((FileReadWriteDriver) driver).copy(temp, file);
		} catch (IOException e) {
			throw new FreeingResourcesException(
					"Cannot copy file: data writen in "
							+ temp.getAbsolutePath(), e, temp);
		}
	}
}