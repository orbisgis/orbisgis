package org.gdms.sql.strategies.algebraic;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;

public interface Operator {

	public DataSource getDataSource() throws ExecutionException;

	public void addChild(Operator operator);

	public Operator getOperator(int i);

	public int getOperatorCount();

}
