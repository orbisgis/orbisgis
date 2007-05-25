package org.gdms;

import org.gdms.data.InternalDataSource;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import junit.framework.TestCase;

public class BaseTest extends TestCase {

	/**
	 * Gets the contents of the InternalDataSource
	 *
	 * @param ds
	 * @return
	 * @throws DriverException
	 */
	public Value[][] getDataSourceContents(InternalDataSource ds) throws DriverException {
		Value[][] ret = new Value[(int) ds.getRowCount()][ds
				.getDataSourceMetadata().getFieldCount()];
		for (int i = 0; i < ret.length; i++) {
			for (int j = 0; j < ret[i].length; j++) {
				ret[i][j] = ds.getFieldValue(i, j);
			}
		}
	
		return ret;
	}

	/**
	 * Compares the two values for testing purposes. This means that two null
	 * values are always equal though its equals method returns always false
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean equals(Value v1, Value v2) {
		if (v1 instanceof NullValue) {
			return v2 instanceof NullValue;
		} else {
			try {
				return ((BooleanValue) v1.equals(v2)).getValue();
			} catch (IncompatibleTypesException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Compares the two arrays of values for testing purposes. This means that
	 * two null values are always equal though its equals method returns always
	 * false
	 *
	 * @param row1
	 * @param row2
	 * @return
	 */
	public boolean equals(Value[] row1, Value[] row2) {
		for (int i = 0; i < row2.length; i++) {
			if (!equals(row1[i], row2[i])) {
				return false;
			}
		}
	
		return true;
	}

	/**
	 * Compares the two arrays of values for testing purposes. This means that
	 * two null values are always equal though its equals method returns always
	 * false
	 *
	 * @param content1
	 * @param content2
	 * @return
	 */
	public boolean equals(Value[][] content1, Value[][] content2) {
		for (int i = 0; i < content1.length; i++) {
			if (!equals(content1[i], content2[i])) {
				return false;
			}
		}
	
		return true;
	}

}
