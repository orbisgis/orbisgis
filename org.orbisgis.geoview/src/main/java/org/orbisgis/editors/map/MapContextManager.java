package org.orbisgis.editors.map;

import org.orbisgis.layerModel.MapContext;

public interface MapContextManager {

	/**
	 * Gets the active MapContext or null if the active editor does not edit a
	 * MapContext
	 *
	 * @return
	 */
	MapContext getActiveView();

}
