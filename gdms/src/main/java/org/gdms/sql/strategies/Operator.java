package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;

public interface Operator {

	public ObjectDriver getResult(IProgressMonitor pm)
			throws ExecutionException;

	public void addChild(Operator operator);

	public Operator getOperator(int i);

	public int getOperatorCount();

	public Metadata getResultMetadata() throws DriverException;

	/**
	 * Expands stars and sets a field context to evaluate field types
	 *
	 * @throws SemanticException
	 * @throws DriverException
	 */
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

	/**
	 * Method to perform the necessary initialization. Typically open the
	 * DataSources
	 *
	 * @throws ExecutionException
	 */
	public void initialize() throws DriverException;

	/**
	 * Gets the information necessary to perform some optimizations
	 *
	 * @return
	 */
	public OptimizationInfo getOptimizationInfo();

	/**
	 * Method to free any resource. Typically close the DataSources
	 *
	 * @throws ExecutionException
	 */
	public void operationFinished() throws DriverException;

	/**
	 * Sets the scan mode of the branch. If there are bifurcations in the branch
	 * this call has no effect. if indexQueries has no elements the scan mode is
	 * 'table-scan', otherwise an index-scan using all the queries is used.
	 *
	 * @param indexQueries
	 */
	public void setScanMode(IndexQuery[] indexQueries);

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

	/**
	 * Resolves the references between the fields and the sources they refer
	 *
	 * @throws SemanticException
	 * @throws DriverException
	 */
	public void resolveFieldSourceReferences(SourceManager sm) throws DriverException,
			SemanticException;
}
