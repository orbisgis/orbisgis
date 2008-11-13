package org.orbisgis.editors.map.tools;

import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class EditionSelectionTool extends AbstractSelectionTool {

	@Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc);
	}

	@Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	@Override
	protected ILayer getLayer(MapContext mc) {
		return mc.getActiveLayer();
	}
}
