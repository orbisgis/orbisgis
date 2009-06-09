package org.orbisgis.core.ui.views.geocatalog;

import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.SelectionListener;

public class SourceSelection implements Selection {

	private int[] selection = new int[0];
	private SelectionListener listener;

	@Override
	public int[] getSelectedRows() {
		return selection;
	}

	@Override
	public void setSelectedRows(int[] indexes) {
		this.selection = indexes;
		listener.selectionChanged();
	}

	@Override
	public void clearSelection() {
		selection = new int[0];
		listener.selectionChanged();
	}

	@Override
	public void selectInterval(int init, int end) {
		selection = new int[end - init + 1];
		for (int i = init; i <= end; i++) {
			selection[i - init] = i;
		}
		listener.selectionChanged();
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		if (listener == this.listener) {
			this.listener = null;
		}
	}

	@Override
	public void setSelectionListener(SelectionListener listener) {
		this.listener = listener;
	}

}
