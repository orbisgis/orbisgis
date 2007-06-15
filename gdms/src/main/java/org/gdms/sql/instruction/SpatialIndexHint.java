package org.gdms.sql.instruction;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.SpatialIterator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class SpatialIndexHint extends IndexHint {

	public SpatialIndexHint(String fieldTable, String fieldName, Expression filteringExpression) {
		super(fieldTable, fieldName, filteringExpression);
	}

	@Override
	public Iterator<PhysicalDirection> getRowIterator(DataSource ds, Value filteringValue) throws DriverException {
		if (ds.getAlias().equals(super.table)) {
			return null;//new SpatialIterator(ds, filteringValue, field);
		} else {
			return null;
		}
	}

}
