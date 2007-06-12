package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.Row;

public interface DataSourceIndex {

	/**
	 * Gets an iterator that will iterate through the filtered rows in the
	 * DataSource that was used in the buildIndex method
	 *
	 *
	 * @param indexQuery
	 * @return
	 */
	public Iterator<Row> getIterator(IndexQuery indexQuery) throws DriverException;

	/**
	 * Gets an iterator over all the rows in the index. It is very usefull for
	 * testing purposes
	 *
	 * @return
	 * @throws DriverException
	 */
	public Iterator<Row> getAll() throws DriverException;

	/**
	 * To update the index.
	 *
	 * @param index
	 * @throws DriverException
	 */
	public void beforeDeletingRow(long index) throws DriverException;

	/**
	 * To update the index
	 * @throws DriverException
	 */
	public void afterInsertingRow(long index, Value[] row) throws DriverException;

	/**
	 * To update the index
	 */
	public void beforeSettingFieldValue(Value oldGeometry, Value newGeometry,
			long rowIndex);

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
}
