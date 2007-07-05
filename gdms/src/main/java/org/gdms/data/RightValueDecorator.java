package org.gdms.data;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;

public class RightValueDecorator extends AbstractDataSourceDecorator implements
		DataSource {

	public RightValueDecorator(DataSource internalDataSource) {
		super(internalDataSource);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		final Value value = getDataSource().getFieldValue(rowIndex, fieldId);

		if (null == value) {
			return ValueFactory.createNullValue();
		} else {
			if ((value instanceof GeometryValue)
					&& ((GeometryValue) value).getGeom().isEmpty()) {
				return ValueFactory.createNullValue();
			} else {
				return value;
			}
		}
	}
}