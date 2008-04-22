package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class ScalarProductDriver extends AbstractMetadataSQLDriver implements
		ObjectDriver {

	private ObjectDriver leftSource;
	private ObjectDriver rightSource;

	private ArrayList<int[]> indexes = new ArrayList<int[]>();
	private int leftFieldCount;

	public ScalarProductDriver(ObjectDriver left, int leftFieldCount,
			ObjectDriver right, Metadata metadata) {
		super(metadata);
		this.leftSource = left;
		this.rightSource = right;
		this.leftFieldCount = leftFieldCount;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int[] rowArray = indexes.get((int) rowIndex);
		if (fieldId < leftFieldCount) {
			return leftSource.getFieldValue(rowArray[0], fieldId);
		} else {
			return rightSource.getFieldValue(rowArray[1], fieldId
					- leftFieldCount);
		}
	}

	public long getRowCount() throws DriverException {
		return indexes.size();
	}

	public void addRow(int... indexes) {
		this.indexes.add(indexes);
	}

}
