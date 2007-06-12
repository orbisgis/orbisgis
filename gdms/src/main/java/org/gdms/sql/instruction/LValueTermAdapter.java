/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Adaptador
 */
public class LValueTermAdapter extends AbstractExpression {

	private String fieldName;

	private String tableName;

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		if (fieldName == null) {
			fillFieldAndTableName();
		}

		return fieldName;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getTableName() {
		if (tableName == null) {
			fillFieldAndTableName();
		}

		return tableName;
	}

	private void fillFieldAndTableName() {
		String ss = Utilities.getText(getEntity());
		String[] ret = ss.split("\\Q.\\E");
		if (ret.length == 1) {
			fieldName = ret[0];
		} else {
			tableName = ret[0];
			fieldName = ret[1];
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		try {
			return getInstructionContext().getFieldValue(getTableName(), getFieldName());
		} catch (DriverException e) {
			throw new EvaluationException(e);
		} catch (AmbiguousFieldNameException e) {
			throw new EvaluationException(e);
		} catch (FieldNotFoundException e) {
			throw new EvaluationException(e);
		}
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}

	/**
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		DataSource ds = getInstructionContext().getDs();
		return ds.getFieldType(ds.getFieldIndexByName(getFieldName())).getTypeCode();
	}

	public String getFieldTable() throws DriverException {
		try {
			return getInstructionContext().getTableName(getTableName(), getFieldName());
		} catch (AmbiguousFieldNameException e) {
			throw new DriverException(e);
		} catch (FieldNotFoundException e) {
			throw new DriverException(e);
		}
	}

	public IndexHint[] getFilters() {
		return new IndexHint[0];
	}
}
