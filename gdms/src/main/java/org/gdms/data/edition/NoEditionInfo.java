package org.gdms.data.edition;

import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;


public class NoEditionInfo extends OriginalEditionInfo {

    private ValueCollection pk;
    private int index;

    /**
     * Indicates that no edition was done at the record
     *
     * @param pk Original primary key value
     * @param index Index in the original source
     */
    public NoEditionInfo(ValueCollection pk, int index) {
        this.pk = pk;
        this.index = index;
    }

    public ValueCollection getPk() {
        return pk;
    }

    public int getIndex() {
        return index;
    }

	public String getSQL(String tableName, String[] pkNames, String[] fieldNames, DBReadWriteDriver driver) throws DriverException {
		return null;
	}

	@Override
	public ValueCollection getPK() {
		return pk;
	}

}
