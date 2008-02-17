package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public interface Operator {

	public ObjectDriver getResult() throws ExecutionException;

	public void addChild(Operator operator);

	public Operator getOperator(int i);

	public int getOperatorCount();

	public Metadata getResultMetadata() throws DriverException;

	public void prepareValidation() throws SemanticException, DriverException;

	public void validateFieldReferences() throws SemanticException,
			DriverException;

	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException;

	public void validateExpressionTypes() throws SemanticException,
			DriverException;

	public String[] getReferencedTables();

	public void validateFunctionReferences() throws DriverException,
			SemanticException;

	public void setDataSourceFactory(DataSourceFactory dsf);

	public void operationFinished() throws ExecutionException;

	/**
	 * @return true if the operator has been validated by a preprocessor
	 */
	public boolean isValidated();

	public void setValidated(boolean validated);

}
