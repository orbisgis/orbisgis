package org.gdms.data.indexes;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.Literal;

public class ExpressionBasedAlphaQuery implements ExpressionBasedIndexQuery,
		AlphaQuery {

	private Expression min;
	private boolean minIncluded;
	private boolean maxIncluded;
	private Expression max;
	private String fieldName;

	public ExpressionBasedAlphaQuery(String fieldName, Expression exp) {
		this(fieldName, exp, true, exp, true);
	}

	public ExpressionBasedAlphaQuery(String fieldName, Expression min,
			boolean minIncluded, Expression max, boolean maxIncluded) {
		this.min = min;
		this.minIncluded = minIncluded;
		this.max = max;
		this.maxIncluded = maxIncluded;
		this.fieldName = fieldName;

		if (this.min == null) {
			this.min = new Literal(ValueFactory.createNullValue());
		}

		if (this.max == null) {
			this.max = new Literal(ValueFactory.createNullValue());
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isStrict() {
		return true;
	}

	public Value getMin() throws EvaluationException {
		return min.evaluate();
	}

	public boolean isMinIncluded() {
		return minIncluded;
	}

	public boolean isMaxIncluded() {
		return maxIncluded;
	}

	public Value getMax() throws EvaluationException {
		return max.evaluate();
	}

	public Field[] getFields() {
		ArrayList<Field> ret = new ArrayList<Field>();
		addFields(ret, min);
		addFields(ret, max);

		return ret.toArray(new Field[0]);
	}

	private void addFields(ArrayList<Field> ret, Expression expression) {
		Field[] fields = expression.getFieldReferences();
		for (Field field : fields) {
			ret.add(field);
		}
	}

}
