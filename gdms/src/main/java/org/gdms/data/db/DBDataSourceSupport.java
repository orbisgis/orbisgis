package org.gdms.data.db;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;

public class DBDataSourceSupport {

	protected DBDriver driver;

	protected DBSource def;

	private int[] cachedPKIndices;

	protected DataSource ds;

	public DBDataSourceSupport(DataSource ds, DBSource def, DBDriver driver) {
		this.driver = driver;
		this.def = def;
		this.ds = ds;
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return driver.getFieldValue(rowIndex, fieldId);
	}

	public long getOriginalRowCount() throws DriverException {
		return driver.getRowCount();
	}

	/**
	 * Gets the value of the primary key just before the beginTrans call
	 * 
	 * @param rowIndex
	 *            Index of the row
	 * @return
	 * @throws DriverException
	 * @throws InvalidTypeException
	 */
	public ValueCollection getPKValue(long rowIndex) throws DriverException {
		int[] fieldsId = getPrimaryKeys();
		Value[] pks = new Value[fieldsId.length];

		for (int i = 0; i < pks.length; i++) {
			pks[i] = ds.getFieldValue(rowIndex, fieldsId[i]);
		}

		return ValueFactory.createValue(pks);
	}

	/**
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws DriverException {
		if (cachedPKIndices == null) {
			cachedPKIndices = MetadataUtilities.getPKIndices(getMetadata());
		}
		return cachedPKIndices;
	}

	public String getPKName(int fieldId) throws DriverException {
		int[] fieldsId = getPrimaryKeys();
		return getMetadata().getFieldName(fieldsId[fieldId]);
	}

	public int getPKCardinality() throws DriverException {
		return getPrimaryKeys().length;
	}

	public String[] getPKNames() throws DriverException {
		final String[] ret = new String[getPKCardinality()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getPKName(i);
		}

		return ret;
	}

	public Metadata getMetadata() throws DriverException {
		return driver.getMetadata();
	}

	public DBDriver getDriver() {
		return driver;
	}
}