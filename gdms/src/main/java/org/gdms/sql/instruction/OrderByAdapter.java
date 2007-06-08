package org.gdms.sql.instruction;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class OrderByAdapter extends Adapter {
	public int getFieldCount() {
		return getChilds()[0].getChilds().length;
	}

	public String getFieldName(int index) {
		return ((Expression) getChilds()[0].getChilds()[index].getChilds()[0])
				.getFieldName();
	}

	public int getOrder(int index) {
		OrderByElemAdapter obe = (OrderByElemAdapter) getChilds()[0]
				.getChilds()[index];
		if (obe.getChilds().length == 2) {
			String order = obe.getChilds()[1].getEntity().first_token.image
					.toLowerCase();

			if (order.equals("asc")) {
				return SelectAdapter.ORDER_ASC;
			} else {
				return SelectAdapter.ORDER_DESC;
			}
		} else {
			return SelectAdapter.ORDER_ASC;
		}
	}
}
