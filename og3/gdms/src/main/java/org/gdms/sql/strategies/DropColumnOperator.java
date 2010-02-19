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

public class DropColumnOperator extends AbstractOperator implements Operator {

	private String columnName;
	private String tableName;
	private DataSourceFactory dsf;

	public DropColumnOperator(DataSourceFactory dsf, String tableName,
			String columnName) {
		this.columnName = columnName;
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
			ds.removeField(ds.getFieldIndexByName(columnName));
			ds.commit();
			ds.close();
		} catch (DriverLoadException e) {
			throw new ExecutionException("Cannot add the column", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot add the column", e);
		} catch (AlreadyClosedException e) {
			throw new ExecutionException("Cannot add the column", e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException("Cannot add the column", e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot edit the table", e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException("Cannot edit the table", e);
		}

		return null;
	}

	private Type getTypeFromSQLEngineConstants(String type) {

		if (type.equalsIgnoreCase("text")) {
			return TypeFactory.createType(Type.STRING);
		}

		else if (type.equalsIgnoreCase("numeric")) {

			return TypeFactory.createType(Type.DOUBLE);
		}

		else if (type.equalsIgnoreCase("integer")) {

			return TypeFactory.createType(Type.INT);
		}

		else {
			throw new UnsupportedOperationException("Unsupported type");
		}
	}

	@Override
	public Metadata getResultMetadata() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

}
