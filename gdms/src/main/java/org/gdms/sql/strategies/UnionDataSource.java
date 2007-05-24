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
 * DataSource que hace la union de dos datasources
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class UnionDataSource extends AbstractSecondaryDataSource {
	private DataSource dataSource1;

	private DataSource dataSource2;

	/**
	 * Creates a new UnionDataSource object.
	 * 
	 * @param ds1
	 *            Primera tabla de la union
	 * @param ds2
	 *            Segunda tabla de la union
	 */
	public UnionDataSource(DataSource ds1, DataSource ds2) {
		dataSource1 = ds1;
		dataSource2 = ds2;
	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void beginTrans() throws DriverException {
		dataSource1.beginTrans();

		try {
			dataSource2.beginTrans();
		} catch (DriverException e) {
			dataSource1.rollBackTrans();

			throw e;
		}
	}

	/**
	 * @see org.gdms.data.DataSource#close(Connection)
	 */
	public void rollBackTrans() throws DriverException {
		dataSource1.rollBackTrans();
		dataSource2.rollBackTrans();
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource1.getFieldIndexByName(fieldName);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		long tamTabla1 = dataSource1.getRowCount();

		if (rowIndex < tamTabla1) {
			return dataSource1.getFieldValue(rowIndex, fieldId);
		} else {
			return dataSource2.getFieldValue(rowIndex - tamTabla1, fieldId);
		}
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return dataSource1.getRowCount() + dataSource2.getRowCount();
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] {
				dataSource1.getMemento(), dataSource2.getMemento() }, getSQL());
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return dataSource1.getDataSourceMetadata();
	}

	public boolean isOpen() {
		return dataSource1.isOpen();
	}

	@Override
	public DataSource cloneDataSource() {
		return new UnionDataSource(dataSource1, dataSource2);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}
}