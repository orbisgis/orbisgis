package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class CreateOperator extends AbstractOperator implements Operator {

	private String tableName;
	private DataSourceFactory dsf;

	public CreateOperator(DataSourceFactory dsf, String tableName) {
		this.tableName = tableName;
		this.dsf = dsf;
	}

	public ObjectDriver getResultContents() throws ExecutionException {
		DataSource ds;
		try {
			ds = dsf.getDataSource(getOperator(0).getResult());
			dsf.saveContents(tableName, ds);
			return null;
		} catch (DriverException e1) {
			throw new ExecutionException("Cannot create table:" + tableName, e1);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return new DefaultMetadata();
	}

	/**
	 * Validates that the source to create exists in the source manager
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateTableReferences()
	 */
	@Override
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		if (!dsf.exists(tableName)) {
			throw new SemanticException(tableName + " does not exist");
		}

		super.validateTableReferences();
	}

}
