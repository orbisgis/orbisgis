package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.AbstractSecondaryDataSource;

class SumDataSource extends AbstractSecondaryDataSource {

	private double sum;

	public SumDataSource(double n) {
		sum = n;
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) {
		if (fieldName.equals("sum"))
			return 0;
		else
			return -1;
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return ValueFactory.createValue(sum);
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return 1;
	}

	/**
	 * @see org.gdms.data.DataSource#beginTrans()
	 */
	public void beginTrans() throws DriverException {
	}

	/**
	 * @see org.gdms.data.DataSource#rollBackTrans()
	 */
	public void rollBackTrans() throws DriverException {
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[0], getSQL());
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return new Metadata() {

			public Boolean isReadOnly(int fieldId) throws DriverException {
				return true;
			}

			public String[] getPrimaryKey() throws DriverException {
				return new String[0];
			}

			public String getFieldName(int fieldId) throws DriverException {
				return "sum";
			}

			public int getFieldType(int fieldId) throws DriverException {
				return Value.INT;
			}

			public int getFieldCount() throws DriverException {
				return 1;
			}

		};
	}

	public boolean isOpen() {
		return true;
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}

	@Override
	public DataSource cloneDataSource() {
		return new SumDataSource(sum);
	}
}