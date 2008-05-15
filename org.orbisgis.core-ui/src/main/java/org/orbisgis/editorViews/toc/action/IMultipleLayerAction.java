package org.orbisgis.editorViews.toc.action;

import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

/**
 * Interface to be implemented by the toc actions that deal with several layers
 * at the same time
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IMultipleLayerAction {

	/**
	 * executes the action on the selected layers
	 *
	 * @param mapContext
	 *            mapContext currently in TOC
	 * @param layers
	 *            Array of currently selected layers
	 */
	public void executeAll(MapContext mapContext, ILayer[] layers);

	/**
	 * Returns true if the action can be executed on the specified layers
	 *
	 * @param layer
	 * @return
	 */
	public boolean acceptsAll(ILayer[] layer);
}
