package org.gdms.data.edition;

import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

public class DeleteEditionInfo extends OriginalEditionInfo implements EditionInfo {

	private ValueCollection pk;

	/**
	 * Creates a new DeleteEditionInfo
	 *
	 * @param pk
	 *            Original primary key of the object to be removed
	 * @param pkNames
	 *            Names of the primary key fields
	 * @param tableName
	 *            Name of the table being edited
	 * @param driver
	 *            driver used to edit
	 */
	public DeleteEditionInfo(ValueCollection pk) {
		this.pk = pk;
	}

	@Override
	public ValueCollection getPK() {
		return pk;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		return driver.getDeleteRecordSQL(tableName, pkNames, pk.getValues());
	}

}
