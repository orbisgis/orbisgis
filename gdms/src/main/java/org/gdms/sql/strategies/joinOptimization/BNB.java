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
import java.util.HashMap;
import java.util.Stack;

import org.gdms.data.ExecutionException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.strategies.JoinContext;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.OperatorFilter;
import org.gdms.sql.strategies.OptimizationInfo;
import org.gdms.sql.strategies.ScalarProductOp;
import org.gdms.sql.strategies.ScanOperator;
import org.gdms.sql.strategies.SelectionOp;
import org.gdms.sql.strategies.SemanticException;

public class BNB {

	private Stack<BNBNode> stack;
	private double best;
	private HashMap<String, ArrayList<String>> tableIndexes;
	private IndexManager im;

	public BNB(IndexManager im) {
		this.im = im;
	}

	public BNBNode optimize(Operator op) throws DriverException,
			SemanticException {
		// Find the scalar product
		Operator[] selections = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return op instanceof SelectionOp;
			}

		});

		if (selections.length == 0) {
			return null;
		}

		ScalarProductOp scalarProduct = null;
		SelectionOp selection = null;
		for (Operator selectionOp : selections) {
			Operator child = selectionOp.getOperator(0);
			if (child instanceof ScalarProductOp) {
				scalarProduct = (ScalarProductOp) child;
				selection = (SelectionOp) selectionOp;
				break;
			}
		}

		createTableIndexesCache(scalarProduct);

		ArrayList<Operator> tables = new ArrayList<Operator>();
		// Create root node empty
		for (int i = 0; i < scalarProduct.getOperatorCount(); i++) {
			tables.add(scalarProduct.getOperator(i));
		}
		BNBNode root = new BNBNode(tables);

		// Initialize best solution to Double.MAX_VALUE
		best = Double.MAX_VALUE;
		BNBNode argBest = null;

		// Initialize the stack of elements and add the empty node
		stack = new Stack<BNBNode>();
		stack.add(root);

		// Do the optimization
		while (!stack.isEmpty()) {
			BNBNode node = stack.pop();
			// If there is no pending table
			if (node.isComplete() && node.evaluate() < best) {
				best = node.evaluate();
				argBest = node;
			} else {
				// For each pending table
				ArrayList<Operator> pendingTables = node.getPendingOperators();
				for (Operator newOperator : pendingTables) {
					OptimizationInfo optimizationInfo = newOperator
							.getOptimizationInfo();
					String tableName = optimizationInfo.getScanOperator()
							.getTableName();
					// Get the list of queries that can improve the
					// execution performance
					ArrayList<IndexScan> availableQueries = new ArrayList<IndexScan>();
					Expression[] ands = selection.getExpressions();
					for (Expression and : ands) {
						IndexQuery[] queries = getIndexableField(node,
								optimizationInfo.getScanOperator(), and);
						for (IndexQuery indexQuery : queries) {
							boolean adHoc;
							if (tableIndexes.get(tableName).contains(
									indexQuery.getFieldName())
									&& (newOperator instanceof ScanOperator)) {
								adHoc = false;
							} else {
								adHoc = true;
							}
							availableQueries.add(new IndexScan(indexQuery,
									adHoc, and));
						}
					}

					// Add all the possibilities of usage of that queries
					for (int i = 0; i < Math.pow(2, availableQueries.size()); i++) {
						BNBNode indexScanChild = node.cloneNode();
						indexScanChild.fixOperator(newOperator);
						int combination = i;
						for (int j = 0; j < availableQueries.size(); j++) {
							if ((combination & 0x01) == 0x01) {
								IndexScan indexScan = availableQueries.get(j);
								indexScanChild.addIndexScan(newOperator,
										indexScan);
							}
							combination = combination >> 1;
						}

						push(indexScanChild);
					}
				}
			}
		}

		argBest.setSelection(selection);
		return argBest;
	}

	private void createTableIndexesCache(ScalarProductOp scalarProduct) {
		tableIndexes = new HashMap<String, ArrayList<String>>();
		String[] tables = scalarProduct.getReferencedTables();
		for (String table : tables) {
			String[] fieldNames = im.getIndexedFieldNames(table);
			ArrayList<String> fieldNamesArray = new ArrayList<String>();
			for (String fieldName : fieldNames) {
				fieldNamesArray.add(fieldName);
			}
			tableIndexes.put(table, fieldNamesArray);
		}
	}

	/**
	 * Gets an index query corresponding to the and expression on a field if and
	 * only if all the field references but one refers to the fixed tables in
	 * node and the remaining field references refers to newTable
	 * 
	 * @param node
	 * @param newOperator
	 * @param and
	 * @return
	 * @throws DriverException
	 * @throws ExecutionException
	 * @throws EvaluationException
	 */
	private IndexQuery[] getIndexableField(final BNBNode node,
			ScanOperator scanOperator, Expression and) throws DriverException {
		Field[] fieldReferences = and.getFieldReferences();
		Field newTableField = null;
		for (Field field : fieldReferences) {
			if (scanOperator.referencesThisTable(field)) {
				newTableField = field;
				break;
			}
		}
		if (newTableField != null) {
			IndexQuery[] ret = ScanOperator.getQuery(new JoinContext() {

				public boolean isEvaluable(Expression exp)
						throws DriverException {
					return node.isEvaluable(exp);
				}

			}, newTableField, and);
			if (ret != null) {
				return ret;
			} else {
				return new IndexQuery[0];
			}
		} else {
			return new IndexQuery[0];
		}
	}

	private void push(BNBNode node) throws DriverException {
		if (node.evaluate() < best) {
			stack.push(node);
		}
	}
}
