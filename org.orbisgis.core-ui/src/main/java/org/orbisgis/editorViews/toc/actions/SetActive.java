package org.orbisgis.editorViews.toc.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class SetActive implements ILayerAction {

	public boolean accepts(ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		mapContext.setActiveLayer(layer);
	}

}
