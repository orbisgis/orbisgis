package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public interface DataSourceIndex {

	/**
	 * Gets an iterator that will iterate through the filtered rows in the
	 * DataSource that was used in the buildIndex method
	 *
	 *
	 * @param indexQuery
	 * @return
	 */
	public Iterator<PhysicalDirection> getIterator(IndexQuery indexQuery)
			throws IndexException;

	/**
	 * Gets an iterator over all the rows in the index. It is very usefull for
	 * testing purposes
	 *
	 * @return
	 * @throws DriverException
	 */
	public Iterator<PhysicalDirection> getAll() throws DriverException;

	/**
	 * To update the index.
	 *
	 * @param direction
	 * @throws DriverException
	 */
	public void deleteRow(PhysicalDirection direction) throws DriverException;

	/**
	 * To update the index
	 *
	 * @throws DriverException
	 */
	public void insertRow(PhysicalDirection direction, Value[] row)
			throws DriverException;

	/**
	 * To update the index
	 */
	public void setFieldValue(Value oldGeometry, Value newGeometry,
			PhysicalDirection direction);

	/**
	 * Returns the identification of the index
	 *
	 * @return
	 */
	public String getId();

	/**
	 * Gets the field this index is built on
	 *
	 * @return
	 */
	public String getFieldName();

	/**
	 * clones this index
	 *
	 * @param ds
	 *
	 * @return
	 * @throws DriverException
	 */
	public DataSourceIndex cloneIndex(DataSource ds) throws IndexException;

	/**
	 * Indexes the specified field of the specified source
	 *
	 * @param ds
	 * @param fieldName
	 * @throws DriverException
	 * @throws IncompatibleTypesException
	 *             If the index cannot be built with that field type
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	public void buildIndex(DataSourceFactory dsf, DataSource ds,
			String fieldName) throws IndexException;

	/**
	 * Loads the index information from the file
	 *
	 * @param file
	 * @throws IOException
	 */
	public void load(File file) throws IndexException;

	/**
	 * Gets a new empty instance of the index
	 *
	 * @return
	 */
	public DataSourceIndex getNewInstance();

	/**
	 * Stores the information of this index at disk
	 *
	 * @param file
	 * @throws IndexException
	 */
	public void save(File file) throws IndexException;

	/**
	 * Assigns the DataSource this index is based on
	 *
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource);

}
