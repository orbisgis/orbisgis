package org.orbisgis.editors.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ResourceSelection implements Selection {

	private JTable table;

	public ResourceSelection(JTable table) {
		this.table = table;
	}

	@Override
	public int[] getSelection() {
		return table.getSelectedRows();
	}

	@Override
	public void setSelection(int[] indexes) {
		ListSelectionModel model = table.getSelectionModel();
		model.setValueIsAdjusting(true);
		model.clearSelection();
		for (int i : indexes) {
			model.addSelectionInterval(i, i);
		}
		model.setValueIsAdjusting(false);
	}

	@Override
	public void clearSelection() {
		table.getSelectionModel().clearSelection();
	}

	@Override
	public void selectInterval(int init, int end) {
		table.getSelectionModel().setSelectionInterval(init, end);
	}

}
