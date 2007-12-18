package org.gdms.sql.strategies.algebraic;

import org.gdms.data.DataSource;

public interface Operator extends TreeNode {
	public DataSource getDataSource();
}
