package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class FilteredProductDataSourceDecorator extends
		ScalarProductDataSource {

	private ArrayList<int[]> indexes = new ArrayList<int[]>();

	public FilteredProductDataSourceDecorator(DataSource[] tables) {
		this.tables = tables;
	}

	public void addRow(int[] indexes) {
		int[] ri = new int[indexes.length];
		System.arraycopy(indexes, 0, ri, 0, indexes.length);
		this.indexes.add(ri);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int[] indexes = this.indexes.get((int) rowIndex);
		int tableIndex = getTableIndexByFieldId(fieldId);
		int row = indexes[tableIndex];
		int fieldIndex = getFieldIndex(fieldId);
		return tables[tableIndex].getFieldValue(row, fieldIndex);
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				return tables[getTableIndexByFieldId(fieldId)]
						.getMetadata().getFieldName(
								getFieldIndex(fieldId));
			}

			public Type getFieldType(int fieldId) throws DriverException {
				int table = getTableIndexByFieldId(fieldId);

				return tables[table].getMetadata().getFieldType(
						getFieldIndex(fieldId));
			}

			public int getFieldCount() throws DriverException {
				int ret = 0;

				for (int i = 0; i < tables.length; i++) {
					ret += tables[i].getMetadata().getFieldCount();
				}

				return ret;
			}

		};
	}

	public long getRowCount() throws DriverException {
		return indexes.size();
	}

	public boolean isOpen() {
		return tables[0].isOpen();
	}

}
