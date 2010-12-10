package org.orbisgis.core.ui.editors.map.tools;

import java.util.Observable;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class EditionSelectionTool extends AbstractSelectionTool {

	AbstractButton button;

	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	public void update(Observable o, Object arg) {
		PlugInContext.checkTool(this);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}

	protected ILayer getLayer(MapContext mc) {
		return mc.getActiveLayer();
	}

	public ImageIcon getImageIcon() {
		return OrbisGISIcon.SELECT;
	}
}
