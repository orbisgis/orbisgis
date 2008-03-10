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
import org.orbisgis.IProgressMonitor;

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

		if (fieldIndex == -1) {
			throw new SemanticException("Field not found: " + field.toString());
		} else {
			return fieldIndex;
		}
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
		Expression selectionExpression = op.getExpressions()[0];
		Expression[] ands = selectionExpression.splitAnds();
		op.setExpressions(ands);
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

}
