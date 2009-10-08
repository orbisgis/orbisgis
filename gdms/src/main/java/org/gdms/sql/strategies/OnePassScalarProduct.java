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
import java.util.HashMap;
import java.util.HashSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.AdHocIndex;
import org.gdms.data.indexes.ExpressionBasedIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.strategies.joinOptimization.IndexScan;
import org.orbisgis.progress.IProgressMonitor;

public class OnePassScalarProduct extends ScalarProductOp {

	private HashSet<Expression> rightSelection;

	private ArrayList<IndexScan> indexScans;

	private IndexManager im;

	public OnePassScalarProduct(IndexManager im) {
		this.im = im;
	}

	@Override
	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			// Get input and outputs
			Operator leftOperator = getOperator(0);
			ObjectDriver leftSource = leftOperator.getResult(pm);
			Operator rightOperator = getOperator(1);
			ObjectDriver rightSource = rightOperator.getResult(pm);
			ScalarProductDriver ret = null;
			ret = new ScalarProductDriver(leftSource, leftSource.getMetadata()
					.getFieldCount(), rightSource, getResultMetadata());
			String rightTableName = rightOperator.getOptimizationInfo()
					.getScanOperator().getTableName();

			// Assign the outer FieldContext
			DefaultFieldContext outerFieldContext = new DefaultFieldContext(
					leftSource);
			for (IndexScan indexScan : indexScans) {
				ExpressionBasedIndexQuery query = (ExpressionBasedIndexQuery) indexScan
						.getQuery();
				Field[] fields = query.getFields();
				for (Field field : fields) {
					field.setFieldContext(outerFieldContext);
					if (referencesLeft(field, leftSource.getMetadata())) {
						field.setFieldIndex(getIndex(leftSource.getMetadata(),
								field.getFieldName()));
					} else {
						field.setFieldIndex(getIndex(rightSource.getMetadata(),
								field.getFieldName()));
					}
				}
			}

			// set the inner field context and resolve field references
			DefaultFieldContext innerFieldContext = new DefaultFieldContext(
					rightSource);
			for (Expression expression : rightSelection) {
				Field[] fields = expression.getFieldReferences();
				for (Field field : fields) {
					if (referencesLeft(field, leftSource.getMetadata())) {
						field.setFieldContext(outerFieldContext);
						field.setFieldIndex(getIndex(leftSource.getMetadata(),
								field.getFieldName()));
					} else {
						field.setFieldContext(innerFieldContext);
						field.setFieldIndex(getIndex(rightSource.getMetadata(),
								field.getFieldName()));
					}
				}
			}

			// Create ad hoc indexes
			HashMap<String, AdHocIndex> adHocIndexes = new HashMap<String, AdHocIndex>();
			for (IndexScan indexScan : indexScans) {
				String indexScanFieldName = indexScan.getQuery().getFieldName();
				if (indexScan.isAdHoc()
						&& !adHocIndexes.containsKey(indexScanFieldName)) {
					String indexId = getIndexTypeForField(rightOperator,
							indexScanFieldName);
					pm.startTask("Creating ad-hoc index");
					AdHocIndex adHocIndex = im.getAdHocIndex(rightSource,
							indexScanFieldName, indexId, pm);
					pm.endTask();
					adHocIndexes.put(indexScanFieldName, adHocIndex);
				}
			}

			// Do the loop
			pm.startTask("Joining tables");
			long rowCount = leftSource.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (i / 100 == i / 100.0) {
					if (pm.isCancelled()) {
						return null;
					} else {
						pm.progressTo((int) (100 * i / rowCount));
					}
				}
				outerFieldContext.setIndex(i);

