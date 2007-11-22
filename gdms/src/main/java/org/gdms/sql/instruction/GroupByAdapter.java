package org.gdms.sql.instruction;

public class GroupByAdapter extends Adapter {

	public String[] getGroupByFieldNames() {
		int count = getChilds()[0].getChilds().length;
		String[] ret = new String[count];
		for (int i = 0; i < count; i++) {
			ret[i] = ((Expression) getChilds()[0].getChilds()[i].getChilds()[0])
					.getFieldName();
		}

		return ret;
	}
}
