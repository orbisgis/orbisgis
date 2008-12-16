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
package org.gdms.sql.strategies.joinOptimization;

import java.util.ArrayList;
import java.util.HashSet;

import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.strategies.OnePassScalarProduct;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.OptimizationInfo;
import org.gdms.sql.strategies.ScalarProductOp;
import org.gdms.sql.strategies.SelectionOp;
import org.gdms.sql.strategies.SemanticException;

public class BNBNode {
	private ArrayList<FixedOperator> fixedOperators = new ArrayList<FixedOperator>();

	private ArrayList<Operator> pendingOperators = new ArrayList<Operator>();

	private double proportional = 1;
	private double constant = 0;

	private boolean updated = true;

	private SelectionOp selection;

	public BNBNode(ArrayList<Operator> pendingTables) {
		this.pendingOperators = pendingTables;
	}

	@SuppressWarnings("unchecked")
	public BNBNode cloneNode() {
		BNBNode ret = new BNBNode((ArrayList<Operator>) pendingOperators
				.clone());
		ret.fixedOperators = (ArrayList<FixedOperator>) fixedOperators.clone();
		ret.constant = constant;
		ret.proportional = proportional;

		return ret;
	}

	private class FixedOperator {
		private Operator operator;
		private OptimizationInfo optimizationInfo;
		private ArrayList<IndexScan> queries = new ArrayList<IndexScan>();

		public FixedOperator(Operator op) {
			this.operator = op;
			this.optimizationInfo = op.getOptimizationInfo();
		}
	}

	public double evaluate() throws DriverException {
		if (!updated) {
			FixedOperator last = fixedOperators.get(fixedOperators.size() - 1);
			OptimizationInfo info = last.optimizationInfo;
			if (last.queries.size() == 0) {
				proportional = proportional * info.getRowCount();
			} else {
				HashSet<String> indexedFields = new HashSet<String>();
				for (IndexScan indexScan : last.queries) {
					String fieldName = indexScan.getQuery().getFieldName();
					if (indexScan.isAdHoc()
							&& !indexedFields.contains(fieldName)) {
						constant = constant + info.getRowCount();
						indexedFields.add(fieldName);
					}
				}
				proportional = proportional * info.getRowCount() / 10;
			}

			updated = true;
		}

		return proportional + constant;
	}

	public boolean isComplete() {
		return pendingOperators.size() == 0;
	}

	public ArrayList<Operator> getPendingOperators() {
		return pendingOperators;
	}

	public void fixOperator(Operator newOperator) {
		pendingOperators.remove(newOperator);
		fixedOperators.add(new FixedOperator(newOperator));
		updated = false;
	}

	/**
	 * Adds a index-scan query to the tables array
	 * 
	 * @param operator
	 * @param indexScan
	 */
	public void addIndexScan(Operator operator, IndexScan indexScan) {
		for (int i = fixedOperators.size() - 1; i >= 0; i--) {
			FixedOperator fixedOp = fixedOperators.get(i);
			if (fixedOp.operator == operator) {
				fixedOp.queries.add(indexScan);
				updated = false;
				return;
			}
		}

		throw new RuntimeException("bug!");
	}

	@Override
	public String toString() {
		try {
			StringBuffer buffer = new StringBuffer().append("(").append(
					evaluate()).append(")\n");
			for (FixedOperator operator : fixedOperators) {
				buffer.append(operator.optimizationInfo.getScanOperator()
						.getTableName());
				for (IndexScan indexScan : operator.queries) {
					buffer.append("(").append(
							indexScan.getQuery().getFieldName()).append(")");
				}
				buffer.append("\n");
			}

			return buffer.toString();
		} catch (DriverException e) {
			throw new RuntimeException("bug!");
		}
	}

	/**
	 * Replaces the scalar product under the selection by the plan this node
	 * represents
	 * 
	 * @param im
	 * 
	 * @throws SemanticException
	 * @throws DriverException
	 */
	public void replaceScalarProduct(IndexManager im) throws DriverException,
			SemanticException {

		// Put the selection on the first left operator
		Operator left = fixedOperators.get(0).operator;

		// Create the product operators that will execute the index strategy
		for (int i = 1; i < fixedOperators.size(); i++) {
			FixedOperator fixedOperator = fixedOperators.get(i);
			Operator right = fixedOperator.operator;

			if (fixedOperator.queries.size() > 0) {
				// Create the scalar product
				OnePassScalarProduct scalar = new OnePassScalarProduct(im);
				scalar.addChild(left);

				// Set the index-scan to apply to the right operator
				scalar.setIndexScan(fixedOperator.queries, right);

				// Delete the expressions in the selection
				for (IndexScan indexScan : fixedOperator.queries) {
					selection.removeExpression(indexScan.getExpression());
				}

				// Set the result as the next product left operand
				left = scalar;

			} else {
				ScalarProductOp scalar = new ScalarProductOp();
				scalar.addChild(left);
				scalar.addChild(right);
				left = scalar;
			}
		}

		Operator newScalarProduct = left;
		selection.substituteChild(newScalarProduct);
		selection.setQueries(new IndexQuery[0]);
	}

	public void setSelection(SelectionOp selection) {
		this.selection = selection;
	}

	/**
	 * The expression is evaluable if all the field reference fixed operators
	 * 
	 * @param exp
	 * @return
	 * @throws DriverException
	 */
	public boolean isEvaluable(Expression exp) throws DriverException {
		Field[] fields = exp.getFieldReferences();
		for (Field field : fields) {
			if (!isFieldEvaluable(field)) {
				return false;
			}
		}

		return true;
	}

	private boolean isFieldEvaluable(Field field) throws DriverException {
		for (FixedOperator fixedOperator : fixedOperators) {
			if (field.getTableName() == null) {
				Metadata metadata = fixedOperator.optimizationInfo
						.getScanOperator().getResultMetadata();
				for (int i = 0; i < metadata.getFieldCount(); i++) {
					if (metadata.getFieldName(i).equals(field.getFieldName())) {
						return true;
					}
				}
			} else {
				String tableName = fixedOperator.optimizationInfo
						.getScanOperator().getTableName();
				String tableAlias = fixedOperator.optimizationInfo
						.getScanOperator().getTableAlias();
				if (field.getTableName().equals(tableAlias)) {
					return true;
				} else if (field.getTableName().equals(tableName)) {
					return true;
				}
			}
		}

		return false;
	}

}
