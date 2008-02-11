package org.gdms.sql.strategies;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class DeleteOperator extends AbstractOperator implements Operator {

	public ObjectDriver getResultContents() throws ExecutionException {
		return null;
	}

	public Metadata getResultMetadata() throws DriverException {
		return new DefaultMetadata();
	}

}
