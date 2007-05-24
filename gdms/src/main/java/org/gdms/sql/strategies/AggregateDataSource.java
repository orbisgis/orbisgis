package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * @author Fernando Gonzalez Cortes
 */
public class AggregateDataSource extends AbstractSecondaryDataSource {

	private Value[] values;

	private String[] names;

	/**
	 * @param aggregateds
	 */
	public AggregateDataSource(Value[] aggregateds) {
		this.values = aggregateds;
		names = new String[aggregateds.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "expr" + i;
		}
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
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return values[fieldId];
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return values.length;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return 1;
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
				return names[fieldId];
			}

			public int getFieldType(int fieldId) throws DriverException {
				return values[fieldId].getType();
			}

			public int getFieldCount() throws DriverException {
				return names.length;
			}

		};
	}

	public boolean isOpen() {
		return true;
	}

	@Override
	public DataSource cloneDataSource() {
		return new AggregateDataSource(values);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}
}