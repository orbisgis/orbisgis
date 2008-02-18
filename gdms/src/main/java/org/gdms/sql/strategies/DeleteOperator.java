package org.gdms.sql.strategies;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.IProgressMonitor;

public class DeleteOperator extends AbstractOperator implements Operator {

	public ObjectDriver getResultContents(IProgressMonitor pm) throws ExecutionException {
		return null;
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

}
