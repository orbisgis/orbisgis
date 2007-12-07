package org.gdms.sql.evaluator;

import org.gdms.data.DataSource;

public class EvaluationContext {

	private DataSource ds;
	private long rowIndex;

	public EvaluationContext(DataSource ds, long rowIndex) {
		super();
		this.ds = ds;
		this.rowIndex = rowIndex;
	}

	public DataSource getDataSource() {
		return ds;
	}

	public long getRowIndex() {
		return rowIndex;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public void setRowIndex(long rowIndex) {
		this.rowIndex = rowIndex;
	}

}
