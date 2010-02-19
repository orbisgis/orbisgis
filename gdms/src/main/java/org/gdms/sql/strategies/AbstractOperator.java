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

import java.util.ArrayList;

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

public abstract class AbstractOperator implements Operator {

	protected ArrayList<Operator> children = new ArrayList<Operator>();
	private ObjectDriver result;
	private DataSourceFactory dsf;
	private boolean removeInternal = false;

	private boolean validated = false;

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).setDataSourceFactory(dsf);
		}
	}

	protected DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	public void addChild(Operator operator) {
		children.add(operator);
	}

	public String toString() {
		String ret = this.getClass().getSimpleName() + "(";
		for (int i = 0; i < children.size(); i++) {
			ret = ret + children.get(i);
		}
		return ret + ")";
	}

	public void addChilds(Operator[] childOperators) {
		for (Operator operator : childOperators) {
			addChild(operator);
		}
	}

	public Operator getOperator(int i) {
		return children.get(i);
	}

	public int getOperatorCount() {
		return children.size();
	}

	public Operator[] getOperators(OperatorFilter filter) {
		return getOperators(this, filter);
	}

	protected Operator[] getOperators(Operator op, OperatorFilter operatorFilter) {
		ArrayList<Operator> ret = new ArrayList<Operator>();

		if (operatorFilter.accept(op)) {
			ret.add(op);
		}

		for (int i = 0; i < op.getOperatorCount(); i++) {
			Operator childOperator = op.getOperator(i);
			Operator[] ops = getOperators(childOperator, operatorFilter);
			for (Operator operator : ops) {
				ret.add(operator);
			}
		}

		return ret.toArray(new Operator[0]);
	}

	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFieldReferences();
		}
	}

	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateTableReferences();
		}
	}

	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateExpressionTypes();
		}
	}

	public void validateFunctionReferences() throws DriverException,
			SemanticException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFunctionReferences();
		}
	}

	public String[] getReferencedTables() {
		ArrayList<String> ret = new ArrayList<String>();
		for (Operator child : children) {
			String[] tables = child.getReferencedTables();
			for (String table : tables) {
				ret.add(table);
			}
		}

		return ret.toArray(new String[0]);
	}

	public void prepareValidation() throws SemanticException, DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).prepareValidation();
		}
	}

	public void setRoot(boolean isRoot) {
		this.removeInternal = isRoot;
	}

	public ObjectDriver getResult(IProgressMonitor pm)
			throws ExecutionException {
		if (result == null) {
			result = getResultContents(pm);
			if (removeInternal && (result != null)) {
				try {
					ArrayList<Integer> externalFields = new ArrayList<Integer>();
					int[] internal = getInternalFields();
					if (internal.length > 0) {
						Metadata metadata = result.getMetadata();
						for (int i = 0; i < metadata.getFieldCount(); i++) {
							boolean isInternal = false;
							for (int j = 0; j < internal.length; j++) {
								if (internal[j] == i) {
									isInternal = true;
								}
							}
							if (!isInternal) {
								externalFields.add(i);
							}
						}
						int[] columnMap = new int[externalFields.size()];
						for (int i = 0; i < columnMap.length; i++) {
							columnMap[i] = externalFields.get(i);
						}
						result = new ColumnMappedDriver(result, columnMap);
					}
				} catch (DriverException e) {
					throw new ExecutionException("Cannot execute query", e);
				}
			}
		}
		return result;
	}

	protected abstract ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException;

	protected Integer getFieldIndexByName(ObjectDriver source, String fieldName)
			throws DriverException, ExecutionException {
		for (int i = 0; i < source.getMetadata().getFieldCount(); i++) {
			if (fieldName.equals(source.getMetadata().getFieldName(i))) {
				return i;
			}
		}

		throw new ExecutionException("Field not found; " + fieldName);
	}

	public void operationFinished() throws DriverException {
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public void setLimit(int limit) {
		for (Operator child : children) {
			child.setLimit(limit);
		}
	}

	public void setOffset(int offset) {
		for (Operator child : children) {
			child.setOffset(offset);
		}
	}

	public void initialize() throws DriverException {
		result = null;
		for (Operator child : children) {
			child.initialize();
		}
	}

	public OptimizationInfo getOptimizationInfo() {
		if (getOperatorCount() == 1) {
			return getOperator(0).getOptimizationInfo();
		}
		return null;
	}

	public void setScanMode(IndexQuery[] indexQueries) {
		if (getOperatorCount() == 1) {
			getOperator(0).setScanMode(indexQueries);
		}
	}

	@Override
	public int passFieldUp(Field field) throws DriverException,
			AmbiguousFieldReferenceException {
		throw new UnsupportedOperationException(
				"This operator doesn't generate any result");
	}

	@Override
	public int[] getInternalFields() throws DriverException {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		int offset = 0;
		for (int i = 0; i < getOperatorCount(); i++) {
			Operator child = getOperator(i);
			int[] fields = child.getInternalFields();
			for (int field : fields) {
				ret.add(field);
			}
			offset += child.getResultMetadata().getFieldCount();
		}

		int[] retArray = new int[ret.size()];
		for (int i = 0; i < retArray.length; i++) {
			retArray[i] = ret.get(i);
		}
		return retArray;
	}

	@Override
	public String getTableName() {
		return getTableNameAndAlias()[0];
	}

	@Override
	public String getTableAlias() {
		return getTableNameAndAlias()[1];
	}

	private String[] getTableNameAndAlias() {
		if (getOperatorCount() != 1) {
			return new String[] { null, null };
		} else {
			return new String[] { getOperator(0).getTableName(),
					getOperator(0).getTableAlias() };
		}
	}

	@Override
	public Metadata getBranchMetadata(String tableName) throws DriverException {
		Metadata ret = null;
		for (int i = 0; i < getOperatorCount(); i++) {
			Metadata childMetadata = null;
			Operator op = getOperator(i);
			String childTableName = op.getTableName();
			if (childTableName != null) {
				if (tableName.equals(op.getTableAlias())
						|| tableName.equals(op.getTableName())) {
					childMetadata = op.getResultMetadata();
				}
			} else {
				childMetadata = op.getBranchMetadata(tableName);
			}

			if (childMetadata != null) {
				ret = childMetadata;
			}
		}

		return ret;
	}

	@Override
	public String getFieldSource(SourceManager sm, Field field) {
		for (int i = 0; i < getOperatorCount(); i++) {
			Operator child = getOperator(i);
			String fieldSource = child.getFieldSource(sm, field);
			if (fieldSource != null) {
				return fieldSource;
			}
		}

		return null;
	}
}
