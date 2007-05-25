package org.gdms.data.edition;

import org.gdms.data.InternalDataSource;
import org.gdms.data.InnerDBUtils;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;

/**
 * Location info of a PK
 * 
 * @author Fernando Gonzalez Cortes
 */
public class UpdateEditionInfo extends BaseEditionInfo implements EditionInfo {
	private int index;

	private ValueCollection originalPK;

	private String tableName;

	private InternalBuffer internalBuffer;

	private InternalDataSource ds;

	private String[] pkNames;

	/**
	 * Creates a new FlagIndexPair.
	 * 
	 * @param index
	 *            Index on the data source where the pk is
	 * @param originalPK
	 *            Value of the PK fields when the edition started
	 */
	public UpdateEditionInfo(String tableName, String[] pkNames, InternalDataSource ds,
			DBDriver driver, int internalBufferIndex,
			ValueCollection originalPK, InternalBuffer internalBuffer) {
		super(driver);
		this.tableName = tableName;
		this.pkNames = pkNames;
		this.ds = ds;
		this.index = internalBufferIndex;
		this.originalPK = originalPK;
		this.internalBuffer = internalBuffer;
	}

	/**
	 * gets the index
	 * 
	 * @return int
	 */
	public int getIndex() {
		return index;
	}

	public ValueCollection getOriginalPK() {
		return originalPK;
	}

	public String getSQL() throws DriverException {
		String[] fieldNames = ds.getFieldNames();
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = internalBuffer.getFieldValue(index, i);
		}
		return InnerDBUtils.createUpdateStatement(super
				.getReferenceExpression(tableName), originalPK.getValues(),
				super.getReferenceExpression(pkNames), super
						.getReferenceExpression(fieldNames), row, driver);
	}
}