				// Query the inner source
				ArrayList<Expression> indexExpressions = new ArrayList<Expression>();
				HashSet<Integer> innerSourceIndexes = new HashSet<Integer>();
				boolean all = true;
				for (IndexScan indexScan : indexScans) {
					int[] queryResult;
					if (!indexScan.isAdHoc()) {
						queryResult = im.queryIndex(rightTableName, indexScan
								.getQuery());
					} else {
						AdHocIndex index = adHocIndexes.get(indexScan
								.getQuery().getFieldName());
						queryResult = index.getIterator(indexScan.getQuery());
					}

					// If it's the first time we add all
					if (all) {
						for (int queryResultRow : queryResult) {
							innerSourceIndexes.add(queryResultRow);
						}
						// Do we still need to evaluate the condition?
						if (!indexScan.getQuery().isStrict()) {
							indexExpressions.add(indexScan.getExpression());
						}
					} else {
						// Else we only add if the set is 'quite small'
						if (queryResult.length < innerSourceIndexes.size() * 3) {
							// Intersect the partial and global results
							HashSet<Integer> partialResult = new HashSet<Integer>();
							for (int rowIndex : queryResult) {
								partialResult.add(rowIndex);
							}
							ArrayList<Integer> toDelete = new ArrayList<Integer>();
							for (Integer integer : innerSourceIndexes) {
								if (!partialResult.contains(integer)) {
									toDelete.add(integer);
								}
							}
							for (Integer integer : toDelete) {
								innerSourceIndexes.remove(integer);
							}
							// Do we still need to evaluate the condition?
							if (!indexScan.getQuery().isStrict()) {
								indexExpressions.add(indexScan.getExpression());
							}
						} else {
							indexExpressions.add(indexScan.getExpression());
						}

					}

					all = false;
				}

				// Iterate through the inner source
				for (Integer index : innerSourceIndexes) {
					innerFieldContext.setIndex(index);
					boolean allTrue = true;
					for (Expression expression : indexExpressions) {
						if (!expression.evaluate().getAsBoolean()) {
							allTrue = false;
							break;
						}
					}

					if (allTrue) {
						ret.addRow(i, index);
					}
				}
			}
			pm.endTask();

			return ret;

		} catch (DriverException e) {
			throw new ExecutionException("Error doing the join", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot find one of the sources", e);
		} catch (IndexException e) {
			throw new ExecutionException("Error managing indexes", e);
		} catch (IncompatibleTypesException e) {
			throw new ExecutionException("Type error evaluating expression", e);
		} catch (SemanticException e) {
			throw new ExecutionException(
					"Semantic error executing instruction", e);
		} catch (EvaluationException e) {
			throw new ExecutionException("Error evaluating expression", e);
		}
	}

	private boolean referencesLeft(Field field, Metadata metadata)
			throws DriverException, SemanticException {
		int fieldIndex = passFieldUp(field);

		return fieldIndex < metadata.getFieldCount();
	}

	private String getIndexTypeForField(Operator operator, String fieldName)
			throws DriverException {
		Metadata metadata = operator.getResultMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldName(i).equals(fieldName)) {
				if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
					return IndexManager.RTREE_SPATIAL_INDEX;
				} else {
					return IndexManager.BTREE_ALPHANUMERIC_INDEX;
				}
			}
		}
		throw new DriverException("Field not found " + fieldName);
	}

	private int getIndex(Metadata metadata, String fieldName)
			throws DriverException {
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldName(i).equals(fieldName)) {
				return i;
			}
		}
		throw new RuntimeException("bug!");
	}

	public void setIndexScan(ArrayList<IndexScan> indexScans, Operator right) {
		addChild(right);
		rightSelection = new HashSet<Expression>();
		for (IndexScan indexScan : indexScans) {
			rightSelection.add(indexScan.getExpression());
		}

		this.indexScans = indexScans;
	}

	@Override
	public String toString() {
		StringBuffer indexScansString = new StringBuffer();
		for (IndexScan indexScan : indexScans) {
			indexScansString.append(indexScan.getQuery().getFieldName())
					.append("-");
		}
		String ret = this.getClass().getSimpleName() + "-" + indexScansString
				+ "(";
		for (int i = 0; i < children.size(); i++) {
			ret = ret + children.get(i);
		}
		return ret + ")";
	}
}