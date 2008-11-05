package org.orbisgis.editorViews.toc;

import org.orbisgis.editors.table.Selection;
import org.orbisgis.layerModel.ILayer;

public class LayerSelection implements Selection {

	private ILayer layer;

	public LayerSelection(ILayer layer) {
		this.layer = layer;
	}

	@Override
	public int[] getSelection() {
		return layer.getSelection();
	}

	@Override
	public void setSelection(int[] indexes) {
		layer.setSelection(indexes);
	}

	@Override
	public void selectInterval(int init, int end) {
		int[] selection = new int[end - init + 1];
		for (int i = init; i <= end; i++) {
			selection[i - init] = i;
		}
		layer.setSelection(selection);
	}

	@Override
	public void clearSelection() {
		layer.setSelection(new int[0]);
	}

}
