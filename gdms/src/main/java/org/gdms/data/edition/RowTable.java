package org.gdms.data.edition;

import java.util.ArrayList;

public class RowTable {
	public static final int EDITED_SOURCE = 0;

	public static final int EXPANSION_FILE = 1;

	private ArrayList<Integer> realIndices = new ArrayList<Integer>();

	private ArrayList<Integer> locations = new ArrayList<Integer>();

	private ArrayList<Integer> backRealIndices;

	private ArrayList<Integer> backLocations;

	public void initialize(long rowCount) {
		for (int i = 0; i < rowCount; i++) {
			realIndices.add(i, i);
			locations.add(i, EDITED_SOURCE);
		}
	}

	public int getIndexLocation(int virtualIndex) {
		return realIndices.get(virtualIndex);
	}

	public int getIndexFile(int virtualIndex) {
		return locations.get(virtualIndex);
	}

	public void setIndexLocation(int virtualIndex, int realIndex) {
		realIndices.set(virtualIndex, realIndex);
	}

	public void setIndexFile(int virtualIndex, int location) {
		locations.set(virtualIndex, location);
	}

	public void addRow(int realIndex) {
		realIndices.add(realIndex);
		locations.add(EXPANSION_FILE);
	}

	public void addRowAt(int virtualIndex, int realIndex) {
		realIndices.add(virtualIndex, realIndex);
		locations.add(virtualIndex, EXPANSION_FILE);
	}

	public void deleteRow(int virtualIndex) {
		realIndices.remove(virtualIndex);
		locations.remove(virtualIndex);
	}

	public long getRowCount() {
		return realIndices.size();
	}

	@SuppressWarnings("unchecked")
	public void saveStatus() {
		backRealIndices = (ArrayList<Integer>) realIndices.clone();
		backLocations = (ArrayList<Integer>) locations.clone();
	}

	public void restoreStatus() {
		realIndices = backRealIndices;
		locations = backLocations;
	}
}
