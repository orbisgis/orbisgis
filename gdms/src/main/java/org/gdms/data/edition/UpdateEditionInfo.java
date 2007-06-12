package org.gdms.data.edition;

import org.gdms.data.InnerDBUtils;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

/**
 * Location info of a PK
 *
 * @author Fernando Gonzalez Cortes
 */
public class UpdateEditionInfo extends OriginalEditionInfo {

	private ValueCollection originalPK;

	private PhysicalDirection dir;

	/**
	 * Creates a new FlagIndexPair.
	 *
	 * @param index
	 *            Index on the data source where the pk is
	 * @param originalPK
	 *            Value of the PK fields when the edition started
	 */
	public UpdateEditionInfo(ValueCollection originalPK, PhysicalDirection dir) {
		this.originalPK = originalPK;
		this.dir = dir;
	}

	public ValueCollection getOriginalPK() {
		return originalPK;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver) throws DriverException {
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = dir.getFieldValue(i);
		}
		return InnerDBUtils.createUpdateStatement(super.getReferenceExpression(
				driver, tableName), originalPK.getValues(), super
				.getReferenceExpression(driver, pkNames), super
				.getReferenceExpression(driver, fieldNames), row, driver);
	}

	@Override
	public ValueCollection getPK() {
		return originalPK;
	}
}
