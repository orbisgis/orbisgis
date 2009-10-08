/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.progress.IProgressMonitor;

public interface Operator {

	public ObjectDriver getResult(IProgressMonitor pm)
			throws ExecutionException;

	public void addChild(Operator operator);

	public Operator getOperator(int i);

	public int getOperatorCount();

	/**
	 * Gets the metadata of this operator result. Return null if this operator
	 * doesn't generate any result
	 *
	 * @return
	 * @throws DriverException
	 */
	public Metadata getResultMetadata() throws DriverException;

	/**
	 * Makes the specified field appear in the result provided by this operator
	 * and returns the field index in the metadata of the result. Return -1 if
	 * this operator cannot resolve the field reference.
	 *
	 * @param field
	 * @return
	 * @throws DriverException
	 *             If there is a problem accessing sources
	 * @throws UnsupportedOperationException
	 *             if invoked in operators that doesn't return any result
	 */
	int passFieldUp(Field field) throws DriverException,
			AmbiguousFieldReferenceException;

	/**
	 * Get the field indexes in the result metadata that were created just to
	 * execute the instruction but were not specified for the final result
	 *
	 * @return
	 * @throws DriverException
	 *             If there is a problem accessing sources
	 */
	int[] getInternalFields() throws DriverException;

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

	public void validateFunctionReferences() throws DriverException,
			SemanticException;

	public void setDataSourceFactory(DataSourceFactory dsf);

	/**
	 * Method to perform the necessary initialization. Typically open the
	 * DataSources. Operators can be executed several times so this method
	 * should clear any cached data
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
	 * Returns the tables referenced in the subtree which root is this operator
	 *
	 * @return
	 */
	public String[] getReferencedTables();

	/**
	 * Return the table name referenced in the tree branch which origin is this
	 * operator. If the tree is not a branch and accesses several tables it
	 * returns null
	 *
	 * @return
	 */
	public String getTableName();

	/**
	 * Return the table alias referenced in the tree branch which origin is this
	 * operator. If the tree is not a branch and accesses several tables it
	 * returns null
	 *
	 * @return
	 */
	public String getTableAlias();

	/**
	 * Gets the metadata of the branch accessing the specified table name or
	 * null if there is no branch that accesses the specified table
	 *
	 * @param tableName
	 * @return
	 * @throws DriverException
	 *             If there is a problem accessing source metadata
	 *
	 */
	public Metadata getBranchMetadata(String tableName) throws DriverException;

	/**
	 * Get the source containing this field
	 *
	 * @param sm
	 * @param field
	 * @return
	 */
	public String getFieldSource(SourceManager sm, Field field);

	/**
	 * Used to notify the root operator that it has no parent
	 *
	 * @param isRoot
	 */
	public void setRoot(boolean isRoot);

}
