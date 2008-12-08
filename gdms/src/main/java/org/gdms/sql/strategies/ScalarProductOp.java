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
import java.util.HashSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.progress.IProgressMonitor;

public class ScalarProductOp extends AbstractOperator implements Operator,
		ChangesMetadata, SelectionTransporter {

	private ArrayList<String> tables = new ArrayList<String>();

	private ArrayList<String> aliases = new ArrayList<String>();

	private int limit = -1;

	private int offset = -1;

	public void addTable(Operator operator, String tableName, String tableAlias) {
		addChild(operator);
		tables.add(tableName);
		aliases.add(tableAlias);
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		ObjectDriver[] dss = new ObjectDriver[getOperatorCount()];
		for (int i = 0; i < dss.length; i++) {
			dss[i] = getOperator(i).getResult(pm);
		}
		try {
			ObjectDriver ret = new ProductDriver(dss, getResultMetadata());
			if ((limit != -1) || (offset != -1)) {
				ret = new LimitOffsetDriver(limit, offset, ret);
			}

			return ret;
		} catch (DriverException e) {
			throw new ExecutionException("Cannot create scalar product", e);
		}
	}

	public String[] getAliases() {
		return aliases.toArray(new String[0]);
	}

	/**
	 * Checks that the tables exist and their aliases doesn't collide
	 * 
	 * @throws NoSuchTableException
	 *             if a table in the product does not exist
	 * @throws SemanticException
	 *             if there is a conflict in the table aliases
	 * @throws DriverException
	 */
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		HashSet<String> refs = new HashSet<String>();
		for (int i = 0; i < tables.size(); i++) {
			String tableName = tables.get(i);

			String ref = tableName;
			String alias = aliases.get(i);
			if (alias != null) {
				ref = alias;
			}
			if (refs.contains(ref)) {
				throw new SemanticException("Ambiguous table reference: " + ref);
			} else {
				refs.add(ref);
			}
		}

		super.validateTableReferences();
	}

	/**
	 * The resulting metadata in a scalar product consist of the metadata in the
	 * first child operator plus the metadata in the second child operator and
	 * so on
	 * 
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		DefaultMetadata ret = new DefaultMetadata();
		for (int i = 0; i < getOperatorCount(); i++) {
			ret.addAll(getOperator(i).getResultMetadata());
		}

		return ret;
	}

	public int getFieldIndex(Field field) throws DriverException,
			SemanticException {
		String tableName = field.getTableName();
		String fieldName = field.getFieldName();
		int fieldIndex = -1;
		// If the field contains a table reference we look just it
		if (tableName != null) {
			Metadata metadata = getMetadata(tableName);
			if (metadata != null) {
				fieldIndex = getFieldIndexInProduct(tableName, fieldName);
			}
		} else {
			// If the field doesn't contain a table reference
			// iterate over the metadata of the tables
			Metadata metadata = getResultMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (metadata.getFieldName(i).equals(fieldName)) {
					if (fieldIndex != -1) {
						throw new SemanticException(
								"Ambiguous field reference: " + fieldName);
					}
					fieldIndex = i;
				}
			}
		}

		return fieldIndex;
	}

	public String getAlias(String sourceName) {
		return aliases.get(tables.indexOf(sourceName));
	}

	Metadata getMetadata(String tableName) throws DriverException,
			SemanticException {
		int tableIndex = tables.indexOf(tableName);
		int aliasIndex = aliases.indexOf(tableName);

		if ((tableIndex == -1) && (aliasIndex == -1)) {
			return null;
		} else if ((tableIndex != -1) && (aliasIndex != -1)) {
			throw new SemanticException("Ambiguous table reference: "
					+ tableName);
		} else {
			if (tableIndex != -1) {
				return getOperator(tableIndex).getResultMetadata();
			} else {
				return getOperator(aliasIndex).getResultMetadata();
			}
		}
	}

	private int getFieldIndexInProduct(String tableName, String fieldName)
			throws DriverException, SemanticException {
		int index = tables.indexOf(tableName);
		if (index == -1) {
			index = aliases.indexOf(tableName);
		}

		if (index == -1) {
			return -1;
		} else {
			int ret = 0;
			for (int i = 0; i < index; i++) {
				ret += getOperator(i).getResultMetadata().getFieldCount();
			}
			Metadata metadata = getMetadata(tableName);
			boolean found = false;
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (fieldName.equals(metadata.getFieldName(i))) {
					found = true;
					ret += i;
				}
			}
			if (found) {
				return ret;
			} else {
				return -1;
			}
		}
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void transportSelection(SelectionOp op) throws DriverException,
			SemanticException {
		Expression[] ands = op.getExpressions();
		int[] tablesNumFields = new int[tables.size()];
		for (int j = 0; j < tablesNumFields.length; j++) {
			tablesNumFields[j] = getMetadata(tables.get(j)).getFieldCount();
		}
		// For each child
		for (int i = 0; i < getOperatorCount(); i++) {
			// Take the expressions that can be pushed to it
			ArrayList<Expression> toPush = new ArrayList<Expression>();
			for (Expression expression : ands) {
				Field[] fields = expression.getFieldReferences();
				boolean allFieldsReferenceIChild = true;
				for (Field field : fields) {
					if (getReferencedChild(tablesNumFields, field) != i) {
						allFieldsReferenceIChild = false;
						break;
					}
				}
				if (allFieldsReferenceIChild) {
					toPush.add(expression);
				}
			}

			// Remove the expressions from the parent and create the selection
			// to push down
			if (toPush.size() > 0) {
				for (Expression expression : toPush) {
					op.removeExpression(expression);
				}
				SelectionOp pushed = new SelectionOp();
				Expression[] expressionsToPush = toPush
						.toArray(new Expression[0]);
				transformFieldReferences(expressionsToPush, i, tablesNumFields);
				pushed.setExpressions(expressionsToPush);
				pushed.addChild(getOperator(i));

				children.set(i, pushed);
			}
		}

	}

	private void transformFieldReferences(Expression[] expressionsToPush,
			int i, int[] tablesNumFields) {
		int sum = 0;
		for (int j = 0; j < i; j++) {
			sum += tablesNumFields[j];
		}

		if (sum != 0) {
			for (Expression expression : expressionsToPush) {
				Field[] fieldRefs = expression.getFieldReferences();
				for (Field field : fieldRefs) {
					field.setFieldIndex(field.getFieldIndex() - sum);
				}
			}
		}
	}

	private int getReferencedChild(int[] tablesNumFields, Field field) {
		int fieldIndex = field.getFieldIndex();
		int child = 0;
		while (fieldIndex >= tablesNumFields[child]) {
			fieldIndex -= tablesNumFields[child];
			child++;
		}

		return child;
	}

	public String getSourceName(Field field) {
		String tableName = field.getTableName();
		int tablesIndex = tables.indexOf(tableName);
		int aliasesIndex = aliases.indexOf(tableName);
		if (tablesIndex != -1) {
			return tableName;
		} else if (aliasesIndex != -1) {
			return tables.get(aliasesIndex);
		} else {
			return null;
		}
	}

}
