package org.gdms.sql.instruction;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public abstract class IndexHint {
	protected String table;

	protected String field;

	protected Expression filteringExpression;

	public IndexHint(String fieldTable, String fieldName, Expression filteringExpression) {
		super();
		this.table = fieldTable;
		this.field = fieldName;
		this.filteringExpression = filteringExpression;
	}

	public String getField() {
		return field;
	}

	public Expression getFilteringExpression() {
		return filteringExpression;
	}

	public String getTable() {
		return table;
	}

	public abstract Iterator<Row> getRowIterator(DataSource ds, Value filteringValue) throws DriverException;
}
