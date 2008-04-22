package org.gdms.sql.strategies;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class ColumnMappedDriver extends AbstractBasicSQLDriver implements
		ObjectDriver {

	private Metadata metadata = null;
	private ObjectDriver source;
	private int[] columnMap;

	public ColumnMappedDriver(ObjectDriver source, int[] columnMap)
			throws DriverException {
		DefaultMetadata dm = new DefaultMetadata();
		Metadata sourceMetadata = source.getMetadata();
		for (Integer column : columnMap) {
			dm.addField(sourceMetadata.getFieldName(column), sourceMetadata
					.getFieldType(column));
		}

		metadata = dm;
		this.source = source;
		this.columnMap = columnMap;
	}

	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return source.getFieldValue(rowIndex, columnMap[fieldId]);
	}

	public long getRowCount() throws DriverException {
		return source.getRowCount();
	}

}
