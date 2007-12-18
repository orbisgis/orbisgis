package org.gdms.sql.strategies.algebraic;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.ASTSQLAndExpr;
import org.gdms.sql.parser.ASTSQLBetweenClause;
import org.gdms.sql.parser.ASTSQLCompareExpr;
import org.gdms.sql.parser.ASTSQLInClause;
import org.gdms.sql.parser.ASTSQLLeftJoinClause;
import org.gdms.sql.parser.ASTSQLLikeClause;
import org.gdms.sql.parser.ASTSQLLiteral;
import org.gdms.sql.parser.ASTSQLLvalueTerm;
import org.gdms.sql.parser.ASTSQLNotExpr;
import org.gdms.sql.parser.ASTSQLOrExpr;
import org.gdms.sql.parser.ASTSQLProductExpr;
import org.gdms.sql.parser.ASTSQLRightJoinClause;
import org.gdms.sql.parser.ASTSQLSelect;
import org.gdms.sql.parser.ASTSQLSelectCols;
import org.gdms.sql.parser.ASTSQLSelectList;
import org.gdms.sql.parser.ASTSQLSumExpr;
import org.gdms.sql.parser.ASTSQLTableList;
import org.gdms.sql.parser.ASTSQLUnaryExpr;
import org.gdms.sql.parser.ASTSQLWhere;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;

public class LogicPlanner {

	private DataSourceFactory dsf;

	public LogicPlanner(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("data", new FileSourceDefinition(
				"src/test/resources/alltypes.dbf"));
		String sql = "select FSTR from data d, data d2;";

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));

		parser.SQLStatement();
		LogicPlanner lp = new LogicPlanner(dsf);
		Operator op = (Operator) lp.getOperator((SimpleNode) parser
				.getRootNode());
		DataSource ds = op.getDataSource();
		System.out.println(op);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();

		ds = dsf.getDataSource("data");
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	/**
	 * Recursive method that returns the root of the tree of logic operators
	 * that implement the query. Returns null if no operator has to be applied
	 *
	 * @param node
	 * @return
	 * @throws SemanticException
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	private TreeNode getOperator(Node theNode) throws SemanticException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		SimpleNode node = (SimpleNode) theNode;

		if (node instanceof ASTSQLSelect) {
			TreeNode ret;
			TreeNode escalarProductOp = getOperator(node.jjtGetChild(1));
			ret = escalarProductOp;
			if (node.jjtGetNumChildren() == 3) {
				TreeNode selectionOp = getOperator(node.jjtGetChild(2));
				selectionOp.addChild(escalarProductOp);
				ret = selectionOp;
			}
			TreeNode projOp = getOperator(node.jjtGetChild(0));
			if (projOp != null) {
				projOp.addChild(ret);
				ret = projOp;
			}

			return ret;
		} else if (node instanceof ASTSQLSelectCols) {
			if (node.first_token.image.equals("*")) {
				ProjectionOp ret = new ProjectionOp();
				return ret;
			} else {
				// SQLSelectList
				return getOperator(node.jjtGetChild(0));
			}
		} else if (node instanceof ASTSQLSelectList) {
			ProjectionOp ret = new ProjectionOp(
					getExpressionAliasPairs((ASTSQLSelectList) node));
			return ret;
		} else if (node instanceof ASTSQLTableList) {
			ScalarProductOp ret = new ScalarProductOp();
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				SimpleNode child = (SimpleNode) node.jjtGetChild(i);
				String tableRef = child.first_token.image;
				String alias = null;
				if (child.first_token != child.last_token) {
					alias = child.last_token.image;
				}
				ret.addTable(dsf, tableRef, alias);
			}
			return ret;
		} else if (node instanceof ASTSQLWhere) {
			SelectionOp ret = new SelectionOp();
			ret.setExpression((Expr) getOperator(node.jjtGetChild(0)));
			return ret;
		} else if (node instanceof ASTSQLOrExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				OrExpr ret = new OrExpr();
				ret.addChilds(getChildOperators(node));
				return ret;
			}
		} else if (node instanceof ASTSQLAndExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				AndExpr ret = new AndExpr();
				ret.addChilds(getChildOperators(node));
				return ret;
			}
		} else if (node instanceof ASTSQLNotExpr) {
			if (node.first_token.kind != SQLEngineConstants.NOT) {
				return getOperator(node.jjtGetChild(0));
			} else {
				NotExpr ret = new NotExpr();
				ret.addChild(getOperator(node));
				return ret;
			}
		} else if (node instanceof ASTSQLCompareExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				// Comparison
				SimpleNode compareExprRight = (SimpleNode) node.jjtGetChild(1);
				if (compareExprRight instanceof ASTSQLLikeClause) {
					LikeOp ret = new LikeOp();
					ret.setExpression((Expr) getOperator(node.jjtGetChild(0)));
					ret.setPattern((Expr) getOperator(compareExprRight
							.jjtGetChild(0)));
					return ret;
				} else if (compareExprRight instanceof ASTSQLInClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLLeftJoinClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLRightJoinClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLBetweenClause) {
					throw new RuntimeException();
				} else {
					SimpleNode operator = (SimpleNode) compareExprRight
							.jjtGetChild(0);
					ComparisonOp ret = new ComparisonOp();
					ret.setArithmeticOperator(operator.first_token.kind);
					ret.setLeftExpression((Expr) getOperator(node
							.jjtGetChild(0)));
					ret.setRightExpression((Expr) getOperator(compareExprRight
							.jjtGetChild(1)));
					return ret;
				}
			}
		} else if (node instanceof ASTSQLSumExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				SumOp ret = new SumOp();
				TreeNode[] expressions = getChildOperators(node);
				ret.addChilds(expressions);
				return ret;
			}
		} else if (node instanceof ASTSQLProductExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				ProductOp ret = new ProductOp();
				TreeNode[] expressions = getChildOperators(node);
				ret.addChilds(expressions);
				return ret;
			}
		} else if (node instanceof ASTSQLUnaryExpr) {
			if (!node.first_token.image.equals("-")) {
				return getOperator(node.jjtGetChild(0));
			} else {
				NegativeOp ret = new NegativeOp();
				ret.addChild(getOperator(node));
				return ret;
			}
		} else if (node instanceof ASTSQLLvalueTerm) {
			FieldOp ret = new FieldOp();
			if (node.first_token == node.last_token) {
				ret.setField(null, node.first_token.image);
			} else {
				ret.setField(node.first_token.image, node.last_token.image);
			}
			return ret;
		} else if (node instanceof ASTSQLLiteral) {
			LiteralOp ret = new LiteralOp();
			ret.setLiteral(ValueFactory.createValue(node.first_token.image,
					node.first_token.kind));
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

	private TreeNode[] getChildOperators(SimpleNode node)
			throws SemanticException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		TreeNode[] ret = new TreeNode[node.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			ret[i] = getOperator(node.jjtGetChild(i));
		}

		return ret;
	}

	private HashMap<Expr, String> getExpressionAliasPairs(ASTSQLSelectList node) {
		//TODO
		HashMap<Expr, String> ret = new HashMap<Expr, String>();
		ret.put(new Expr() {

		}, "campo");

		return ret;
	}
}