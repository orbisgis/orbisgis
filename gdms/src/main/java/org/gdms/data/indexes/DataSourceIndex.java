package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

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
			throws DriverException;

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
	 * Sets the DataSource this index operates upon
	 *
	 * @param ds
	 */
	public void setDataSource(DataSource ds);
}
