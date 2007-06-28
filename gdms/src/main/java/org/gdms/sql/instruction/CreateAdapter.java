package org.gdms.sql.instruction;

public class CreateAdapter extends Adapter {

	public String getTableName() {
		return getEntity().first_token.next.next.image;
	}

}
