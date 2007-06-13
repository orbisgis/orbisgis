package org.gdms.data.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.Commiter;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ObjectReadWriteDriver;

public class ObjectDataSourceAdapter extends DataSourceCommonImpl implements
		Commiter {

	private ObjectDriver driver;

	public ObjectDataSourceAdapter(String name, String alias,
			ObjectDriver driver) {
		super(name, alias);
		this.driver = driver;
	}

	public void open() throws DriverException {
		driver.start();
	}

	public void commit() throws DriverException, FreeingResourcesException {
		driver.stop();
		((ObjectReadWriteDriver) driver).write(this);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return driver.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return driver.getRowCount();
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		driver.stop();
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.open();
		((ObjectReadWriteDriver) driver).write(ds);
		ds.cancel();
	}

	public String getFieldName(int fieldId) throws DriverException {
		return getMetadata().getFieldName(fieldId);
	}

	public Type getFieldType(int i) throws DriverException {
		return getMetadata().getFieldType(i);
	}

	public Metadata getMetadata() throws DriverException {
		return driver.getMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return MetadataUtilities.check(getMetadata(), fieldId, value);
	}

	public ObjectDriver getDriver() {
		return driver;
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public void commit(List<PhysicalDirection> rowsDirections,
			String[] fieldName, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException, FreeingResourcesException {
		((ObjectReadWriteDriver) driver).write(modifiedSource);
		try {
			driver.stop();
		} catch (DriverException e) {
			throw new FreeingResourcesException(e);
		}
	}
}