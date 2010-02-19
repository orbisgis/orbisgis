package org.orbisgis.core.ui.plugins.demo.extensions.tools;

import java.util.Observable;

import javax.swing.AbstractButton;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;

public class HelloTool extends Hello {

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
		return true;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

}
