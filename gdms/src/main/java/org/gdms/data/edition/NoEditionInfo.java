package org.gdms.data.edition;

import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public class NoEditionInfo implements EditionInfo {

	private ValueCollection pk;

	private int index;

	/**
	 * Indicates that no edition was done at the record
	 * 
	 * @param pk
	 *            Original primary key value
	 * @param index
	 *            Index in the original source
	 */
	public NoEditionInfo(ValueCollection pk, int index) {
		this.pk = pk;
		this.index = index;
	}

	public String getSQL() throws DriverException {
		return null;
	}

	public ValueCollection getPk() {
		return pk;
	}

	public int getIndex() {
		return index;
	}

}
