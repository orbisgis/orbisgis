package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.progress.IProgressMonitor;

public class RenameTableOperator extends AbstractOperator implements Operator {

	private String tableNewName;
	private String tableName;
	private DataSourceFactory dsf;

	public RenameTableOperator(DataSourceFactory dsf, String tableName,
			String tableNewName) {
		this.tableNewName = tableNewName;
		this.tableName = tableName;
		this.dsf = dsf;
	}

	@Override
	protected ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {

		dsf.getSourceManager().rename(tableName, tableNewName);
		return null;
	}


	@Override
	public Metadata getResultMetadata() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

}
