package org.gdms.sql.strategies;

import org.gdms.driver.DriverException;

public interface OptimizationInfo {

	/**
	 * returns the ScanOperator at the end of this branch. If there are several
	 * operators it returns null
	 *
	 * @return
	 */
	ScanOperator getScanOperator();

	long getRowCount() throws DriverException;
}
