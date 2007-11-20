package org.orbisgis.geoview.table;

import java.awt.Component;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;

public class ShowInTable implements org.orbisgis.geoview.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer instanceof VectorLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		Component comp = view.getView("org.orbisgis.geoview.Table");
		Table table = (Table) comp;
		table.setContents(((VectorLayer) resource).getDataSource());
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {

	}

}
