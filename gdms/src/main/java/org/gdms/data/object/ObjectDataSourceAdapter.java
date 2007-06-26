package org.gdms.data.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.Commiter;
import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ObjectReadWriteDriver;

public class ObjectDataSourceAdapter extends DriverDataSource implements
		Commiter {

	private ObjectDriver driver;

	public ObjectDataSourceAdapter(String name, ObjectDriver driver) {
		super(name);
		this.driver = driver;
	}

	public void open() throws DriverException {
		driver.start();
	}

	public void commit() throws DriverException, FreeingResourcesException {
		driver.stop();
		((ObjectReadWriteDriver) driver).write(this);
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		driver.stop();
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.open();
		((ObjectReadWriteDriver) driver).write(ds);
		ds.cancel();
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