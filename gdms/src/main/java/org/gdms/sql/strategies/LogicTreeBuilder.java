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
import java.util.Arrays;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.evaluator.And;
import org.gdms.sql.evaluator.Division;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.evaluator.GreaterThan;
import org.gdms.sql.evaluator.GreaterThanOrEqual;
import org.gdms.sql.evaluator.IsOperator;
import org.gdms.sql.evaluator.LessThan;
import org.gdms.sql.evaluator.LessThanOrEqual;
import org.gdms.sql.evaluator.LikeOperator;
import org.gdms.sql.evaluator.Literal;
import org.gdms.sql.evaluator.Not;
import org.gdms.sql.evaluator.NotEquals;
import org.gdms.sql.evaluator.Or;
import org.gdms.sql.evaluator.Product;
import org.gdms.sql.evaluator.Substraction;
import org.gdms.sql.evaluator.Sum;
import org.gdms.sql.parser.ASTSQLAdd;
import org.gdms.sql.parser.ASTSQLAddColumn;
import org.gdms.sql.parser.ASTSQLAddPrimaryKey;
import org.gdms.sql.parser.ASTSQLAlter;
import org.gdms.sql.parser.ASTSQLAndExpr;
import org.gdms.sql.parser.ASTSQLBetweenClause;
import org.gdms.sql.parser.ASTSQLColRef;
import org.gdms.sql.parser.ASTSQLCompareExpr;
import org.gdms.sql.parser.ASTSQLCreate;
import org.gdms.sql.parser.ASTSQLDelete;
import org.gdms.sql.parser.ASTSQLDrop;
import org.gdms.sql.parser.ASTSQLDropColumn;
import org.gdms.sql.parser.ASTSQLExistsClause;
import org.gdms.sql.parser.ASTSQLFunction;
import org.gdms.sql.parser.ASTSQLGroupBy;
import org.gdms.sql.parser.ASTSQLId;
import org.gdms.sql.parser.ASTSQLIdSequence;
import org.gdms.sql.parser.ASTSQLInClause;
import org.gdms.sql.parser.ASTSQLInsert;
import org.gdms.sql.parser.ASTSQLIsClause;
import org.gdms.sql.parser.ASTSQLLeftJoinClause;
import org.gdms.sql.parser.ASTSQLLikeClause;
import org.gdms.sql.parser.ASTSQLLiteral;
import org.gdms.sql.parser.ASTSQLNotExpr;
import org.gdms.sql.parser.ASTSQLOrExpr;
import org.gdms.sql.parser.ASTSQLOrderBy;
import org.gdms.sql.parser.ASTSQLPattern;
import org.gdms.sql.parser.ASTSQLProductExpr;
import org.gdms.sql.parser.ASTSQLRename;
import org.gdms.sql.parser.ASTSQLRenameColumn;
import org.gdms.sql.parser.ASTSQLRenameTable;
import org.gdms.sql.parser.ASTSQLRightJoinClause;
import org.gdms.sql.parser.ASTSQLSelect;
import org.gdms.sql.parser.ASTSQLSelectAllCols;
import org.gdms.sql.parser.ASTSQLSelectAllColsInTable;
import org.gdms.sql.parser.ASTSQLSelectAllInTableModifier;
import org.gdms.sql.parser.ASTSQLSelectAllModifier;
import org.gdms.sql.parser.ASTSQLSelectAllModifierExcept;
import org.gdms.sql.parser.ASTSQLSelectCols;
import org.gdms.sql.parser.ASTSQLSelectLimit;
import org.gdms.sql.parser.ASTSQLSelectList;
import org.gdms.sql.parser.ASTSQLSelectOffset;
import org.gdms.sql.parser.ASTSQLSumExpr;
import org.gdms.sql.parser.ASTSQLTableList;
import org.gdms.sql.parser.ASTSQLTableRef;
import org.gdms.sql.parser.ASTSQLUnaryExpr;
import org.gdms.sql.parser.ASTSQLUnion;
import org.gdms.sql.parser.ASTSQLUpdate;
import org.gdms.sql.parser.ASTSQLValueList;
import org.gdms.sql.parser.ASTSQLWhere;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;
import org.gdms.sql.strategies.ProjectionOp.AbstractStarElement;
import org.gdms.sql.strategies.ProjectionOp.StarElement;

