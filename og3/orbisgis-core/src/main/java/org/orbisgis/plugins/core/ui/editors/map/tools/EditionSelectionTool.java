package org.orbisgis.plugins.core.ui.editors.map.tools;

import java.util.Observable;

import javax.swing.AbstractButton;

import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.UpdatePlugInFactory;
import org.orbisgis.plugins.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class EditionSelectionTool extends AbstractSelectionTool {

	AbstractButton button;

	@Override
	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	@Override
	public void update(Observable o, Object arg) {
		UpdatePlugInFactory.checkTool(this);
	}

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

	public String getMouseCursor() {
		return Names.EDIT_SELECTION_ICON;
	}
}
