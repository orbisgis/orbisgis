package org.gdms.data.edition;

import org.gdms.data.InnerDBUtils;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;

public class DeleteEditionInfo extends BaseEditionInfo implements EditionInfo {

	private String tableName;

	private String[] pkNames;

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
	public DeleteEditionInfo(ValueCollection pk, String[] pkNames,
			String tableName, DBDriver driver) {
		super(driver);
		this.pk = pk;
		this.pkNames = pkNames;
		this.tableName = tableName;
	}

	public String getSQL() throws DriverException {
		return InnerDBUtils.createDeleteStatement(pk.getValues(),
				getReferenceExpression(pkNames), super
						.getReferenceExpression(tableName), driver);
	}

}
