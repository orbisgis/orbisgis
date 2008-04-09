package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ScanOptimizationInfo implements OptimizationInfo {

	private DataSource dataSource;
	private ScanOperator scanOperator;

	public ScanOptimizationInfo(ScanOperator scanOperator, DataSource dataSource) {
		this.dataSource = dataSource;
		this.scanOperator = scanOperator;
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

	public ScanOperator getScanOperator() {
		return scanOperator;
	}

}
