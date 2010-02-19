package org.orbisgis.plugins.core.ui.extensions.tools;

import java.util.Observable;

import javax.swing.AbstractButton;

import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.UpdatePlugInFactory;
import org.orbisgis.plugins.core.ui.editors.map.tool.ToolManager;

public class HelloTool extends Hello {

	AbstractButton button;

	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	public void update(Observable o, Object arg) {
		UpdatePlugInFactory.checkTool(this);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return true;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

}
