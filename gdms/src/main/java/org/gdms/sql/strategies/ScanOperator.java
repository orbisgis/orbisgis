package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.IProgressMonitor;

public class ScanOperator extends AbstractOperator {

	private String tableName;
	private String tableAlias;
	private DataSourceFactory dsf;
	private Metadata metadata = null;
	private DataSource dataSource;

	public ScanOperator(DataSourceFactory dsf, String tableName,
			String tableAlias) {
		this.tableName = tableName;
		this.tableAlias = tableAlias;
		this.dsf = dsf;
	}

	/**
	 * The result metadata of a scan operator is the metadata of the source it
	 * accesses
	 *
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		if (metadata == null) {
			try {
				DataSource ds = dsf.getDataSource(tableName);
				ds.open();
				Metadata metadata = ds.getMetadata();
				this.metadata = new DefaultMetadata(metadata);
				ds.cancel();
			} catch (DriverLoadException e) {
				throw new DriverException(e);
			} catch (NoSuchTableException e) {
				throw new DriverException(e);
			} catch (DataSourceCreationException e) {
				throw new DriverException(e);
			}
		}

		return metadata;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm) throws ExecutionException {
		try {
			dataSource = dsf.getDataSource(tableName);
			dataSource.open();
			return new DataSourceDriver(dataSource);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Bug!", e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException("Bug!", e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot access "
					+ "the source in the SQL: " + tableName, e);
		}
	}

	@Override
	public void operationFinished() throws ExecutionException {
		try {
			dataSource.cancel();
		} catch (DriverException e) {
			throw new ExecutionException("Problem releasing sources: "
					+ tableName, e);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * Checks the referenced source exists
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateTableReferences()
	 */
	@Override
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		if (!dsf.exists(tableName)) {
			throw new NoSuchTableException(tableName);
		}
		super.validateTableReferences();
	}

	@Override
	public String[] getReferencedTables() {
		return new String[] { tableName };
	}
}
