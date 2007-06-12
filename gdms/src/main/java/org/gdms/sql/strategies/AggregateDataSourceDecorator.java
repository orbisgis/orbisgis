package org.gdms.sql.strategies;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * @author Fernando Gonzalez Cortes
 */
public class AggregateDataSourceDecorator extends AbstractSecondaryDataSource {

	private Value[] values;

	private String[] names;

	/**
	 * @param aggregateds
	 */
	public AggregateDataSourceDecorator(Value[] aggregateds) {
		this.values = aggregateds;
		names = new String[aggregateds.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "expr" + i;
		}
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return values.length;
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				return names[fieldId];
			}

			public Type getFieldType(int fieldId) throws DriverException {
				try {
					return TypeFactory.createType(values[fieldId].getType());
				} catch (InvalidTypeException e) {
					throw new DriverException("Bug in the driver: invalid type");
				}
			}

			public int getFieldCount() throws DriverException {
				return names.length;
			}

		};
	}

	public boolean isOpen() {
		return true;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return values[fieldId];
	}

	public long getRowCount() throws DriverException {
		return 1;
	}

	public void cancel() throws DriverException, AlreadyClosedException {
	}

	public void open() throws DriverException {
	}
}