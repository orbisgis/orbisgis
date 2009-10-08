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
		SelectionTransporter {

	private int limit = -1;

	private int offset = -1;

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
		for (int i = 0; i < getOperatorCount(); i++) {
			String tableName = getOperator(i).getTableName();
			if (tableName != null) {
				String ref = tableName;
				String alias = getOperator(i).getTableAlias();
				if (alias != null) {
					ref = alias;
				}
				if (refs.contains(ref)) {
					throw new SemanticException("Ambiguous table reference: "
							+ ref);
				} else {
					refs.add(ref);
				}
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

	public int passFieldUp(Field field) throws DriverException,
			AmbiguousFieldReferenceException {
		String tableName = field.getTableName();
		String fieldName = field.getFieldName();
		int fieldIndex = -1;
		// If the field contains a table reference we look just it
		if (tableName != null) {
			Metadata metadata = getBranchMetadata(tableName);
			if (metadata != null) {
				int offset = 0;
				for (int i = 0; i < getOperatorCount(); i++) {
					Operator child = getOperator(i);
					int childFieldIndex = child.passFieldUp(field);
					if (childFieldIndex != -1) {
						fieldIndex = offset + childFieldIndex;
					} else {
						offset += child.getResultMetadata().getFieldCount();
					}
				}
			}
		} else {
			// If the field doesn't contain a table reference
			// iterate over the metadata of the tables
			Metadata metadata = getResultMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (metadata.getFieldName(i).equals(fieldName)) {
					if (fieldIndex == -1) {
						fieldIndex = i;
					} else {
						throw new AmbiguousFieldReferenceException(field);
					}
				}
			}
		}

		return fieldIndex;
	}

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
		int[] tablesNumFields = new int[getOperatorCount()];
		for (int j = 0; j < tablesNumFields.length; j++) {
			tablesNumFields[j] = getOperator(j).getResultMetadata()
					.getFieldCount();
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

}
