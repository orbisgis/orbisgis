package org.orbisgis.views.geocatalog;

import org.orbisgis.editors.table.Selection;

public class ResourceSelection implements Selection {

	private int[] selection = new int[0];

	@Override
	public int[] getSelection() {
		return selection;
	}

	@Override
	public void setSelection(int[] indexes) {
		this.selection = indexes;
		// ListSelectionModel model = table.getSelectionModel();
		// model.setValueIsAdjusting(true);
		// model.clearSelection();
		// for (int i : indexes) {
		// model.addSelectionInterval(i, i);
		// }
		// model.setValueIsAdjusting(false);
	}

	@Override
	public void clearSelection() {
		selection = new int[0];
	}

	@Override
	public void selectInterval(int init, int end) {
		selection = new int[end - init + 1];
		for (int i = init; i <= end; i++) {
			selection[i - init] = i;
		}
	}

}
