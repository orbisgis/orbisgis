/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.editorViews.toc;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerListenerAdapter;
import org.orbisgis.core.layerModel.SelectionEvent;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.SelectionListener;

public class LayerSelection implements Selection {

	private ILayer layer;
	private LayerListenerAdapter layerListener = null;

	public LayerSelection(ILayer layer) {
		this.layer = layer;
	}

	@Override
	public int[] getSelectedRows() {
		return layer.getSelection();
	}

	@Override
	public void setSelectedRows(int[] indexes) {
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

	@Override
	public void setSelectionListener(final SelectionListener listener) {
		if (layerListener != null) {
			removeSelectionListener(listener);
		}
		layerListener = new LayerListenerAdapter() {

			@Override
			public void selectionChanged(SelectionEvent e) {
				listener.selectionChanged();
			}
		};
		layer.addLayerListener(layerListener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		layer.removeLayerListener(layerListener);
		layerListener = null;
	}

}
