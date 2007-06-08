package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

class SumDataSourceDecorator extends AbstractSecondaryDataSource {

	private double sum;

	public SumDataSourceDecorator(double n) {
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
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[0], getSQL());
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
				return "sum";
			}

			public Type getFieldType(int fieldId) throws DriverException {
				try {
					return TypeFactory.createType(Type.INT);
				} catch (InvalidTypeException e) {
					throw new DriverException("Invalid type");
				}
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
		return ValueFactory.createValue(sum);
	}

	@Override
	public DataSource cloneDataSource() {
		DataSource ret = new SumDataSourceDecorator(sum);
		ret.setDataSourceFactory(getDataSourceFactory());

		return ret;
	}

	public long getOriginalRowCount() throws DriverException {
		return 1;
	}
}