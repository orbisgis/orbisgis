package org.gdms.data.file;

import java.io.File;
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
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileReadWriteDriver;

/**
 * Adapta la interfaz FileDriver a la interfaz DataSource
 *
 * @author Fernando Gonzalez Cortes
 */
public class FileDataSourceAdapter extends DriverDataSource implements Commiter {

	private FileDriver driver;

	private File file;

	public FileDataSourceAdapter(String name, File file,
			FileDriver driver) {
		super(name);
		this.driver = driver;
		this.file = file;
	}

	public FileDriver getDriver() {
		return driver;
	}

	public void commit() throws DriverException, FreeingResourcesException {
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