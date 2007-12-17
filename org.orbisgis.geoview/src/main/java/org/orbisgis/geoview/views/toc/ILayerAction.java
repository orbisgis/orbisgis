package org.orbisgis.geoview.views.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;

public interface ILayerAction {

	void executeAll(GeoView2D view, ILayer[] layers);

	void execute(GeoView2D view, ILayer resource);

	boolean acceptsSelectionCount(int selectionCount);

	boolean accepts(ILayer layer);

	boolean acceptsAll(ILayer[] layer);

}
