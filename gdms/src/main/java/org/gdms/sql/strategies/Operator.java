package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.IProgressMonitor;

public interface Operator {

	public ObjectDriver getResult(IProgressMonitor pm)
			throws ExecutionException;

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

	/**
	 * Sets the maximum number of rows this instruction can return. If an
	 * operator does not modify the number of rows it should delegate on the
	 * children, otherwise it should keep the limit without delegating on the
	 * children
	 *
	 * @param limit
	 */
	public void setLimit(int limit);

	/**
	 * Sets the offset of this instruction result. If an operator does not
	 * modify the rows it should delegate on the children, otherwise it should
	 * keep the offset without delegating on the children
	 *
	 * @param offset
	 */
	public void setOffset(int offset);

	/**
	 * Gets the operators that match the filter that are located in the sub tree
	 * which root is this operator
	 *
	 * @param filter
	 * @return
	 */
	Operator[] getOperators(OperatorFilter filter);
}
