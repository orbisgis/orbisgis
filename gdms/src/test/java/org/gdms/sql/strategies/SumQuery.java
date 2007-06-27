package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;

/**
 */
public class SumQuery implements CustomQuery {

	/**
	 * @throws QueryException
	 * @see org.gdms.sql.customQuery.CustomQuery#evaluate(DataSourceFactory,
	 *      org.gdms.data.DataSource[], Value[])
	 */
	public DataSource evaluate(DataSourceFactory dsf,
			DataSource[] tables, Value[] values) throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException("SUM only operates on one table");
		if (values.length != 1)
			throw new ExecutionException("SUM only operates with one value");

		String fieldName = values[0].toString();
		double res = 0;
		try {

			tables[0].open();

			int fieldIndex = tables[0].getFieldIndexByName(fieldName);
			if (fieldIndex == -1)
				throw new RuntimeException(
						"we found the field name of the expression but could not find the field index?");

			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, fieldIndex);
				if (v instanceof NumericValue) {
					res += ((NumericValue) v).doubleValue();
				} else {
					throw new ExecutionException(
							"SUM only operates with numeric fields");
				}
			}

			tables[0].cancel();
		} catch (DriverException e) {
			throw new ExecutionException("Error reading data", e);
		}

		return new SumDataSourceDecorator(res);
	}

	/**
	 * @see org.gdms.sql.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "SUMQUERY";
	}
}
