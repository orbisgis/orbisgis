package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.indexes.IndexResolver;
import org.gdms.driver.DriverException;

public abstract class ScalarProductDataSource extends AbstractSecondaryDataSource {

	protected DataSource[] tables;
	protected long tablesArity;

	/**
	 * Dado un �ndice de campo en la tabla producto, devuelve el �ndice en el
	 * array de tablas de la tabla operando que contiene dicho campo
	 *
	 * @param fieldId
	 *            �ndice del campo en la tabla producto
	 *
	 * @return �ndice de la tabla en el array de tablas
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 */
	protected int getTableIndexByFieldId(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return table;
	}

	/**
	 * Dado un �ndice de campo en la tabla producto, devuelve el �ndice en la
	 * tabla operando a la cual pertenence el campo
	 *
	 * @param fieldId
	 *            �ndice en la tabla producto
	 *
	 * @return �ndice en la tabla operando
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 */
	protected int getFieldIndex(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return fieldId;
	}

	/**
	 * @see org.gdbms.data.DataSource#open(java.io.File)
	 */
	public void open() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			try {
				IndexResolver.useIndexes = false;
				tables[i].open();
			} catch (DriverException e) {
				for (int j = 0; j < i; j++) {
					tables[i].cancel();
				}

				throw e;
			}
		}

		tablesArity = 1;

		for (int i = 0; i < tables.length; i++) {
			tablesArity *= tables[i].getRowCount();
		}
	}

	/**
	 * @see org.gdbms.data.DataSource#close(Connection)
	 */
	public void cancel() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			tables[i].cancel();
		}
	}

}
