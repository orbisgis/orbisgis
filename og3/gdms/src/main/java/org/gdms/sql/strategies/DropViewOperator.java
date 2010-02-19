package org.gdms.sql.strategies;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.progress.IProgressMonitor;

public class DropViewOperator extends AbstractOperator implements Operator {

	protected ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		for (int i = 0; i < getOperatorCount(); i++) {
			String[] tables = getOperator(i).getReferencedTables();
			for (String tableName : tables) {
				getDataSourceFactory().getSourceManager().remove(tableName);
			}
		}
		return null;
	}

	public Metadata getResultMetadata() throws DriverException {

		return null;
	}

}
