package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.IProgressMonitor;

public class GroupByOperator extends AbstractExpressionOperator implements
		Operator, ChangesMetadata {

	public static final String FIELD_PREFIX = "groupby";
	private ArrayList<Expression> fields = new ArrayList<Expression>();
	private int offset = -1;
	private int limit = -1;

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			ObjectDriver source = getOperator(0).getResult(pm);
			if (pm.isCancelled()) {
				return null;
			} else {
				DefaultFieldContext fieldContext = new DefaultFieldContext(
						source);
				// get the functions and attributes index into an array
				ArrayList<Integer> fieldIndexes = new ArrayList<Integer>();
				ArrayList<Expression> initialExpressions = new ArrayList<Expression>();
				for (Expression expr : fields) {
					if (expr instanceof Field) {
						String fieldName = ((Field) expr).getFieldName();
						fieldIndexes
								.add(getFieldIndexByName(source, fieldName));
					} else {
						initialExpressions.add(expr);
						for (Field field : expr.getFieldReferences()) {
							field.setFieldContext(fieldContext);
						}
					}
				}

				// create a map for the group by classes and it's expressions
				// and
				// results
				HashMap<ValueCollection, Expression[]> classExpressions = new HashMap<ValueCollection, Expression[]>();
				HashMap<ValueCollection, Value[]> classResults = new HashMap<ValueCollection, Value[]>();

				pm.startTask("Creating the groups");
				// Iterate throughout the source
				long rowCount = source.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					if (i / 1000 == i / 1000.0) {
						if (pm.isCancelled()) {
							return null;
						} else {
							pm.progressTo((int) (100 * i / rowCount));
						}
					}
					fieldContext.setIndex(i);
					Value[] groupByValues = new Value[fieldIndexes.size()];
					for (int j = 0; j < fieldIndexes.size(); j++) {
						groupByValues[j] = source.getFieldValue(i, fieldIndexes
								.get(j));
					}
					// Get the expressions for this class or create a new one
					ValueCollection vc = ValueFactory
							.createValue(groupByValues);
					Expression[] exprs = classExpressions.get(vc);
					Value[] results = classResults.get(vc);
					if (exprs == null) {
						Value[] newClassResults = new Value[initialExpressions
								.size()];
						Expression[] newClassExpressions = new Expression[newClassResults.length];
						for (int k = 0; k < newClassExpressions.length; k++) {
							newClassExpressions[k] = initialExpressions.get(k)
									.cloneExpression();
						}
						classExpressions.put(vc, newClassExpressions);
						classResults.put(vc, newClassResults);
						exprs = newClassExpressions;
						results = newClassResults;
					}
					for (int k = 0; k < exprs.length; k++) {
						Expression expression = exprs[k];
						Value res = expression.evaluate();
						results[k] = res;
					}
				}
				pm.endTask();

				pm.startTask("Calculating aggregates");
				ObjectMemoryDriver omd = new ObjectMemoryDriver(
						getResultMetadata());
				Iterator<ValueCollection> it = classExpressions.keySet()
						.iterator();
				int index = 0;
				while (it.hasNext()) {
					index++;
					if (index / 1000 == index / 1000.0) {
						if (pm.isCancelled()) {
							return null;
						} else {
							pm
									.progressTo(100 * index
											/ classExpressions.size());
						}
					}
					ValueCollection groupByClass = it.next();
					Value[] fieldValues = groupByClass.getValues();
					Value[] exprResults = classResults.get(groupByClass);
					Value[] row = new Value[fields.size()];
					int fieldValuesIndex = 0;
					int exprResultIndex = 0;
					for (int i = 0; i < row.length; i++) {
						if (fields.get(i) instanceof Field) {
							row[i] = fieldValues[fieldValuesIndex];
							fieldValuesIndex++;
						} else {
							row[i] = exprResults[exprResultIndex];
							exprResultIndex++;
						}
					}
					omd.addValues(row);
				}
				pm.endTask();

				if ((limit != -1) || (offset != -1)) {
					return new LimitOffsetDriver(limit, offset, omd);
				} else {
					return omd;
				}
			}
		} catch (DriverException e) {
			throw new ExecutionException("Cannot access data to group", e);
		} catch (EvaluationException e) {
			throw new ExecutionException(
					"Problem while evaluating expressions", e);
		}
	}

	/**
	 * The result metadata of a group by consists of the fields in the group by
	 * clause and one field for each of the aggregated function in the select
	 * clause. This aggregated functions will have as name the string 'groupby'
	 * concatenated with the index in the field array of the resulting metadata
	 *
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		DefaultMetadata ret = new DefaultMetadata();
		for (int i = 0; i < fields.size(); i++) {
			Expression expression = fields.get(i);
			String fieldName = FIELD_PREFIX + i;
			if (expression instanceof Field) {
				fieldName = ((Field) expression).getFieldName();
			}
			ret.addField(fieldName, expression.getType());
		}

		return ret;
	}

	public void addField(Field field) {
		fields.add(field);
	}

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return fields.toArray(new Expression[0]);
	}

	public void addAggregatedFunction(Expression[] expressions) {
		for (Expression expression : expressions) {
			fields.add(expression);
		}
	}

	public ArrayList<Field> getGroupByField() {
		ArrayList<Field> ret = new ArrayList<Field>();
		for (Expression field : fields) {
			if (field instanceof Field) {
				ret.add(((Field) field));
			}
		}

		return ret;
	}

	public int getFieldIndex(Field field) throws DriverException,
			SemanticException {
		String tableName = field.getTableName();
		String fieldName = field.getFieldName();
		int fieldIndex = -1;
		// If the field contains a table reference we just look it
		if (tableName != null) {
			for (int i = 0; i < fields.size(); i++) {
				Expression expression = fields.get(i);
				if (expression instanceof Field) {
					Field groupByField = (Field) expression;
					if (tableName.equals(groupByField.getTableName())
							&& field.getFieldName().equals(
									groupByField.getFieldName())) {
						return i;
					}
				}
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

		if (fieldIndex == -1) {
			throw new SemanticException("The field " + field + " could not be "
					+ "resolved. Check it's on the group by clause or"
					+ " inside an aggregate function.");
		} else {
			return fieldIndex;
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
}
