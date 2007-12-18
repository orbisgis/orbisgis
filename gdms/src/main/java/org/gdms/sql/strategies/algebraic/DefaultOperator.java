package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;

public abstract class DefaultOperator implements TreeNode {

	private ArrayList<TreeNode> childs = new ArrayList<TreeNode>();

	public void addChild(TreeNode operator) {
		childs.add(operator);
	}

	public String toString() {
		String ret = this.getClass().getSimpleName() + "(";
		for (int i = 0; i < childs.size(); i++) {
			ret = ret + childs.get(i);
		}
		return ret + ")";
	}

	public void addChilds(TreeNode[] childOperators) {
		for (TreeNode operator : childOperators) {
			addChild(operator);
		}
	}

}
