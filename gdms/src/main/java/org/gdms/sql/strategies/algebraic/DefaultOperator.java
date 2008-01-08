package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;

public abstract class DefaultOperator implements Operator {

	protected ArrayList<Operator> childs = new ArrayList<Operator>();

	public void addChild(Operator operator) {
		childs.add(operator);
	}

	public String toString() {
		String ret = this.getClass().getSimpleName() + "(";
		for (int i = 0; i < childs.size(); i++) {
			ret = ret + childs.get(i);
		}
		return ret + ")";
	}

	public void addChilds(Operator[] childOperators) {
		for (Operator operator : childOperators) {
			addChild(operator);
		}
	}

	public DataSource getDataSource() throws ExecutionException {
		// TODO Remove this method
		return null;
	}

	public Operator getOperator(int i) {
		return childs.get(i);
	}

	public int getOperatorCount() {
		return childs.size();
	}

}
