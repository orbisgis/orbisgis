package org.gdms.sql.strategies;

import java.sql.Connection;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Clase que representa el producto cartesiano de dos o m�s tablas. El
 * almacenamiento de dicha tabla se realiza en las propias tablas sobre las que
 * se opera, haciendo los c�lculos en cada acceso para saber en qu� tabla y en
 * qu� posici�n de la tabla se encuentra el dato buscado
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class PDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource[] tables;

	private long tablesArity;

	/**
	 * Creates a new PDataSourceDecorator object.
	 *
	 * @param tables
	 *            Array de tablas que forman el producto
	 * @throws DriverException
	 */
	public PDataSourceDecorator(DataSource[] tables) {
		this.tables = tables;
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
	private int getFieldIndex(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getDataSourceMetadata().getFieldCount()) {
			fieldId -= tables[table].getDataSourceMetadata().getFieldCount();
			table++;
		}

		return fieldId;
	}

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
	private int getTableIndexByFieldId(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getDataSourceMetadata().getFieldCount()) {
			fieldId -= tables[table].getDataSourceMetadata().getFieldCount();
			table++;
		}

		return table;
	}

	/**
	 * Devuelve la fila de la tabla operando con �ndice tableIndex que contiene
	 * la informaci�n de la fila rowIndex en la tabla producto
	 *
	 * @param rowIndex
	 *            fila en la tabla producto a la que se quiere acceder
	 * @param tableIndex
	 *            �ndice de la tabla
	 *
	 * @return fila en la tabla operando de �ndice tableIndex que se quiere
	 *         acceder
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 * @throws ArrayIndexOutOfBoundsException
	 *             Si la fila que se pide (rowIndex) supera el n�mero de filas
	 *             de la tabla producto
	 */
	private long getTableRowIndexByTablePosition(long rowIndex, int tableIndex)
			throws DriverException {
		if (rowIndex >= tablesArity) {
			throw new ArrayIndexOutOfBoundsException();
		}

		int arity = 1;

		for (int i = tableIndex + 1; i < tables.length; i++) {
			arity *= tables[i].getRowCount();
		}

		long selfArity = tables[tableIndex].getRowCount();

		return (rowIndex / arity) % selfArity;
	}

	/**
	 * @see org.gdbms.data.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		int ret = 0;

		for (int i = 0; i < tables.length; i++) {
			ret += tables[i].getDataSourceMetadata().getFieldCount();
		}

		return ret;
	}

	/**
	 * @see org.gdbms.data.DataSource#open(java.io.File)
	 */
	public void open() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			try {
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

		super.open();
	}

	/**
	 * @see org.gdbms.data.DataSource#close(Connection)
	 */
	public void cancel() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			tables[i].cancel();
		}
		super.cancel();
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		Memento[] mementos = new Memento[tables.length];

		for (int i = 0; i < mementos.length; i++) {
			mementos[i] = tables[i].getMemento();
		}

		return new OperationLayerMemento(getName(), mementos, getSQL());
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return new Metadata() {

			public Boolean isReadOnly(int fieldId) throws DriverException {
				return true;
			}

			public String[] getPrimaryKey() throws DriverException {
				return new String[0];
			}

			public String getFieldName(int fieldId) throws DriverException {
				return tables[getTableIndexByFieldId(fieldId)]
						.getDataSourceMetadata().getFieldName(
								getFieldIndex(fieldId));
			}

			public int getFieldType(int fieldId) throws DriverException {
				int table = getTableIndexByFieldId(fieldId);

				return tables[table].getDataSourceMetadata().getFieldType(
						getFieldIndex(fieldId));
			}

			public int getFieldCount() throws DriverException {
				int ret = 0;

				for (int i = 0; i < tables.length; i++) {
					ret += tables[i].getDataSourceMetadata().getFieldCount();
				}

				return ret;
			}

		};
	}

	public boolean isOpen() {
		return tables[0].isOpen();
	}

	@Override
	public DataSource cloneDataSource() {
		DataSource[] newTables = new DataSource[tables.length];
		for (int i = 0; i < tables.length; i++) {
			newTables[i] = super.clone(tables[i]);
		}

		PDataSourceDecorator ret = new PDataSourceDecorator(newTables);
		ret.tablesArity = this.tablesArity;
		ret.setDataSourceFactory(getDataSourceFactory());

		return ret;
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int tableIndex = getTableIndexByFieldId(fieldId);

		return tables[tableIndex].getFieldValue(
				getTableRowIndexByTablePosition(rowIndex, tableIndex),
				getFieldIndex(fieldId));
	}

	public long getOriginalRowCount() throws DriverException {
		return tablesArity;
	}
}