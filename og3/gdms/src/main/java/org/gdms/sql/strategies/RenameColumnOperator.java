package org.gdms.sql.strategies;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.progress.IProgressMonitor;

public class RenameColumnOperator extends AbstractOperator implements Operator {

	private String columnName;
	private String columnNewName;
	private String tableName;
	private DataSourceFactory dsf;

	public RenameColumnOperator(DataSourceFactory dsf, String tableName,
			String columnName, String columnNewName) {
		this.columnName = columnName;
		this.columnNewName = columnNewName;
		this.tableName = tableName;
		this.dsf = dsf;
	}

	@Override
	protected ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {

		DataSource ds;
		try {
			ds = getDataSourceFactory().getDataSource(tableName);

			if (!ds.isOpen()) {
				ds.open();
			}
			ds.setFieldName(ds.getFieldIndexByName(columnName), columnNewName);
			ds.commit();
			ds.close();
		} catch (DriverLoadException e) {
			throw new ExecutionException("Cannot rename the column", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot rename the column", e);
		} catch (AlreadyClosedException e) {
			throw new ExecutionException("Cannot rename the column", e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException("Cannot rename the column", e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot edit the table", e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException("Cannot edit the table", e);
		}

		return null;
	}

	@Override
	public Metadata getResultMetadata() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

}
