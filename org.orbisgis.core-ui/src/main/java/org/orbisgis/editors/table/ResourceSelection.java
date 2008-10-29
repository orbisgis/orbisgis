package org.orbisgis.editors.table;


public class ResourceSelection implements Selection {

	private int[] rowIndexes;

	public ResourceSelection(int[] rowIndexes) {
		this.rowIndexes = rowIndexes;
	}

	@Override
	public int[] getSelection() {
		return rowIndexes;
	}

	@Override
	public void setSelection(int[] indexes) {
		this.rowIndexes = indexes;
	}

}
