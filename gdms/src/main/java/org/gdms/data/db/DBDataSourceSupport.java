package org.gdms.data.db;

import org.gdms.data.DataSource;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

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
	 * @see org.gdms.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws DriverException {
		if (cachedPKIndices == null) {
			String[] pkNames = getDriverMetadata().getPrimaryKeys();
			cachedPKIndices = new int[pkNames.length];

			for (int i = 0; i < cachedPKIndices.length; i++) {
				DriverMetadata dmd = getDriverMetadata();
				cachedPKIndices[i] = -1;
				for (int j = 0; j < dmd.getFieldCount(); j++) {
					if (dmd.getFieldName(j).equals(pkNames[i])) {
						cachedPKIndices[i] = j;
						break;
					}
				}
				if (cachedPKIndices[i] == -1) {
					throw new RuntimeException();
				}
			}
		}

		return cachedPKIndices;
	}

	public String getPKName(int fieldId) throws DriverException {
		int[] fieldsId = getPrimaryKeys();

		return getDriverMetadata().getFieldName(fieldsId[fieldId]);
	}

	public int getPKCardinality() throws DriverException {
		return getPrimaryKeys().length;
	}

	public String[] getPKNames() throws DriverException {
		String[] ret = new String[getPKCardinality()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getPKName(i);
		}

		return ret;
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		DriverMetadata dmd = getDriverMetadata();
		String[] pkNames = getPKNames();
		boolean[] readOnly = new boolean[dmd.getFieldCount()];
		for (int i = 0; i < readOnly.length; i++) {
			readOnly[i] = driver.isReadOnly(i);
		}
		return new DefaultMetadata(dmd, driver, readOnly, pkNames);
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		return driver.getDriverMetadata();
	}

	public String check(Field field, Value value) throws DriverException {
		return driver.check(field, value);
	}

	public DBDriver getDriver() {
		return driver;
	}

}
