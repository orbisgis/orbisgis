package org.orbisgis.core.ui.plugins.toc;


import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;

import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class SwitchToDraftPlugIn extends AbstractPlugIn {

	private boolean draft = false;

	@Override
	public boolean execute(PlugInContext context) {
		MapTransform mt = context.getMapEditor().getMapTransform();

		if (draft) {
			mt.switchToScreen();
		} else {
			mt.switchToDraft();
		}
		draft = !draft;

		mt.redraw();

		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();

		context.getFeatureInstaller().addPopupMenuItem(frame, this,
			new String[]{"toogle rendering mode"},
			Names.POPUP_TOC_LEGEND_GROUP, false,
			OrbisGISIcon.EDIT_LEGEND, wbContext);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isSelected() {
		return false;
	}
}
