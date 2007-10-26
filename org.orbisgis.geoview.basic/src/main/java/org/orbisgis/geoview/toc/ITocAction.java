package org.orbisgis.geoview.toc;

import org.orbisgis.geoview.layerModel.ILayer;

public interface ITocAction {

	void execute(Toc toc, ILayer resource);

	boolean acceptsSelectionCount(int selectionCount);

	boolean accepts(ILayer layer);

}
