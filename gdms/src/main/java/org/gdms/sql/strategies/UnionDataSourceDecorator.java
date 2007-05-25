package org.gdms.sql.strategies;

import java.sql.Connection;

import org.gdms.data.InternalDataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * InternalDataSource que hace la union de dos datasources
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class UnionDataSourceDecorator extends AbstractSecondaryDataSource {
	private InternalDataSource dataSource1;

	private InternalDataSource dataSource2;

	/**
	 * Creates a new UnionDataSourceDecorator object.
	 *
	 * @param ds1
	 *            Primera tabla de la union
	 * @param ds2
	 *            Segunda tabla de la union
	 */
	public UnionDataSourceDecorator(InternalDataSource ds1, InternalDataSource ds2) {
		dataSource1 = ds1;
		dataSource2 = ds2;
	}

	/**
	 * @see org.gdms.data.InternalDataSource#open()
	 */
	public void open() throws DriverException {
		dataSource1.open();

		try {
			dataSource2.open();
		} catch (DriverException e) {
			dataSource1.cancel();

			throw e;
		}

		super.open();
	}

	/**
	 * @see org.gdms.data.InternalDataSource#close(Connection)
	 */
	public void cancel() throws DriverException {
		dataSource1.cancel();
		dataSource2.cancel();
		super.cancel();
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource1.getFieldIndexByName(fieldName);
	}

	/**
	 * @see org.gdms.data.InternalDataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] {
				dataSource1.getMemento(), dataSource2.getMemento() }, getSQL());
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return dataSource1.getDataSourceMetadata();
	}

	public boolean isOpen() {
		return dataSource1.isOpen();
	}

	@Override
	public InternalDataSource cloneDataSource() {
		return new UnionDataSourceDecorator(dataSource1, dataSource2);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		long tamTabla1 = dataSource1.getRowCount();

		if (rowIndex < tamTabla1) {
			return dataSource1.getFieldValue(rowIndex, fieldId);
		} else {
			return dataSource2.getFieldValue(rowIndex - tamTabla1, fieldId);
		}
	}

	public long getOriginalRowCount() throws DriverException {
		return dataSource1.getRowCount() + dataSource2.getRowCount();
	}
}