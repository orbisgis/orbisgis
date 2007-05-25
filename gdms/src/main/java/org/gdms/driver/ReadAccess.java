package org.gdms.driver;

import org.gdms.data.values.Value;


/**
 * Interface that defines the read methods in gdms
 *
 * @author Fernando Gonzalez Cortes
 */
public interface ReadAccess {

	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	public static final int TIME = 3;

	/**
	 * Obtiene el valor que se encuentra en la fila y columna indicada
	 *
	 * @param rowIndex fila
	 * @param fieldId columna
	 *
	 * @return subclase de Value con el valor del origen de datos. Never null (use
     * ValueFactory.createNullValue() instead)
	 *
	 * @throws DriverException Si se produce un error accediendo al InternalDataSource
	 */
	public abstract Value getFieldValue(long rowIndex, int fieldId)
		throws DriverException;

	/**
	 * Get the number of elements in the source of data
	 *
	 * @return
	 *
	 * @throws DriverException If some error happens accessing the data source
	 */
	public abstract long getRowCount() throws DriverException;

	/**
	 * returns the scope of the data source.
	 *
	 * @param dimension
	 *            Currently X, Y, Z or can be anything that a driver
	 *            implementation is waiting for, for example: TIME
	 * @param fieldName
	 *            Name of the field. Can be null if the
	 * @return An array two elements indicating the bounds of the dimension. Can
	 *         return null if the source is not bounded
	 * @throws DriverException
	 */
	Number[] getScope(int dimension, String fieldName) throws DriverException;
}
