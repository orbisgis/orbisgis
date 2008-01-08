package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;

public class ProjectionOp extends DefaultOperator implements Operator,
		OperatorWithFieldReferences {

	private String[] aliases;
	private Expression[] expressions;
	private ArrayList<Operator> dependencies = new ArrayList<Operator>();

	public ProjectionOp() {
		this.expressions = null;
		this.aliases = null;
	}

	public ProjectionOp(Expression[] expressions, String[] aliases) {
		this.expressions = expressions;
		this.aliases = aliases;
	}

	public DataSource getDataSource() throws ExecutionException {
		if (expressions == null) {
			return childs.get(0).getDataSource();
		} else {
			return new ProjectionPipeline(childs.get(0).getDataSource(),
					expressions, aliases);
		}
	}

	public Field[] getFieldReferences() {
		ArrayList<Field> ret = new ArrayList<Field>();
		ArrayList<Expression> bag = new ArrayList<Expression>();
		for (Expression expression : expressions) {
			bag.add(expression);
		}

		while (!bag.isEmpty()) {
			Expression exp = bag.remove(0);
			if (exp instanceof Field) {
				ret.add((Field) exp);
			}
			Expression leftOperator = exp.getLeftOperator();
			if (leftOperator != null) {
				bag.add(leftOperator);
			}
			Expression rightOperator = exp.getRightOperator();
			if (rightOperator != null) {
				bag.add(rightOperator);
			}
		}

		return ret.toArray(new Field[0]);
	}

	public void setDependency(Operator operator) {
		dependencies.add(operator);
	}

}
