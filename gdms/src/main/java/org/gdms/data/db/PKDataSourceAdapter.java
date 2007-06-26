package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

/**
 * This class simulates a DataSource with primary key that uses the getPK method
 * as a pk field. This class only wraps DataSource without primary key and so
 * the primary key is a single integer value
 *
 * @author Fernando Gonzalez Cortes
 *
 */
class PKDataSourceAdapter extends AbstractDataSourceDecorator implements
		DataSource {

	private DefaultMetadata met;

	public PKDataSourceAdapter(DataSource ds) {
		super(ds);
	}

	public Metadata getMetadata() throws DriverException {
		if (met == null) {
			met = new DefaultMetadata(getDataSource().getMetadata());

			try {
				met.addField(0, getPKName(), TypeFactory.createType(Type.INT)
						.getTypeCode(),
						new Constraint[] { new PrimaryKeyConstraint() });
			} catch (InvalidTypeException e) {
				throw new DriverException(e);
			}
		}
		return met;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (fieldId == 0) {
			return ((ValueCollection) getDataSource().getPK((int) rowIndex))
					.getValues()[0];
		} else {
			return getDataSource().getFieldValue(rowIndex, fieldId - 1);
		}
	}

	public long getRowCount() throws DriverException {
		return getDataSource().getRowCount();
	}

	private String getPKName() throws DriverException {
		int i = 0;
		String pkName = "pk_" + i;
		while (getDataSource().getFieldIndexByName(pkName) != -1) {
			i++;
			pkName = "pk_" + i;
		}
		return pkName;
	}
}