public class LogicTreeBuilder {

	private DataSourceFactory dsf;

	public LogicTreeBuilder(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public Operator buildTree(SimpleNode rootNode) throws SemanticException,
			DriverException {
		Operator operator = getOperator(rootNode);
		operator.setDataSourceFactory(dsf);
		operator.setRoot(true);
		return operator;
	}

	/**
	 * Recursive method that returns the root of the tree of logic operators
	 * that implement the query. Returns null if no operator has to be applied
	 *
	 * @param node
	 * @return
	 * @throws SemanticException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	private Operator getOperator(Node theNode) throws SemanticException,
			DriverException {
		SimpleNode node = (SimpleNode) theNode;

		if (node instanceof ASTSQLDelete) {
			String tableRefId = getId(node.jjtGetChild(0));
			ScanOperator tableRef = new ScanOperator(dsf, tableRefId, null);
			// we use a scalar product because it helps to resolve the field
			// references
			ScalarProductOp identity = new ScalarProductOp();
			identity.addChild(tableRef);
			Operator top = identity;
			SimpleNode whereNode = getChildNode(node, ASTSQLWhere.class);
			if (whereNode != null) {
				Operator where = getOperator(whereNode);
				where.addChild(top);
				top = where;
			}
			Operator deleteOperator = new DeleteOperator();
			deleteOperator.addChild(top);
			return deleteOperator;
		} else if (node instanceof ASTSQLInsert) {
			String tableRefId = getId(node.jjtGetChild(0));
			ScanOperator tableRef = new ScanOperator(dsf, tableRefId, null);
			// we use a scalar product because it helps to resolve the field
			// references
			ScalarProductOp identity = new ScalarProductOp();
			identity.addChild(tableRef);
			Operator top = identity;

			InsertOperator ret = new InsertOperator();
			ret.addChild(top);
			int valueStartIndex;
			if (node.jjtGetChild(1) instanceof ASTSQLId) {
				int i = 1;
				while (node.jjtGetChild(i) instanceof ASTSQLId) {
					ret.addField(getId(node.jjtGetChild(i)));
					i++;
				}
				valueStartIndex = i;
			} else {
				ret.addAllFields();
				valueStartIndex = 1;
			}

			for (int i = valueStartIndex; i < node.jjtGetNumChildren(); i++) {
				Expression value = getExpression(node.jjtGetChild(i));
				ret.addFieldValue(value);
			}

			return ret;
		} else if (node instanceof ASTSQLDrop) {
			if ((node.first_token.next.kind == SQLEngineConstants.TABLE)
					|| (node.first_token.next.kind == SQLEngineConstants.VIEW)) {
				Node tableListNode = node.jjtGetChild(0);
				DropTableOperator operator = new DropTableOperator();
				for (int i = 0; i < tableListNode.jjtGetNumChildren(); i++) {
					ScanOperator scan = (ScanOperator) getOperator(tableListNode
							.jjtGetChild(i));
					operator.addChild(scan);
				}

				return operator;

			} else {
				String tableName = getId(node.jjtGetChild(0));
				String fieldName = getId(node.jjtGetChild(1));
				DropIndexOperator op = new DropIndexOperator(dsf, tableName,
						fieldName);
				op.addChild(new ScanOperator(dsf, tableName, tableName));
				return op;
			}
		} else if (node instanceof ASTSQLAlter) {
			if (node.first_token.next.kind == SQLEngineConstants.TABLE) {
				String tableName = getId(node.jjtGetChild(0));
				Node schemaNode = node.jjtGetChild(1);

				if (schemaNode instanceof ASTSQLAdd) {

					Node subnode = schemaNode.jjtGetChild(0);
					if (subnode instanceof ASTSQLAddColumn) {
						String columnName = getId(subnode.jjtGetChild(0));
						String columnType = getId(subnode.jjtGetChild(1));
						AddColumnOperator op = new AddColumnOperator(dsf,
								tableName, columnName, columnType);
						return op;
					} else if (subnode instanceof ASTSQLAddPrimaryKey) {
						SimpleNode node1 = (SimpleNode) subnode.jjtGetChild(0);
						String columnName = node1.first_token.toString();
						AddPrimaryKeyOperator op = new AddPrimaryKeyOperator(
								dsf, tableName, columnName);
						return op;
					}

				}

				else if (schemaNode instanceof ASTSQLRename) {
					Node subnode = schemaNode.jjtGetChild(0);
					if (subnode instanceof ASTSQLRenameColumn) {
						String columnName = getId(subnode.jjtGetChild(0));
						String columnNewName = getId(subnode.jjtGetChild(1));
						RenameColumnOperator op = new RenameColumnOperator(dsf,
								tableName, columnName, columnNewName);
						return op;
					} else if (subnode instanceof ASTSQLRenameTable) {
						SimpleNode node1 = (SimpleNode) subnode;
						RenameTableOperator op = new RenameTableOperator(dsf,
								tableName, node1.last_token.toString());
						return op;
					}

				}

				else if (schemaNode instanceof ASTSQLDropColumn) {

					throw new UnsupportedOperationException("Not yet supported");
				}

				else {

					throw new UnsupportedOperationException("Not yet supported");
				}

			}

			throw new UnsupportedOperationException("Not yet supported");

		} else if (node instanceof ASTSQLUnion) {
			Node tableOrSelect1 = node.jjtGetChild(0);
			Node tableOrSelect2 = node.jjtGetChild(1);
			Operator op1 = getOperator(tableOrSelect1);
			Operator op2 = getOperator(tableOrSelect2);
			return new UnionOperator(op1, op2);
		} else if (node instanceof ASTSQLCreate) {
			if (node.first_token.next.kind == SQLEngineConstants.TABLE) {
				String tableName = getId(node.jjtGetChild(0));
				Node schemaNode = node.jjtGetChild(1);
				if (schemaNode instanceof ASTSQLSelect) {
					Operator selectOp = getOperator(schemaNode);
					Operator co = new CreateTableOperator(dsf, tableName);
					co.addChild(selectOp);
					return co;
				} else if (schemaNode instanceof ASTSQLUnion) {
					Operator unionOp = getOperator(schemaNode);
					Operator co = new CreateTableOperator(dsf, tableName);
					co.addChild(unionOp);
					return co;
				} else {
					throw new UnsupportedOperationException(
							"Only 'create table [tablename] "
									+ "as ...' implemented");
				}
			}

			else if (node.first_token.next.kind == SQLEngineConstants.VIEW) {
				String tableName = getId(node.jjtGetChild(0));
				Node schemaNode = node.jjtGetChild(1);
				CreateViewOperator co = new CreateViewOperator(tableName,
						getText(schemaNode), dsf);

				if (schemaNode instanceof ASTSQLSelect) {
					Operator selectOp = getOperator(schemaNode);
					co.addChild(selectOp);
				} else if (schemaNode instanceof ASTSQLUnion) {
					Operator unionOp = getOperator(schemaNode);
					co.addChild(unionOp);
				}
				return co;
			}

			else {
				// Create index
				String tableName = getId(node.jjtGetChild(0));
				String fieldName = getId(node.jjtGetChild(1));
				CreateIndexOperator op = new CreateIndexOperator(dsf,
						tableName, fieldName);
				op.addChild(new ScanOperator(dsf, tableName, tableName));
				return op;
			}
		} else if (node instanceof ASTSQLUpdate) {
			String tableName = getId(node.jjtGetChild(0));
			ScanOperator scan = new ScanOperator(dsf, tableName, null);
			// we use a scalar product because it helps to resolve the field
			// references
			ScalarProductOp identity = new ScalarProductOp();
			identity.addChild(scan);
			Operator last = identity;

			UpdateOperator updateOperator = new UpdateOperator();
			Node whereNode = getChildNode(node, ASTSQLWhere.class);
			if (whereNode != null) {
				Expression whereExpr = getSQLExpression(whereNode
						.jjtGetChild(0));
				updateOperator.setWhereExpression(whereExpr);
			}

			int index = 1;
			while ((index < node.jjtGetNumChildren())
					&& !(node.jjtGetChild(index) instanceof ASTSQLWhere)) {
				Node updateAssignmentNode = node.jjtGetChild(index);
				Field field = new Field(getId(updateAssignmentNode
						.jjtGetChild(0)));
				Expression value = getSQLExpression(updateAssignmentNode
						.jjtGetChild(1));
				updateOperator.addAssignment(field, value);
				index++;
			}

			updateOperator.addChild(last);
			return updateOperator;
		} else if (node instanceof ASTSQLSelect) {
			Operator last = null;

			// Scalar product
			SimpleNode tableListNode = getChildNode(node, ASTSQLTableList.class);
			Operator scalarProductOp = null;
			if (tableListNode != null) {
				scalarProductOp = getOperator(node.jjtGetChild(1));
				last = scalarProductOp;
			}

			// Selection
			SimpleNode whereNode = getChildNode(node, ASTSQLWhere.class);
			Operator selectionOp = null;
			if (whereNode != null) {
				// Selection of records
				selectionOp = getOperator(whereNode);
				if (last != null) {
					selectionOp.addChild(last);
				}
				last = selectionOp;
			}

			// Grouping
			SimpleNode groupByNode = getChildNode(node, ASTSQLGroupBy.class);
			GroupByOperator groupByOperator = null;
			if (groupByNode != null) {
				groupByOperator = new GroupByOperator();
				Node groupByListNode = groupByNode.jjtGetChild(0);
				for (int i = 0; i < groupByListNode.jjtGetNumChildren(); i++) {
					Field field = (Field) getSQLExpression(groupByListNode
							.jjtGetChild(i));
					groupByOperator.addField(field);
				}
				if (last != null) {
					groupByOperator.addChild(last);
				}
				last = groupByOperator;

			}

			// Projection
			SimpleNode projectionNode = (SimpleNode) node.jjtGetChild(0);
			ProjectionOp projOp = (ProjectionOp) getOperator(projectionNode);
			if (last != null) {
				projOp.addChild(last);
			}
			last = projOp;

			if (groupByNode != null) {
				Node havingNode = getChildNode(groupByNode, ASTSQLOrExpr.class);
				if (havingNode != null) {
					Expression havingExpr = getSQLExpression(havingNode);
					SelectionOp havingSelection = new SelectionOp();
					havingSelection.setExpression(havingExpr);
					havingSelection.addChild(last);
					last = havingSelection;
				}
			}

			// Discriminate custom queries from normal selects
			if (projOp.isCustomQuery()) {
				Expression customQueryExpr = projOp.getExpressions()[0];
				CustomQueryOperator op = new CustomQueryOperator(
						customQueryExpr);
				if (scalarProductOp != null) {
					if (selectionOp != null) {
						if (scalarProductOp.getOperatorCount() > 1) {
							throw new SemanticException(
									"Custom queries with where "
											+ "clause must have one and "
											+ "only one table in the from clause");
						} else {
							op.addChild(selectionOp);
						}
					} else {
						op.addChild(scalarProductOp);
					}
				}

				if (getChildNode(node, ASTSQLSelectLimit.class) != null) {
					throw new SemanticException("Custom queries cannot "
							+ "have 'limit' clause");
				} else if (getChildNode(node, ASTSQLSelectOffset.class) != null) {
					throw new SemanticException("Custom queries cannot "
							+ "have 'offset' clause");
				}

				return op;
			} else {

				// Check that it must have FROM clause
				if (getChildNode(node, ASTSQLTableList.class) == null) {
					throw new SemanticException("A FROM clause must "
							+ "be specified in non custom queries");
				}

				// Merge aggregated and group by
				if (projOp.isAggregated()) {
					if (groupByOperator == null) {
						groupByOperator = new GroupByOperator();
						if (projOp.getOperatorCount() == 0) {
							throw new SemanticException(
									"Invalid SQL: non custom "
											+ "queries must have 'from' clause");
						}
						groupByOperator.addChild(projOp.removeOperator(0));
						projOp.addChild(groupByOperator);
					}
				}
				if (groupByOperator != null) {
					projOp.setGroupByFieldNames(groupByOperator
							.getGroupByField());
					groupByOperator.addAggregatedFunction(projOp
							.transformExpressionsInGroupByReferences());
				}

				// Order by
				SimpleNode orderByNode = getChildNode(node, ASTSQLOrderBy.class);
				if (orderByNode != null) {
					OrderByOperator orderByOperator = new OrderByOperator();
					Node obListNode = orderByNode.jjtGetChild(0);
					for (int i = 0; i < obListNode.jjtGetNumChildren(); i++) {
						Node obElement = obListNode.jjtGetChild(i);
						int nodeType = 0;
						Field field = null;
						if (node instanceof ASTSQLFunction) {
							nodeType = 1;
						} else {
							nodeType = 2;
							field = (Field) getSQLExpression(obElement
									.jjtGetChild(0));
						}
						boolean asc = true;
						if (obElement.jjtGetNumChildren() > 1) {
							int orderType = ((SimpleNode) obElement
									.jjtGetChild(1)).first_token.kind;
							if (orderType == SQLEngineConstants.DESC) {
								asc = false;
							}
						}

						if (nodeType == 1) {
							throw new UnsupportedOperationException(
									"Cuurently not supported");
						}

						else if (nodeType == 2) {
							orderByOperator.addCriterium(field, asc);
						}

					}

					orderByOperator.addChild(last);
					last = orderByOperator;
				}

				// Limit and offset
				SimpleNode limitNode = getChildNode(node,
						ASTSQLSelectLimit.class);
				SimpleNode offsetNode = getChildNode(node,
						ASTSQLSelectOffset.class);
				if (limitNode != null) {
					int limit = getLimitOffsetliteral(limitNode);
					last.setLimit(limit);
				}
				if (offsetNode != null) {
					int offset = getLimitOffsetliteral(offsetNode);
					last.setOffset(offset);
				}

				return last;
			}
		} else if (node instanceof ASTSQLSelectCols) {
			ProjectionOp projOp = (ProjectionOp) getOperator(node
					.jjtGetChild(0));
			if (node.first_token.kind == SQLEngineConstants.DISTINCT) {
				projOp.setDistinct(true);
			}
			return projOp;
		} else if (node instanceof ASTSQLSelectList) {
			// Iterate getting either the expression or the '*' or the
			// table_ref.*
			ProjectionOp ret = new ProjectionOp();
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
				if (childNode instanceof ASTSQLOrExpr) {
					Expression expression = getSQLExpression(childNode);
					String alias = null;
					if (i + 1 < node.jjtGetNumChildren()) {
						Node idNode = node.jjtGetChild(i + 1);
						if (idNode instanceof ASTSQLId) {
							alias = getId(idNode);
							i++;
						}
					}
					ret.addExpr(expression, alias);
				} else if (childNode instanceof ASTSQLSelectAllCols) {
					ProjectionOp.StarElement star = new StarElement();
					fillStar(star, childNode);
					ret.addSelectElement(star);
				} else if (childNode instanceof ASTSQLSelectAllColsInTable) {
					String tableName = getId(childNode.jjtGetChild(0));
					ProjectionOp.TableStarElement star = new ProjectionOp.TableStarElement(
							tableName);
					fillStar(star, childNode);
					ret.addSelectElement(star);
				}
			}
			return ret;
		} else if (node instanceof ASTSQLTableList) {
			ScalarProductOp ret = new ScalarProductOp();
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				SimpleNode tableRefNode = (SimpleNode) node.jjtGetChild(i);
				ScanOperator tableScanOperator = (ScanOperator) getOperator(tableRefNode);
				ret.addChild(tableScanOperator);
			}
			return ret;
		} else if (node instanceof ASTSQLTableRef) {
			String tableName = getId(node.jjtGetChild(0));
			String tableAlias = null;
			if (node.jjtGetNumChildren() == 2) {
				tableAlias = getId(node.jjtGetChild(1));
			}
			return new ScanOperator(dsf, tableName, tableAlias);
		} else if (node instanceof ASTSQLWhere) {
			SelectionOp ret = new SelectionOp();
			ret
					.setExpression(getSQLExpression((SimpleNode) node
							.jjtGetChild(0)));
			return ret;
		} else {
			/*
			 * Default behavior is by-pass the node
			 */
			if (node.jjtGetNumChildren() != 1) {
				throw new RuntimeException("If a node has not "
						+ " one and only one child it has to do "
						+ "a specific action: " + node);
			}
			return getOperator(node.jjtGetChild(0));
		}
	}

	private int getLimitOffsetliteral(SimpleNode limitNode)
			throws SemanticException {
		String image = limitNode.first_token.next.image;
		try {
			return Integer.parseInt(image);
		} catch (NumberFormatException e) {
			throw new SemanticException("Limit argument must be an integer");
		}
	}

	private String getId(Node node) {
		SimpleNode n = (SimpleNode) node;
		Token token = n.first_token;
		String ret = token.image;
		return ret;
	}

	private Expression getSQLExpression(Node node) {
		Expression ret = getExpression(node);

		return ret;
	}

	private Expression getExpression(Node theNode) {
		SimpleNode node = (SimpleNode) theNode;
		if (node instanceof ASTSQLSumExpr) {
			// Get expressions or bypass if only one child
			Expression op = buildArithmeticOperator(new OperatorFactory() {

				public Expression instantiateOperator(String symbol,
						Expression left, Expression right) {
					if (symbol.equals("+")) {
						return new Sum(left, right);
					} else {
						return new Substraction(left, right);
					}
				}

			}, node);
			if (op != null) {
				return op;
			}
		} else if (node instanceof ASTSQLProductExpr) {
			// Get expressions or bypass if only one child
			Expression op = buildArithmeticOperator(new OperatorFactory() {

				public Expression instantiateOperator(String symbol,
						Expression left, Expression right) {
					if (symbol.equals("*")) {
						return new Product(left, right);
					} else {
						return new Division(left, right);
					}
				}

			}, node);
			if (op != null) {
				return op;
			}
		} else if (node instanceof ASTSQLNotExpr) {
			if (node.first_token.kind == SQLEngineConstants.NOT) {
				Expression expr = getExpression(node.jjtGetChild(0));
				Expression op = new Not(expr);
				return op;
			}
		} else if (node instanceof ASTSQLOrExpr) {
			// Get expressions or bypass if only one child
			Expression op = buildBooleanOperator(new OperatorFactory() {

				public Expression instantiateOperator(String symbol,
						Expression left, Expression right) {
					return new Or(left, right);
				}

			}, node);
			if (op != null) {
				return op;
			}
		} else if (node instanceof ASTSQLAndExpr) {
			// Get expressions or bypass if only one child
			Expression op = buildBooleanOperator(new OperatorFactory() {

				public Expression instantiateOperator(String symbol,
						Expression left, Expression right) {
					return new And(left, right);
				}

			}, node);
			if (op != null) {
				return op;
			}
		} else if (node instanceof ASTSQLFunction) {
			String functionName = node.first_token.image;
			Node argsNode = node.jjtGetChild(0);
			int numArgs = argsNode.jjtGetNumChildren();
			if ((numArgs == 1)
					&& (argsNode.jjtGetChild(0) instanceof ASTSQLSelectAllCols)) {
				return new FunctionOperator(functionName);
			} else {
				Expression[] args = new Expression[numArgs];
				for (int i = 0; i < numArgs; i++) {
					args[i] = getSQLExpression((SimpleNode) argsNode
							.jjtGetChild(i));
				}
				return new FunctionOperator(dsf, functionName, args);
			}
		} else if (node instanceof ASTSQLColRef) {
			String fieldName = getId(node.jjtGetChild(0));
			String tableName = null;
			if (node.jjtGetNumChildren() > 1) {
				tableName = fieldName;
				fieldName = getId(node.jjtGetChild(1));
			}
			return new Field(tableName, fieldName);
		} else if (node instanceof ASTSQLLiteral) {
			return new Literal(ValueFactory.createValue(getText(node),
					getType(node)));
		} else if (node instanceof ASTSQLUnaryExpr) {
			Expression exp = getExpression((SimpleNode) node.jjtGetChild(0));
			if (node.first_token.image.equals("-")) {
				exp = new Product(new Literal(ValueFactory.createValue(-1)),
						exp);
			}

			return exp;
		} else if (node instanceof ASTSQLIsClause) {
			Expression ref = getSQLExpression(node.jjtGetChild(0));
			Token token = node.first_token;
			while (token.kind != SQLEngineConstants.IS) {
				token = token.next;
			}
			boolean not = token.next.kind == SQLEngineConstants.NOT;
			IsOperator is = new IsOperator(ref, not);
			return is;

		} else if (node instanceof ASTSQLExistsClause) {
			throw new UnsupportedOperationException("Exist is not supported");
		} else if (node instanceof ASTSQLPattern) {
			if (node.jjtGetNumChildren() == 0) {
				// literal
				return new Literal(ValueFactory.createValue(getText(node),
						getType(node)));
			} else {
				// colref
				return getExpression(node.jjtGetChild(0));
			}
		} else if (node instanceof ASTSQLCompareExpr) {
			if (node.jjtGetNumChildren() > 1) {
				// SQLCompareExprRight
				SimpleNode right = (SimpleNode) node.jjtGetChild(1);
				if (right.jjtGetNumChildren() == 2) {
					// Comparison
					String operator = getText(right.jjtGetChild(0));
					Expression leftOperand = getSQLExpression((SimpleNode) node
							.jjtGetChild(0));
					Expression rightOperand = getSQLExpression((SimpleNode) right
							.jjtGetChild(1));
					if (operator.equals("<")) {
						return new LessThan(leftOperand, rightOperand);
					} else if (operator.equals("<=")) {
						return new LessThanOrEqual(leftOperand, rightOperand);
					} else if (operator.equals("=")) {
						return new Equals(leftOperand, rightOperand);
					} else if (operator.equals(">=")) {
						return new GreaterThanOrEqual(leftOperand, rightOperand);
					} else if (operator.equals(">")) {
						return new GreaterThan(leftOperand, rightOperand);
					} else if ((operator.equals("!="))
							|| (operator.equals("<>"))) {
						return new NotEquals(leftOperand, rightOperand);
					} else {
						throw new RuntimeException("Unsupported operator: "
								+ operator);
					}

				} else {
					SimpleNode rightExpression = (SimpleNode) right
							.jjtGetChild(0);
					if (rightExpression instanceof ASTSQLLikeClause) {
						Expression ref = getSQLExpression((SimpleNode) node
								.jjtGetChild(0));
						Expression pattern = getExpression(rightExpression
								.jjtGetChild(0));
						Token firstToken = rightExpression.first_token;
						boolean not = (firstToken.kind == SQLEngineConstants.NOT);
						return new LikeOperator(ref, pattern, not);
					} else if (rightExpression instanceof ASTSQLInClause) {
						Expression ref = getSQLExpression((SimpleNode) node
								.jjtGetChild(0));

						// We translate into an array of "ref=value"
						Node valueList = rightExpression.jjtGetChild(0);
						if (valueList instanceof ASTSQLValueList) {
							ArrayList<Expression> equals = new ArrayList<Expression>();
							for (int i = 0; i < valueList.jjtGetNumChildren(); i++) {
								Node valueElement = valueList.jjtGetChild(i);
								if (valueElement.jjtGetNumChildren() == 0) {
									equals.add(new IsOperator(ref, false));
								} else if (valueElement.jjtGetChild(0) instanceof ASTSQLSumExpr) {
									Expression elementExpr = getSQLExpression(valueElement);
									equals.add(new Equals(ref, elementExpr));
								}
							}

							// We construct the "or" tree
							Expression last = equals.get(0);
							for (int i = 1; i < equals.size(); i++) {
								Or or = new Or(last, equals.get(i));
								last = or;
							}

							return last;
						}
					} else if (rightExpression instanceof ASTSQLLeftJoinClause) {
					} else if (rightExpression instanceof ASTSQLRightJoinClause) {
					} else if (rightExpression instanceof ASTSQLBetweenClause) {
						boolean not = (rightExpression.first_token.kind == SQLEngineConstants.NOT);
						Expression firstExpr = getSQLExpression((SimpleNode) rightExpression
								.jjtGetChild(0));
						Expression secondExpr = getSQLExpression((SimpleNode) rightExpression
								.jjtGetChild(1));
						Expression ref = getSQLExpression((SimpleNode) node
								.jjtGetChild(0));
						GreaterThan greaterThan = new GreaterThan(ref,
								firstExpr);
						LessThan lessThan = new LessThan(ref, secondExpr);
						Expression ret = new And(greaterThan, lessThan);
						if (not) {
							ret = new Not(ret);
						}

						return ret;
					}
				}
			}
		}

		if (node.jjtGetNumChildren() == 1) {
			return getExpression((SimpleNode) node.jjtGetChild(0));
		} else {
			throw new RuntimeException("not implemented: " + node);
		}
	}

	/**
	 * Gets the type of the specified node if the node consists of only one
	 * token Otherwise it returns -1
	 *
	 * @param n
	 *
	 * @return A constant in {@link SQLEngineConstants}
	 */
	private int getType(Node n) {
		SimpleNode node = (SimpleNode) n;

		if (node.first_token == node.last_token) {
			return node.first_token.kind;
		}

		return -1;
	}

	/**
	 * Gets the text of the specified node
	 *
	 * @param n
	 *
	 * @return
	 */
	private String getText(Node n) {
		SimpleNode s = (SimpleNode) n;
		String ret = "";

		for (Token tok = s.first_token; tok != s.last_token.next; tok = tok.next) {
			ret += (" " + tok.image);
		}

		return ret.trim();
	}

	private Expression buildArithmeticOperator(OperatorFactory opFactory,
			SimpleNode node) {
		if (node.jjtGetNumChildren() > 1) {
			Expression left = getExpression(node.jjtGetChild(0));
			for (int i = 1; i < node.jjtGetNumChildren(); i = i + 2) {
				String symbol = getText(node.jjtGetChild(i));
				Expression right = getExpression(node.jjtGetChild(i + 1));
				left = opFactory.instantiateOperator(symbol, left, right);
			}
			return left;
		} else {
			return null;
		}
	}

	private Expression buildBooleanOperator(OperatorFactory opFactory,
			SimpleNode node) {
		if (node.jjtGetNumChildren() > 1) {
			Expression left = getExpression(node.jjtGetChild(0));
			for (int i = 1; i < node.jjtGetNumChildren(); i++) {
				Expression right = getExpression(node.jjtGetChild(i));
				left = opFactory.instantiateOperator(null, left, right);
			}
			return left;
		} else {
			return null;
		}
	}

	private SimpleNode getChildNode(SimpleNode parent, Class<?> type) {
		for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
			Node child = parent.jjtGetChild(i);
			if (child.getClass().equals(type)) {
				return (SimpleNode) child;
			}
		}

		return null;
	}

	private interface OperatorFactory {
		Expression instantiateOperator(String symbol, Expression left,
				Expression right);
	}

	public void fillStar(AbstractStarElement ret, SimpleNode childNode)
			throws SemanticException {
		SimpleNode starModifier = getChildNode(childNode,
				ASTSQLSelectAllModifier.class);
		if (starModifier == null) {
			starModifier = getChildNode(childNode,
					ASTSQLSelectAllInTableModifier.class);
		}

		if (starModifier != null) {
			for (int i = 0; i < starModifier.jjtGetNumChildren(); i++) {
				SimpleNode modifier = (SimpleNode) starModifier.jjtGetChild(i);
				if (modifier instanceof ASTSQLSelectAllModifierExcept) {
					ret.except = new ArrayList<String>();
					String[] idSequence = getIdSequence((ASTSQLIdSequence) modifier
							.jjtGetChild(0));
					ret.except.addAll(Arrays.asList(idSequence));
				} else {
					throw new UnsupportedOperationException("Not yet supported");

				}
			}
		}
	}

	public String[] getIdSequence(ASTSQLIdSequence node) {
		int idCount = node.jjtGetNumChildren();
		String[] ret = new String[idCount];
		for (int i = 0; i < idCount; i++) {
			ret[i] = getId(node.jjtGetChild(i));
		}

		return ret;
	}

}